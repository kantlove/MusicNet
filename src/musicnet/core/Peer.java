package musicnet.core;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

/**
 * Created by mt on 12/2/2015.
 */
public final class Peer extends Thread {
    public PeerEvent peerDiscovered = new PeerEvent();
    public PeerEvent fileReceived = new PeerEvent();
    public PeerEvent filesListReceived = new PeerEvent();
    public PeerEvent searchResultsReceived = new PeerEvent();

    public PeerInfo info;
    public List<PeerInfo> knownHost = new CopyOnWriteArrayList<>();
    public Map<String, ArrayList<DataChunk>> receivedData = new ConcurrentHashMap<>();

    public List<SongFile> filesList;
    private String filesPath;

    /**
     * Init with nodes.txt
     * @param nodeFile nodes.txt path
     */
    public Peer(String nodeFile) throws IOException {
        knownHost.addAll(readNodes(nodeFile));
        filesList = Helper.getFilesInDirectory(this.filesPath);
        start();
    }

    private List<PeerInfo> readNodes(String path) throws IOException {
        final List<PeerInfo> rs = new ArrayList<>();
        class Int {int val = 0;}
        Int i = new Int();

        Files.lines(Paths.get(path)).forEach(s -> {
            PeerInfo info = null;
            try {
                if(i.val == 1) {
                    filesPath = s;
                }
                else {
                    info = PeerInfo.fromString(s);
                    if (i.val == 0)
                        this.info = info;
                    else {
                        rs.add(info);
                    }
                }
            }
            catch (UnknownHostException e) {
                e.printStackTrace();
            }
            i.val++;
        });
        return rs;
    }

    public void sendRequest(Request req) {
        req.sender = this.info;
        new SendThread(this, req);
    }

    public void sendRequest(Request req, List<SearchResult> searchResults) {
        req.sender = this.info;
        new SendThread(this, req, searchResults);
    }

    /**
     * Get the list of known hosts from other peers periodically
     */
    private void discover() {
        int interval = 5000;
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //Console.info("Discover request sent to " + Arrays.toString(knownHost.toArray()));
                sendRequest(new Request(RequestType.GetHosts, knownHost));
            }
        }, interval, interval);
    }

    /**
     * Listen continuously for incoming packet
     */
    private void listen() {
        System.out.printf("Listening on %s\n", this.info.address);
        try {
            DatagramSocket ds = new DatagramSocket(this.info.address.port);

            byte[] receiveData;
            while (true) {
                ds.setSoTimeout(2000);
                try {
                    receiveData = new byte[Config.RECEIVE_BASKET_SIZE]; // the size must be always > any possible datachunk size
                    DatagramPacket dp = new DatagramPacket(receiveData, receiveData.length);

                    ds.receive(dp); // after this, receiveData is populated

                    Object received = Serializer.deserialize(receiveData);
                    new ReceiveThread(this, received);
                }
                catch (ClassNotFoundException | IOException e) {
                    // ignore because we are looping forever
                    if(e instanceof SocketTimeoutException) {
                        startResending();
                    }
                }
            }
        }
        catch (SocketException ex) {
            System.out.printf("UDP Port %d is occupied.\n", this.info.address.port);
            System.exit(1);
        }
    }

    private void startResending() {
        for(Map.Entry<String, ArrayList<DataChunk>> entry : receivedData.entrySet()) {
            List<DataChunk> val = entry.getValue();
            if(val.size() == 0 || val.get(0) == null || Helper.countNonNull(val) < val.get(0).total) {
                Console.infof("A file is corrupted. Let the user click retry or resend request automatically?t.\n");
            }
        }
    }

    public List<SearchResult> search(String query) {
        return Helper.bulkMatch(query, this.filesList, 0.3);
    }

    @Override
    public void run() {
        discover();
        listen(); // IMPORTANT! This must be called last!
    }
}

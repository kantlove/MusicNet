package musicnet.core;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by mt on 12/2/2015.
 */
public final class Peer extends Thread {
    public PeerInfo info;
    public List<PeerInfo> knownHost = new CopyOnWriteArrayList<>();
    public Map<String, ArrayList<Byte[]>> receivedData = new ConcurrentHashMap<>();

    public Peer(PeerInfo info) {
        this.info = info;
        start();
    }

    /**
     * Init with nodes.txt
     * @param nodeFile nodes.txt path
     */
    public Peer(String nodeFile) throws IOException {
        knownHost.addAll(readNodes(nodeFile));
        start();
    }

    private List<PeerInfo> readNodes(String path) throws IOException {
        final List<PeerInfo> rs = new ArrayList<>();
        Files.lines(Paths.get(path)).forEach(s -> {
            PeerInfo info = null;
            try {
                info = PeerInfo.fromString(s);
                if (this.info == null)
                    this.info = info;
                else {
                    rs.add(info);
                }
            }
            catch (UnknownHostException e) {
                e.printStackTrace();
            }
        });
        return rs;
    }

    public void sendRequest(Request req) {
        req.sender = this.info;
        new SendThread(this, req);
    }

    /**
     * Listen continuously for incoming packet
     */
    private void listen() {
        System.out.printf("Listening on %s\n", this.info.address);
        try {
            DatagramSocket ds = new DatagramSocket(this.info.address.port);

            byte[] receiveData;
            int off = 0;
            while (true) {
                ds.setSoTimeout(2000);
                try {
                    receiveData = new byte[16384]; // the size must be always > any possible datachunk size
                    DatagramPacket dp = new DatagramPacket(receiveData, receiveData.length);

                    ds.receive(dp); // after this, receiveData is populated

                    Object received = Serializer.deserialize(receiveData);
                    //Console.log("Packet received!");

                    processReceivedPacket(received);

                    off = off + dp.getLength();
                }
                catch (ClassNotFoundException | IOException e) {
                    // ignore because we are looping forever
                }
            }
        }
        catch (SocketException ex) {
            System.out.printf("UDP Port %d is occupied.\n", this.info.address.port);
            System.exit(1);
        }
    }

    private void processReceivedPacket(Object received) throws IOException, ClassNotFoundException {
        DataChunk data = (DataChunk)received;
        addReceivedChunk(data);
    }

    private void addReceivedChunk(DataChunk chunk) throws IOException, ClassNotFoundException {
        if(!receivedData.containsKey(chunk.id)) {
            receivedData.put(chunk.id, new ArrayList<>());
        }

        List<Byte[]> list = receivedData.get(chunk.id);
        Helper.insert(list, chunk.data, chunk.sequence);

        /* Check if a file is complete */
        if(list.size() == chunk.total) {
            byte[] whole = Helper.mergeChunks(list);
            processCompleteFile(chunk.type, whole);
        }
    }

    private void processCompleteFile(Datatype type, byte []data) throws IOException, ClassNotFoundException {
        if(type == Datatype.Object) {
            //Console.log("Received an Object");
            List<PeerInfo> list = (List<PeerInfo>)Serializer.deserialize(data);
            addNewHosts(list);
        }
        /* Receive a request for sending something */
        else if (type == Datatype.Request) {
            //Console.log("Received a Request");
            Request request = (Request)Serializer.deserialize(data);

            addNewHosts(Arrays.asList(request.sender)); // add if this is a stranger

            request.type = RequestType.SendHosts;
            request.receivers = Arrays.asList(request.sender);
            sendRequest(request); // IMPORTANT! This line must be the last line
        }
    }

    /**
     * Get new hosts from a list and add them
     * @param newHosts list contains some hosts
     */
    private void addNewHosts(List<PeerInfo> newHosts) {
        String s = "";
        for(PeerInfo p : newHosts) {
            if(!knownHost.contains(p) && !this.info.equals(p)) {
                knownHost.add(p);
                s += " " + p.name;
            }
        }
        if(s.length() > 0) {
            Console.logf("%s: %s. ", this.info.name, Arrays.toString(knownHost.toArray()));
            Console.log("Discover " + s);
        }
    }

    @Override
    public void run() {
        listen();
    }
}

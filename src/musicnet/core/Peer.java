package musicnet.core;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
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
        new SendThread(req);
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
                    receiveData = new byte[1024];
                    DatagramPacket dp = new DatagramPacket(receiveData, receiveData.length);

                    ds.receive(dp); // after this, receiveData is populated

                    Console.log("Packet received!");
                    Object received = Serializer.deserialize(receiveData);

                    /* Receive a request for sending something */
                    if (received instanceof Request) {
                        new SendThread((Request) received);
                    }
                    /* Receive a chunk of data from a file */
                    else if (received instanceof DataChunk) {
                        DataChunk data = (DataChunk)received;
                        addReceivedChunk(data);
                    }

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

    private void addReceivedChunk(DataChunk chunk) {
        if(!receivedData.containsKey(chunk.id)) {
            receivedData.put(chunk.id, new ArrayList<>());
        }
        Helper.insert(receivedData.get(chunk.id), chunk.data, chunk.sequence);
    }

    @Override
    public void run() {
        listen();
    }
}

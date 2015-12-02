package musicnet.core;

import java.io.IOException;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by mt on 12/2/2015.
 */
public final class Peer extends Thread {
    public PeerInfo info;
    public List<PeerInfo> knownHost = new CopyOnWriteArrayList<>();

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
                if(this.info == null)
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
     * Listen forever
     */
    private void listen() {
        try
        {
            DatagramSocket ds = new DatagramSocket(this.info.address.port, this.info.address.ip);

            byte[] receiveData;
            int off = 0;
            while(true)
            {
                ds.setSoTimeout(2000);

                try {
                    receiveData = new byte[1024];
                    DatagramPacket dp = new DatagramPacket(receiveData, receiveData.length);

                    ds.receive(dp); // after this, receiveData is populated

                    Object received = Serializer.deserialize(receiveData);
                    if(received instanceof Request)
                        new SendThread((Request)received);

                    off = off + dp.getLength();
                }
                catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                    // ignore because we are looping forever
                }
            }
        }
        catch (SocketException ex) {
            System.out.printf("UDP Port %d is occupied.\n", this.info.address.port);
            System.exit(1);
        }
    }

    @Override
    public void run() {
        listen();
    }
}

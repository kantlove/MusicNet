package musicnet.core;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mt on 12/2/2015.
 */
public class SendThread extends Thread {
    private Request request;

    public SendThread(Request request) {
        this.request = request;
        start();
    }

    public void send() throws IOException {
        Address addr = new Address(request.params[0], Integer.valueOf(request.params[1]));
        DatagramSocket ds = new DatagramSocket();
        List<String> test = Arrays.asList("foo", "bar", "baz");
        byte[] full = Serializer.serialize(test), b;
        int remainLength = full.length, off = 0, chunkSize = 1024;

        while (remainLength >= chunkSize) {
            b = Arrays.copyOfRange(full, off, off + chunkSize);

            off = off + chunkSize;
            remainLength = remainLength - chunkSize;

            DatagramPacket dp = new DatagramPacket(b, b.length, addr.ip, addr.port);
            System.out.println("Sending data to " + b.length + " bytes to server, port 4567");

            ds.send(dp);
        }
        if (remainLength > 0) {
            b = Arrays.copyOfRange(full, off, off + chunkSize);
            System.out.println("The number of bytes will be read: " + remainLength);

            DatagramPacket dp = new DatagramPacket(b, b.length, addr.ip, addr.port);
            System.out.println("Sending data to " + b.length + " bytes to server.");
            ds.send(dp);
        }
    }

    @Override
    public void run() {
        try {
            send();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}

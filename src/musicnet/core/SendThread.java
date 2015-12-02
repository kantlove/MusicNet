package musicnet.core;

import java.io.File;
import java.io.FileInputStream;
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

        String path = "C:\\Users\\mt\\Desktop\\proj 2\\code\\4_TransferFile";
        File file = new File(path + "/send_files/song.mp3");

        FileInputStream f = new FileInputStream(file);
        byte[] full = new byte[(int)file.length()], b, c;

        int remainLength = full.length, off = 0, chunkSize = 1024;
        int sequence = 0, chunkCount = (int)Math.ceil(full.length * 1.0 / chunkSize);

        while (remainLength >= chunkSize) {
            b = Arrays.copyOfRange(full, off, off + chunkSize);
            DataChunk chunk = new DataChunk(sequence, chunkCount, String.valueOf(full.hashCode()), b);
            c = Serializer.serialize(chunk);

            off = off + chunkSize;
            remainLength = remainLength - chunkSize;

            DatagramPacket dp = new DatagramPacket(c, c.length, addr.ip, addr.port);
            System.out.println("Sending data to " + c.length + " bytes to server, port 4567");

            ds.send(dp);
            sequence++;
        }
        if (remainLength > 0) {
            b = Arrays.copyOfRange(full, off, off + chunkSize);
            DataChunk chunk = new DataChunk(sequence, String.valueOf(full.hashCode()), b);
            c = Serializer.serialize(chunk);
            System.out.println("The number of bytes will be read: " + remainLength);

            DatagramPacket dp = new DatagramPacket(c, c.length, addr.ip, addr.port);
            System.out.println("Sending data to " + c.length + " bytes to server.");
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

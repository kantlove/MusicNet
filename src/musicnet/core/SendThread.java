package musicnet.core;

import javax.xml.crypto.Data;
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
    private Peer parent;

    public SendThread(Peer parent, Request request) {
        this.parent = parent;
        this.request = request;
        start();
    }

    /**
     * Base on request type, prepare the correct data that we need to send
     */
    private byte[] prepareData() throws IOException {
        switch (request.type) {
            case SendHosts:
                return Serializer.serialize(parent.knownHost);
            case GetHosts:
            default:
                return Serializer.serialize(request);
        }
    }

    private DataChunk createChunk(int sequence, int chunkCount, String dataId, byte[] b) {
        DataChunk chunk = new DataChunk(sequence, chunkCount, dataId, b);
        switch (request.type) {
            case SendHosts:
                chunk.type = Datatype.Object;
                break;
            case GetHosts:
                chunk.type = Datatype.Request;
                break;
        }
        return chunk;
    }

    private void send() throws IOException {
        for(PeerInfo p : request.receivers) {
            send(p.address);
        }
    }

    private void send(Address receiver) throws IOException {
        DatagramSocket ds = new DatagramSocket();

        byte[] full = prepareData(), b, c;
        String dataId = String.valueOf(Arrays.hashCode(full));

        int fileLength = full.length;
        int remainLength = fileLength, off = 0, chunkSize = 1024;
        int sequence = 0, chunkCount = (int)Math.ceil(fileLength * 1.0 / chunkSize);

        while (remainLength >= chunkSize) {
            b = Arrays.copyOfRange(full, off, off + chunkSize);
            DataChunk chunk = createChunk(sequence, chunkCount, dataId, b);
            c = Serializer.serialize(chunk);

            DatagramPacket dp = new DatagramPacket(c, c.length, receiver.ip, receiver.port);
            //Console.log("Sending " + c.length + " bytes to server, port " + receiver.port);

            ds.send(dp);
            
            sequence++;
            off = off + chunkSize;
            remainLength = remainLength - chunkSize;
        }
        if (remainLength > 0) {
            b = Arrays.copyOfRange(full, off, off + remainLength);
            DataChunk chunk = createChunk(sequence, chunkCount, dataId, b);
            c = Serializer.serialize(chunk);

            //Console.log("The number of bytes will be read: " + remainLength);
            DatagramPacket dp = new DatagramPacket(c, c.length, receiver.ip, receiver.port);
            //Console.log("Sending " + c.length + " bytes to server.");

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

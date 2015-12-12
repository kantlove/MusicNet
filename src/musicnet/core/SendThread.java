package musicnet.core;

import musicnet.Client;
import musicnet.model.Address;
import musicnet.model.PeerInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by mt on 12/2/2015.
 */
public class SendThread extends Thread {
    private Client client;
    private Request request;
    private Object data;
    private File file;
    private double percent, uploadedAmount;
    private Timer statusTimer = new Timer();

    public SendThread(Client client, Request request) {
        this.client = client;
        this.request = request;
        start();
    }

    public SendThread(Client client, Request request, Object data) {
        this.client = client;
        this.request = request;
        this.data = data;
        start();
    }

    public SendThread(Client client, Request request, File file) {
        this.client = client;
        this.request = request;
        this.file = file;
        start();
    }

    /**
     * Base on request type, prepare the correct data that we need to send
     */
    private byte[] prepareData() throws IOException {
        switch (request.type) {
            case SendHosts:
            case SendFilesList:
            case SearchResult:
                assert data != null;
                return Serializer.serialize(data);
            case SendFile:
                assert file != null;
                FileInputStream stream = new FileInputStream(file);
                byte[] b = new byte[(int) file.length()];
                stream.read(b);
                return b;
            default:
                return Serializer.serialize(request);
        }
    }

    private byte[] prepareChunk(int sequence, int chunkCount, String dataId, byte[] b) throws IOException {
        DataChunk chunk = createChunk(sequence, chunkCount, dataId, b);
        return Serializer.serialize(chunk);
    }

    private DataChunk createChunk(int sequence, int chunkCount, String dataId, byte[] b) {
        DataChunk chunk = new DataChunk(sequence, chunkCount, dataId, b);
        switch (request.type) {
            case SendHosts:
            case SendFilesList:
            case SearchResult:
                chunk.type = Datatype.Object;
                break;
            case SendFile:
                chunk.type = Datatype.File;
                break;
            case GetHosts:
            case GetFile:
            case GetFilesList:
            case Search:
                chunk.type = Datatype.Request;
                break;
        }
        return chunk;
    }

    private void send() throws IOException {
        for (PeerInfo p : request.receivers) {
            send(p.address);
        }
    }

    private void send(Address receiver) throws IOException {
        DatagramSocket ds = new DatagramSocket();

        byte[] full = prepareData(), b, c;
        String dataId = String.valueOf(Arrays.hashCode(full));

        int fileLength = full.length;
        int remainLength = fileLength, off = 0, chunkSize = Config.CHUNK_SIZE;
        int sequence = 0, chunkCount = (int) Math.ceil(fileLength * 1.0 / chunkSize);

        while (remainLength >= chunkSize) {
            b = Arrays.copyOfRange(full, off, off + chunkSize);
            c = prepareChunk(sequence, chunkCount, dataId, b);

            DatagramPacket dp = new DatagramPacket(c, c.length, receiver.ip, receiver.port);
            //Console.log("Sending " + c.length + " bytes to server, port " + receiver.port);

            ds.send(dp);

            sequence++;
            off += chunkSize;
            remainLength -= chunkSize;
            if (request.type == RequestType.SendFile) {
                percent = (sequence + 1.0) / chunkCount * 100;
                uploadedAmount += chunkSize;
            }

            /* Avoid sending too fast by adding a small delay */
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (remainLength > 0) {
            b = Arrays.copyOfRange(full, off, off + remainLength);
            c = prepareChunk(sequence, chunkCount, dataId, b);

            //Console.log("The number of bytes will be read: " + remainLength);
            DatagramPacket dp = new DatagramPacket(c, c.length, receiver.ip, receiver.port);
            //Console.log("Sending " + c.length + " bytes to server.");

            ds.send(dp);
        }
        if (request.type == RequestType.SendFile)
            Console.info("Complete sending file.");
        statusTimer.cancel();
    }

    private void startStatusTimer() {
        int interval = 1000;
        statusTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (request.type == RequestType.SendFile)
                    Console.logf("Uploaded %.1f%% at %.1fkB/s.\n", percent, uploadedAmount / 1024);
                uploadedAmount = 0;
            }
        }, interval, interval);
    }

    @Override
    public void run() {
        try {
            startStatusTimer();
            send();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

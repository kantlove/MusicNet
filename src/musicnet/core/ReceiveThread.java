package musicnet.core;

import musicnet.Client;
import musicnet.model.PeerInfo;
import musicnet.model.SearchResult;
import musicnet.model.SongInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by mt on 12/5/2015.
 */
public class ReceiveThread extends Thread {
    private Client client;
    private Object receivedObj;

    public ReceiveThread(Client client, Object receivedObj) {
        this.client = client;
        this.receivedObj = receivedObj;
        start();
    }

    private void processReceivedPacket() throws IOException, ClassNotFoundException {
        DataChunk data = (DataChunk) receivedObj;
        addReceivedChunk(data);
    }

    private void addReceivedChunk(DataChunk chunk) throws IOException, ClassNotFoundException {
        if (!client.receivedData.containsKey(chunk.id)) {
            client.receivedData.put(chunk.id, new ArrayList<>());
        }

        List<DataChunk> list = client.receivedData.get(chunk.id);
        Helper.insert(list, chunk, chunk.sequence);

        /* Check if a file is complete */
        if (Helper.countNonNull(list) == chunk.total) {
            byte[] whole = Helper.mergeChunks(list);
            processCompleteFile(chunk.type, whole);
            client.receivedData.remove(chunk.id);
        }
    }

    private void processCompleteFile(Datatype type, byte[] data) throws IOException, ClassNotFoundException {
        if (type == Datatype.Object) {
            List<?> list = (List<?>) Serializer.deserialize(data);
            if (list.size() > 0) {
                Type elementType = Helper.getElementType(list);
                if (elementType == PeerInfo.class) {
                    client.updatePeers((List<PeerInfo>) list);
                } else if (elementType == SongInfo.class) {
                    client.updateFiles((List<SongInfo>) list);
                } else if (elementType == SearchResult.class) {
                    client.updateResult((List<SearchResult>) list);
                }
            }
        }
        /* Receive a request for sending something */
        else if (type == Datatype.Request) {
            Console.log("Received a Request");
            Request request = (Request) Serializer.deserialize(data);
            client.updatePeers(Collections.singletonList(request.sender));

            switch (request.type) {
                case GetHosts:
                    client.sendHosts(request.sender);
                    break;
                case GetFile:
                    client.sendFile(request.sender, request.params);
                    Console.info("Send file request received.");
                    break;
                case GetFilesList:
                    client.sendFilesList(request.sender);
                    Console.info("Send files list request received.");
                    break;
                case Search:
                    client.sendSearch(request.sender, request.params);
                    Console.info("Search request received.");
                    break;
            }
        }
        /* Receive a file */
        else if (type == Datatype.File) {
            try {
                String hash = Helper.getDataCheckSum(data);
                File file = new File(client.getDirectory(), hash);
                FileOutputStream stream = new FileOutputStream(file);
                stream.write(data);
                stream.flush();
                stream.close();

                client.fileReceived(file);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        try {
            processReceivedPacket();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

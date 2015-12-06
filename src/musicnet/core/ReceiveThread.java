package musicnet.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by mt on 12/5/2015.
 */
public class ReceiveThread extends Thread {
    private Peer parent;
    private Object receivedObj;

    public ReceiveThread(Peer parent, Object receivedObj) {
        this.parent = parent;
        this.receivedObj = receivedObj;
        start();
    }

    private void processReceivedPacket() throws IOException, ClassNotFoundException {
        DataChunk data = (DataChunk)receivedObj;
        addReceivedChunk(data);
    }

    private void addReceivedChunk(DataChunk chunk) throws IOException, ClassNotFoundException {
        if(!parent.receivedData.containsKey(chunk.id)) {
            parent.receivedData.put(chunk.id, new ArrayList<>());
        }

        List<DataChunk> list = parent.receivedData.get(chunk.id);
        Helper.insert(list, chunk, chunk.sequence);

        /* Check if a file is complete */
        if(Helper.countNonNull(list) == chunk.total) {
            byte[] whole = Helper.mergeChunks(list);
            processCompleteFile(chunk.type, whole);
        }
    }

    private void processCompleteFile(Datatype type, byte []data) throws IOException, ClassNotFoundException {
        if(type == Datatype.Object) {
            List<?> list = (List<?>)Serializer.deserialize(data);
            if(list.size() > 0) {
                Type elementType = Helper.getElementType(list);
                if(elementType == PeerInfo.class) {
                    addNewHosts((List<PeerInfo>)list);
                }
                else if(elementType == SongFile.class) {
                    parent.filesListReceived.invoke(parent, list);
                }
                else if(elementType == SearchResult.class) {
                    parent.searchResultsReceived.invoke(parent, list);
                }
            }
        }
        /* Receive a request for sending something */
        else if (type == Datatype.Request) {
            //Console.log("Received a Request");
            Request request = (Request)Serializer.deserialize(data);
            addNewHosts(Arrays.asList(request.sender)); // add if this is a stranger
            List<SearchResult> searchResults = null;

            switch(request.type) {
                case GetHosts:
                    request.type = RequestType.SendHosts;
                    break;
                case GetFile:
                    request.type = RequestType.SendFile;
                    Console.info("Send file request received.");
                    break;
                case GetFilesList:
                    request.type = RequestType.SendFilesList;
                    Console.info("Send files list request received.");
                    break;
                case Search:
                    request.type = RequestType.SearchResult;
                    Console.info("Search request received.");
                    assert request.params != null && request.params.length > 0 : "Invalid search parameters";
                    searchResults = parent.search(request.params[0]);
                    break;
            }
            request.receivers = Arrays.asList(request.sender);

            // IMPORTANT! This line must be the last line.
            if(searchResults == null)
                parent.sendRequest(request);
            else
                parent.sendRequest(request, searchResults);
        }
        /* Receive a file */
        else if(type == Datatype.File) {
            File file = new File("D:\\My Document\\Java projects\\MusicNet\\data_receive\\song.mp3");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data);
            fos.flush();
            fos.close();

            parent.fileReceived.invoke(parent, file);
        }
    }

    /**
     * Get new hosts from a list and add them
     * @param newHosts list contains some hosts
     */
    private void addNewHosts(List<PeerInfo> newHosts) {
        List<PeerInfo> validHosts = new ArrayList<>(); // real new hosts

        for(PeerInfo p : newHosts) {
            if(!parent.knownHost.contains(p) && !parent.info.equals(p)) {
                parent.knownHost.add(p);

                validHosts.add(p);
            }
        }
        if(validHosts.size() > 0) {
            parent.peerDiscovered.invoke(parent, validHosts);
        }
    }

    @Override
    public void run() {
        try {
            processReceivedPacket();
        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

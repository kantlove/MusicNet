package musicnet;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import musicnet.core.*;
import musicnet.core.Console;
import musicnet.model.PeerInfo;
import musicnet.model.SongFile;
import musicnet.model.SongInfo;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Client extends Thread {
    public final ObservableList<PeerInfo> peers;
    public final ObservableList<SongFile> songs;
    public final ObservableList<SongInfo> infos;
    public Map<String, List<DataChunk>> receivedData = new ConcurrentHashMap<>();
    private PeerInfo info;
    private File directory;
    private File nodesFile;

    public Client() {
        peers = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
        songs = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
        infos = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
    }

    public boolean isReady() {
        return peers.size() >= 2;
    }

    public File getDirectory() {
        if (directory == null) {
            directory = new File("C:\\Users\\Quan\\Desktop\\Data1");
        }
        return directory;
    }

    public void setDirectory(File directory) {
        this.directory = directory;
    }

    public File getNodesFile() {
        if (nodesFile == null) {
            nodesFile = new File(getDirectory().getAbsolutePath() + "\\nodes.txt");
        }
        return nodesFile;
    }

    public void loadPeers() {
        peers.clear();
        File file = getNodesFile();
        if (!file.exists()) return;

        try {
            Scanner scanner = new Scanner(new FileReader(file));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] tokens = line.split(" ");
                if (tokens.length >= 2) {
                    peers.add(new PeerInfo(tokens[0], tokens[1]));
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean savePeers() {
        File file = getNodesFile();
        try {
            PrintWriter writer = new PrintWriter(file);
            for (PeerInfo peer : peers)
                writer.println(peer.getName() + " " + peer.getAddress());
            writer.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void updateSongs() {
        File[] files = getDirectory().listFiles((dir, name) -> {
            return name.toLowerCase().endsWith(".mp3");
        });
        if (files == null) {
            songs.clear();
        } else {
            SongFile[] newSongs = new SongFile[files.length];
            boolean[] add = new boolean[files.length];
            for (int i = 0; i < newSongs.length; ++i)
                if (files[i].isFile()) {
                    try {
                        newSongs[i] = new SongFile(files[i]);
                        add[i] = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            for (int i = 0; i < songs.size(); ) {
                SongFile song = songs.get(i);

                boolean remove = true;
                for (int j = 0; j < newSongs.length; ++j)
                    if (song.equals(newSongs[j])) {
                        add[j] = false;
                        remove = false;
                    }
                if (remove) songs.remove(i);
                else ++i;
            }

            for (int i = 0; i < newSongs.length; ++i)
                if (add[i]) songs.add(newSongs[i]);
        }
    }

    public void sendRequest(Request req) {
        req.sender = this.info;
        new SendThread(this, req);
    }

    public void sendRequest(Request req, Object data) {
        req.sender = this.info;
        new SendThread(this, req, data);
    }

    public void sendRequest(Request req, File file) {
        req.sender = this.info;
        new SendThread(this, req, file);
    }

    public void updatePeers(List<PeerInfo> list) {

    }

    public void updateInfos(List<SongInfo> list) {

    }

    public void updateResult(List<SearchResult> list) {

    }

    public void fileReceived(File file) {

    }

    public List<SearchResult> search(String query) {
        return null;
    }

    public void sendHosts(PeerInfo sender) {

    }

    public void sendFile(PeerInfo sender, String[] params) {

    }

    public void sendFilesList(PeerInfo sender) {

    }

    public void sendSearch(PeerInfo sender, String[] params) {
        assert params != null && params.length > 0 : "Invalid search parameters";
    }

    @Override
    public void run() {
        initialize();
        discover();
        listen(); // IMPORTANT! This must be called last!
    }

    private void initialize() {
        info = peers.get(0);
        peers.remove(0);
        updateSongs();
    }

    private void discover() {
        int interval = 5000;
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Console.info("Discover request sent to " + Arrays.toString(peers.toArray()));
                sendRequest(new Request(RequestType.GetHosts, peers));
            }
        }, interval, interval);
    }

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
                } catch (ClassNotFoundException | IOException e) {
                    // ignore because we are looping forever
                    if (e instanceof SocketTimeoutException) {
                        startResending();
                    }
                }
            }
        } catch (SocketException ex) {
            System.out.printf("UDP Port %d is occupied.\n", this.info.address.port);
            System.exit(1);
        }
    }

    private void startResending() {
        for (Map.Entry<String, List<DataChunk>> entry : receivedData.entrySet()) {
            List<DataChunk> val = entry.getValue();
            if (val.size() == 0 || val.get(0) == null || Helper.countNonNull(val) < val.get(0).total) {
                Console.infof("A file is corrupted. Let the user click retry or resend request automatically?t.\n");
            }
        }
    }


}

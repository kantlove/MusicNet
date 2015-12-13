package musicnet;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import musicnet.controller.TopBarController;
import musicnet.core.*;
import musicnet.core.Console;
import musicnet.model.*;

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
    public final ObservableList<SongInfo> files;
    public final ObservableList<SearchResult> results;
    public final ObservableList<DownloadItem> downloadItems;
    public final ObservableList<UploadItem> uploadItems;
    public Map<String, List<DataChunk>> receivedData = new ConcurrentHashMap<>();
    private PeerInfo info;
    private TopBarController topBar;
    private File directory;
    private File playFile;

    public Client() {
        peers = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
        songs = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
        files = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
        results = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
        downloadItems = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
        uploadItems = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
    }

    public boolean isReady() {
        return peers.size() > 0;
    }

    public File getDirectory() {
        if (directory == null) {
            directory = new File("C:\\Users\\Quan\\Desktop\\Data2");
        }
        return directory;
    }

    public void setDirectory(File directory) {
        this.directory = directory;
    }

    public File getNodesFile() {
        return new File(getDirectory().getAbsolutePath() + "\\nodes.txt");
    }

    public void setTopBar(TopBarController topBar) {
        this.topBar = topBar;
    }

    public File getPlayFile() {
        if (playFile == null) {
            if (songs.size() > 0)
                playFile = songs.get(0).file;
        }
        return playFile;
    }

    public void setPlayFile(File file) {
        playFile = file;
    }

    public void play() {
        if (topBar != null)
            topBar.play();
    }

    public List<PeerInfo> getPeers() {
        return new ArrayList<>(peers);
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

    public void showFilesList(PeerInfo peer) {
        files.clear();
        Request req = new Request(RequestType.GetFilesList, Collections.singletonList(peer));
        sendRequest(req);
    }

    public void search(String query) {
        results.clear();
        results.addAll(Helper.bulkMatch(query, songs, 0.3));
        Request req = new Request(RequestType.Search, getPeers(), query);
        sendRequest(req);
    }

    public void download(PeerInfo peer, SongInfo song) {
        for (SongFile file : songs)
            if (file.getHash().equals(song.getHash()))
                return;
        for (DownloadItem x : downloadItems)
            if (x.song.getHash().equals(song.getHash()))
                return;

        downloadItems.add(new DownloadItem(peer, song));
        Request req = new Request(RequestType.GetFile, Collections.singletonList(peer), song.getHash());
        sendRequest(req);
    }

    public void updatePeers(List<PeerInfo> list) {
        for (PeerInfo newPeer : list) {
            if (newPeer.equals(info)) continue;
            boolean add = true;
            for (PeerInfo peer : peers)
                if (peer.equals(newPeer)) {
                    add = false;
                    break;
                }
            if (add) peers.add(newPeer);
        }
    }

    public void updateFiles(List<SongInfo> list) {
        files.addAll(list);
    }

    public void updateResult(List<SearchResult> list) {
        for (SearchResult x : list) {
            boolean add = true;
            for (SearchResult y : results)
                if (x.equals(y)) {
                    add = false;
                    break;
                }
            if (add) results.add(x);
        }
    }

    public void fileReceived(File file) {
        for (int i = 0; i < downloadItems.size(); ++i) {
            DownloadItem item = downloadItems.get(i);
            if (item.song.getHash().equals(file.getName())) {
                if (file.renameTo(new File(file.getParentFile(), item.song.getName()))) {
                    updateSongs();
                } else {
                    if (!file.delete())
                        file.deleteOnExit();
                }
                downloadItems.remove(i);
                break;
            }
        }
    }

    public void sendHosts(PeerInfo peer) {
        List<PeerInfo> list = new ArrayList<>();
        list.add(info);
        list.addAll(peers);

        Request req = new Request(RequestType.SendHosts, Collections.singletonList(peer));
        sendRequest(req, list);
    }

    public void sendFile(PeerInfo peer, String[] params) {
        assert params != null && params.length > 1 : "Invalid params";
        String hash = params[0];
        for (SongFile song : songs)
            if (song.getHash().equals(hash)) {
                Request req = new Request(RequestType.SendFile, Collections.singletonList(peer), hash);
                sendRequest(req, song.file);
                uploadItems.add(new UploadItem(peer, new SongInfo(song)));
                break;
            }
    }

    public void sendFilesList(PeerInfo peer) {
        List<SongFile> all = getAllSharedSong();
        List<SongInfo> list = new ArrayList<>();
        for (SongFile song : all)
            list.add(new SongInfo(song));

        Request req = new Request(RequestType.SendFilesList, Collections.singletonList(peer));
        sendRequest(req, list);
    }

    public void sendSearch(PeerInfo peer, String[] params) {
        assert params != null && params.length > 0 : "Invalid search parameters";
        String query = params[0];
        List<SearchResult> results = Helper.bulkMatch(query, getAllSharedSong(), 0.3);

        Request req = new Request(RequestType.SearchResult, Collections.singletonList(peer));
        sendRequest(req, results);
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
                sendRequest(new Request(RequestType.GetHosts, getPeers()));
            }
        }, interval, interval);
    }

    private void listen() {
        System.out.printf("Listening on %s\n", this.info.address);
        try {
            DatagramSocket ds = new DatagramSocket(this.info.address.port);

            byte[] receiveData;
            while (true) {
                ds.setSoTimeout(10000);
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

    private List<SongFile> getAllSharedSong() {
        List<SongFile> list = new ArrayList<>();
        for (SongFile song : songs)
            if (song.getShared())
                list.add(song);
        return list;
    }
}

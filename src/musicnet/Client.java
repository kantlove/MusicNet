package musicnet;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import musicnet.model.PeerInfo;
import musicnet.model.SongFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Scanner;

public class Client extends Thread {
    public final ObservableList<PeerInfo> peers;
    public final ObservableList<SongFile> songs;
    private PeerInfo info;
    private File directory;
    private File nodesFile;

    public Client() {
        peers = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
        songs = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
    }

    public boolean isReady() {
        return peers.size() >= 2;
    }

    public File getDirectory() {
        if (directory == null) {
            directory = new File("C:\\Users\\Quan\\Desktop\\Data");
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

    public void savePeers() {
        File file = getNodesFile();
        try {
            PrintWriter writer = new PrintWriter(file);
            for (PeerInfo peer : peers)
                writer.println(peer.getName() + " " + peer.getAddress());
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
                    newSongs[i] = new SongFile(files[i]);
                    add[i] = true;
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

    }

    private void listen() {

    }
}

package musicnet;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import musicnet.model.PeerInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Scanner;

public class Client extends Thread {
    public final ObservableList<PeerInfo> peers;
    private File directory;
    private File nodesFile;

    public Client() {
        peers = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
    }

    public File getDirectory() {
        if (directory == null) {
            directory = new File("");
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

    @Override
    public void run() {
        initialize();
    }

    private void initialize() {

    }
}

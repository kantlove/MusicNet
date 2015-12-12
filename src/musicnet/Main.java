package musicnet;


import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import musicnet.core.Peer;
import musicnet.util.FXMLLoaderEx;
import musicnet.core.*;

import javax.naming.directory.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Main extends Application {
    private Stage primaryStage;
    private BorderPane layoutRoot;
    private Client client;

    public Client getClient() {
        return client;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("MusicNet");

        client = new Client();
        initLayoutRoot();
        showWelcomeView();
    }

    private void initLayoutRoot() throws IOException {
        FXMLLoaderEx loader = new FXMLLoaderEx();
        loader.setLocation(getClass().getResource("view/layout_root.fxml"));
        layoutRoot = loader.load();
        loader.getController().setMain(this);
        primaryStage.setScene(new Scene(layoutRoot));
        primaryStage.show();
    }

    private void showWelcomeView() throws IOException {
        FXMLLoaderEx loader = new FXMLLoaderEx();
        loader.setLocation(getClass().getResource("view/welcome.fxml"));
        layoutRoot.setCenter(loader.load());
        loader.getController().setMain(this);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void showHomeView() throws IOException {
        FXMLLoaderEx loader = new FXMLLoaderEx();
        loader.setLocation(getClass().getResource("view/library.fxml"));
        Node tmp = layoutRoot.getBottom();
        layoutRoot.setCenter(loader.load());
        loader.getController().setMain(this);
    }

//    static Peer peer;
//    public static void main(String[] args) throws IOException {
//        // initialize
//        peer = new Peer("D:\\My Document\\Java projects\\MusicNet\\data\\nodesA.txt");
//        // setup event handlers
//        peer.peerDiscovered.subscribe(Main::PeerDiscoveredHandler);
//        peer.fileReceived.subscribe(Main::FileReceivedHandler);
//        peer.filesListReceived.subscribe(Main::FilesListReceivedHandler);
//        peer.searchResultsReceived.subscribe(Main::SearchResultsReceivedHandler);
//
//        // a test sending request
//        if(peer.info.name.equals("A")) {
//            Timer timer = new Timer();
//            timer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    //peer.sendRequest(new Request(RequestType.GetFile, peer.knownHost, "song.mp3"));
//                    //peer.sendRequest(new Request(RequestType.GetFilesList, peer.knownHost));
//                    peer.sendRequest(new Request(RequestType.Search, peer.knownHost, "noo phuc thinh ngay mua tan"));
//                    peer.sendRequest(new Request(RequestType.Search, peer.knownHost, "two hearst"));
//                }
//            }, 10000);
//        }
//    }
//
//    private static void PeerDiscoveredHandler(Object sender, Object arg) {
//        List<PeerInfo> newHosts = (List<PeerInfo>)arg;
//        Console.log("Discover " + Arrays.toString(newHosts.toArray()));
//    }
//
//    private static void FileReceivedHandler(Object sender, Object arg) {
//        Console.info("File received.");
//    }
//
//    private static void FilesListReceivedHandler(Object sender, Object arg) {
//        Console.info("Files list received");
//        Console.info(Arrays.toString(((List<SongFile>)arg).toArray()));
//    }
//
//    private static void SearchResultsReceivedHandler(Object sender, Object arg) {
//        Console.info("Search results received");
//        Console.info(Arrays.toString(((List<SearchResult>)arg).toArray()));
//    }
}

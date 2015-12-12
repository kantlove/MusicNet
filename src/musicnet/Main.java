package musicnet;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import musicnet.util.FXMLLoaderEx;

import java.io.IOException;

public class Main extends Application {
    public Stage primaryStage;
    public Client client;
    private BorderPane layoutRoot;

    public static void main(String[] args) {
        launch(args);
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

    private void showTopBar() throws IOException {
        FXMLLoaderEx loader = new FXMLLoaderEx();
        loader.setLocation(getClass().getResource("view/topbar.fxml"));
        layoutRoot.setTop(loader.load());
        loader.getController().setMain(this);
    }

    private Tab loadTab(String name, String resource) throws IOException {
        FXMLLoaderEx loader = new FXMLLoaderEx();
        loader.setLocation(getClass().getResource(resource));
        Tab tab = new Tab(name, loader.load());
        loader.getController().setMain(this);
        return tab;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("MusicNet");

        client = new Client();
        initLayoutRoot();
        showWelcomeView();
    }

    public void startApplication() throws IOException {
        client.start();

        TabPane tabPane = new TabPane();
        tabPane.getTabs().add(loadTab("Library", "view/library.fxml"));
        tabPane.getTabs().add(loadTab("Explorer", "view/explorer.fxml"));
        tabPane.getTabs().add(loadTab("Search", "view/search.fxml"));
        layoutRoot.setCenter(tabPane);
        showTopBar();
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

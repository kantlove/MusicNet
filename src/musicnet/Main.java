package musicnet;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import musicnet.core.Peer;
import musicnet.util.FXMLLoaderEx;
import musicnet.core.*;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Main extends Application {
    private Stage primaryStage;
    private BorderPane layoutRoot;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("MusicNet");

        initLayoutRoot();
    }

    private void initLayoutRoot() throws IOException {
        FXMLLoaderEx loader = new FXMLLoaderEx();
        loader.setLocation(getClass().getResource("view/layout_root.fxml"));
        loader.getController().setMain(this);
        layoutRoot = loader.load();
        primaryStage.setScene(new Scene(layoutRoot));
        primaryStage.show();
    }

    static Peer peer;

    public static void main(String[] args) throws IOException {
        //Peer p = new Peer(new PeerInfo("A", new Address("127.0.0.1", 2015)));
        peer = new Peer("D:\\My Document\\Java projects\\MusicNet\\data\\nodesA.txt");
        peer.peerDiscovered.subscribe(Main::PeerDiscoveredHandler);

        if(peer.info.name.equals("A")) {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    peer.sendRequest(new Request(RequestType.GetFile, peer.knownHost, "song.mp3"));
                }
            }, 10000);
        }
    }

    private static void PeerDiscoveredHandler(Object sender, Object arg) {
        List<PeerInfo> newHosts = (List<PeerInfo>)arg;
        Console.log("Discover " + Arrays.toString(newHosts.toArray()));
    }
}

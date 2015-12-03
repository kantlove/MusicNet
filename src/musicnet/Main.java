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

    public static void main(String[] args) throws IOException {
        //Peer p = new Peer(new PeerInfo("A", new Address("127.0.0.1", 2015)));
        Peer p = new Peer("D:\\My Document\\Java projects\\MusicNet\\data\\nodesD.txt");

        /* Get the list of known hosts from other peers periodically */
        int interval = 5000;
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Console.info("Discover request sent to " + Arrays.toString(p.knownHost.toArray()));
                p.sendRequest(new Request(RequestType.GetHosts, p.knownHost));
            }
        }, interval, interval);
    }
}

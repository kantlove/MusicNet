package musicnet;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import musicnet.core.Address;
import musicnet.core.Peer;
import musicnet.core.PeerInfo;
import musicnet.util.FXMLLoaderEx;

import java.io.IOException;
import java.net.UnknownHostException;

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

    public static void main(String[] args) throws UnknownHostException {
        Peer p = new Peer(new PeerInfo("A", new Address("127.0.0.1", 2015)));
    }
}

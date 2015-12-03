package musicnet;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import musicnet.core.*;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("view/sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
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

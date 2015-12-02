package musicnet;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import musicnet.core.Address;
import musicnet.core.Peer;
import musicnet.core.PeerInfo;
import musicnet.core.Request;

import java.net.UnknownHostException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("view/sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }


    public static void main(String[] args) throws UnknownHostException {
        Peer p = new Peer(new PeerInfo("A", new Address("127.0.0.1", 2015)));
//        for(int i = 0; i < 10; ++i) {
//            Request req = new Request();
//            req.params = new String[]{"192.168.0.102", "2015"};
//            p.sendRequest(req);
//        }
    }
}

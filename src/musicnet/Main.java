package musicnet;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
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

        primaryStage.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue)
                client.updateSongs();
        });
    }
}

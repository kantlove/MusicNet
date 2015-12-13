package musicnet.controller;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import musicnet.Main;
import musicnet.model.DownloadItem;
import musicnet.model.UploadItem;
import musicnet.util.FXMLLoaderEx;

import java.io.File;
import java.io.IOException;

public class TopBarController extends BaseController {
    private final String PLAY = "Play";
    private final String PAUSE = "Pause";

    public Button buttonMedia;
    public Button buttonDown;
    public Button buttonUp;
    public Label labelName;
    private MediaPlayer player;

    @Override
    public void setMain(Main main) {
        super.setMain(main);
        getClient().setTopBar(this);
        getClient().downloadItems.addListener((ListChangeListener<DownloadItem>) c
                -> Platform.runLater(() -> buttonDown.setText("Download (" + getClient().downloadItems.size() + ")")));
        getClient().uploadItems.addListener((ListChangeListener<UploadItem>) c
                -> Platform.runLater(() -> buttonUp.setText("Upload (" + getClient().uploadItems.size() + ")")));
    }

    public void showDownloadDialog(ActionEvent event) throws IOException {
        FXMLLoaderEx loader = new FXMLLoaderEx();
        loader.setLocation(getMain().getClass().getResource("view/download.fxml"));

        Stage dialog = new Stage();
        dialog.setTitle("Downloading items");
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setScene(new Scene(loader.load()));
        loader.getController().setMain(getMain());
        dialog.show();
    }

    public void showUploadDialog(ActionEvent event) throws IOException {
        FXMLLoaderEx loader = new FXMLLoaderEx();
        loader.setLocation(getMain().getClass().getResource("view/upload.fxml"));

        Stage dialog = new Stage();
        dialog.setTitle("Uploading items");
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setScene(new Scene(loader.load()));
        loader.getController().setMain(getMain());
        dialog.show();
    }

    public void onButtonMediaClicked(ActionEvent event) {
        updateMedia();
    }

    public void updateMedia() {
        if (buttonMedia.getText().equals(PLAY)) {
            if (player == null) {
                File file = getClient().getPlayFile();
                Platform.runLater(() -> labelName.setText(file.getName()));
                Media media = new Media(file.toURI().toString());
                player = new MediaPlayer(media);
            }
            player.play();
            Platform.runLater(() -> buttonMedia.setText(PAUSE));
        } else {
            player.pause();
            Platform.runLater(() -> buttonMedia.setText(PLAY));
        }
    }

    public void play() {
        File file = getClient().getPlayFile();
        Platform.runLater(() -> labelName.setText(file.getName()));
        if (player != null) {
            player.dispose();
        }
        Media media = new Media(file.toURI().toString());
        player = new MediaPlayer(media);
        player.play();
        Platform.runLater(() -> buttonMedia.setText(PAUSE));
    }

    public void pause() {
        if (player != null) {
            player.pause();
            Platform.runLater(() -> buttonMedia.setText(PLAY));
        }
    }
}

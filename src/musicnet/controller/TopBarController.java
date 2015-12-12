package musicnet.controller;

import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import musicnet.Main;
import musicnet.model.DownloadItem;
import musicnet.model.UploadItem;
import musicnet.util.FXMLLoaderEx;

import java.io.IOException;

public class TopBarController extends BaseController {
    public Button buttonDown;
    public Button buttonUp;

    @Override
    public void setMain(Main main) {
        super.setMain(main);
        getClient().downloadItems.addListener((ListChangeListener<DownloadItem>) c -> {
            buttonDown.setText("Download (" + getClient().downloadItems.size() + ")");
        });
        getClient().uploadItems.addListener((ListChangeListener<UploadItem>) c -> {
            buttonDown.setText("Upload (" + getClient().uploadItems.size() + ")");
        });
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
}

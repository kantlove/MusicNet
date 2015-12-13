package musicnet.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import musicnet.Main;
import musicnet.model.PeerInfo;
import musicnet.model.SongInfo;

public class ExplorerController extends BaseController {
    public ListView<PeerInfo> listPeers;
    public TableView<SongInfo> tableFiles;

    @Override
    public void setMain(Main main) {
        super.setMain(main);
        listPeers.setItems(getClient().peers);
        tableFiles.setItems(getClient().files);
    }

    @FXML
    public void initialize() {
        TableColumn nameCol = new TableColumn("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<SongInfo, String>("name"));

        tableFiles.getColumns().addAll(nameCol);
        tableFiles.setEditable(true);

        ContextMenu menu = new ContextMenu();
        MenuItem downloadFileItem = new MenuItem("Download");
        downloadFileItem.setOnAction(event -> {
            SongInfo item = tableFiles.getSelectionModel().getSelectedItem();
            if (item != null) {
                getClient().download(listPeers.getSelectionModel().getSelectedItem(), item);
            }
        });
        menu.getItems().add(downloadFileItem);
        tableFiles.setContextMenu(menu);

        listPeers.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            getClient().showFilesList(newValue);
        });
    }
}

package musicnet.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import musicnet.Main;
import musicnet.model.PeerInfo;
import musicnet.model.SongInfo;

/**
 * Created by Quan on 12/12/2015.
 */
public class ExplorerController extends BaseController {
    public ListView<PeerInfo> listPeers;
    public TableView tableFiles;

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

        listPeers.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            getClient().showFilesList(newValue);
        });
    }
}

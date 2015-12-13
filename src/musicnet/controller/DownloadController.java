package musicnet.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import musicnet.Main;
import musicnet.model.DownloadItem;

public class DownloadController extends BaseController {
    public TableView<DownloadItem> tableDownload;

    @Override
    public void setMain(Main main) {
        super.setMain(main);
        tableDownload.setItems(getClient().downloadItems);
    }

    @FXML
    public void initialize() {
        TableColumn nameCol = new TableColumn("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<DownloadItem, String>("name"));

        TableColumn fromCol = new TableColumn("From");
        fromCol.setCellValueFactory(new PropertyValueFactory<DownloadItem, String>("from"));

        tableDownload.getColumns().addAll(nameCol, fromCol);
    }
}

package musicnet.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import musicnet.Main;
import musicnet.model.UploadItem;

public class UploadController extends BaseController {
    public TableView<UploadItem> tableUpload;

    @Override
    public void setMain(Main main) {
        super.setMain(main);
        tableUpload.setItems(getClient().uploadItems);
    }

    @FXML
    public void initialize() {
        TableColumn nameCol = new TableColumn("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<UploadItem, String>("name"));

        TableColumn fromCol = new TableColumn("To");
        fromCol.setCellValueFactory(new PropertyValueFactory<UploadItem, String>("to"));

        tableUpload.getColumns().addAll(nameCol, fromCol);
    }
}

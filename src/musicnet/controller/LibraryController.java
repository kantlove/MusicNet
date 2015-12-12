package musicnet.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import musicnet.Main;
import musicnet.model.SongFile;

public class LibraryController extends BaseController {
    public TableView tableSongs;

    @Override
    public void setMain(Main main) {
        super.setMain(main);
        tableSongs.setItems(getClient().songs);
    }

    @FXML
    public void initialize() {
        TableColumn sharedCol = new TableColumn("Shared");
        sharedCol.setCellValueFactory(new PropertyValueFactory<SongFile, Boolean>("shared"));
        sharedCol.setCellFactory(CheckBoxTableCell.forTableColumn(sharedCol));

        TableColumn nameCol = new TableColumn("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<SongFile, String>("name"));

        tableSongs.getColumns().addAll(sharedCol, nameCol);
        tableSongs.setEditable(true);
    }
}

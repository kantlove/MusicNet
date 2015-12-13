package musicnet.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import musicnet.Main;
import musicnet.model.SongFile;

public class LibraryController extends BaseController {
    public TableView<SongFile> tableSongs;

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

        ContextMenu menu = new ContextMenu();
        MenuItem playItem = new MenuItem("Play");
        playItem.setOnAction(event -> {
            SongFile item = tableSongs.getSelectionModel().getSelectedItem();
            if (item != null) {
                getClient().setPlayFile(item.file);
                getClient().play();
            }
        });
        menu.getItems().add(playItem);
        tableSongs.setContextMenu(menu);
    }
}

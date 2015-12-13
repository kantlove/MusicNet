package musicnet.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import musicnet.Main;
import musicnet.model.SongFile;

import java.io.File;
import java.util.List;

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

        tableSongs.setRowFactory(param -> {
            TableRow<SongFile> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    SongFile song = row.getItem();
                    getClient().setPlayFile(song.file);
                    getClient().play();
                }
            });

            MenuItem playItem = new MenuItem("Play");
            playItem.setVisible(!row.isEmpty());
            playItem.setOnAction(event -> {
                if (!row.isEmpty()) {
                    SongFile song = row.getItem();
                    getClient().setPlayFile(song.file);
                    getClient().play();
                }
            });

            MenuItem removeItem = new MenuItem("Remove");
            removeItem.setVisible(!row.isEmpty());
            removeItem.setOnAction(event -> {
                if (!row.isEmpty()) {
                    SongFile song = row.getItem();
                    getClient().remove(song);
                }
            });

            MenuItem importItem = new MenuItem("Import");
            importItem.setOnAction(this::importSongs);

            row.emptyProperty().addListener((observable, oldValue, newValue) -> {
                Platform.runLater(() -> {
                    playItem.setVisible(!newValue);
                    removeItem.setVisible(!newValue);
                });
            });

            ContextMenu menu = new ContextMenu();
            menu.getItems().addAll(playItem, removeItem, importItem);
            row.setContextMenu(menu);

            return row;
        });

        MenuItem importItem = new MenuItem("Import");
        importItem.setOnAction(this::importSongs);
        ContextMenu menu = new ContextMenu();
        menu.getItems().add(importItem);
        tableSongs.setContextMenu(menu);
    }

    private void importSongs(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select files");
        chooser.setInitialDirectory(getClient().getDirectory().getAbsoluteFile());
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP3", "*.mp3"));
        List<File> files = chooser.showOpenMultipleDialog(getMain().primaryStage);
        for (File file : files)
            getClient().importFile(file);
    }
}

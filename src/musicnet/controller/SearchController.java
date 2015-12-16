package musicnet.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import musicnet.Main;
import musicnet.model.SearchResult;
import musicnet.model.SongInfo;

/**
 * Created by Quan on 12/12/2015.
 */
public class SearchController extends BaseController {
    public TextField fieldQuery;
    public TableView<SearchResult> tableResult;

    @Override
    public void setMain(Main main) {
        super.setMain(main);
        tableResult.setItems(getClient().results);
    }

    @FXML
    public void initialize() {
        TableColumn nameCol = new TableColumn("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<SearchResult, String>("name"));

        TableColumn fromCol = new TableColumn("From");
        fromCol.setCellValueFactory(new PropertyValueFactory<SearchResult, String>("from"));

        tableResult.getColumns().addAll(nameCol, fromCol);
        tableResult.setEditable(true);

        ContextMenu menu = new ContextMenu();
        MenuItem downloadFileItem = new MenuItem("Download");
        downloadFileItem.setOnAction(event -> {
            SearchResult result = tableResult.getSelectionModel().getSelectedItem();
            if (result != null) {
                getClient().download(result.peer, result.info);
            }
        });
        menu.getItems().add(downloadFileItem);
        tableResult.setContextMenu(menu);
    }

    public void search(ActionEvent event) {
        String query = fieldQuery.getText().trim();
        if (query.length() > 0) {
            getClient().search(query);
        }
    }
}

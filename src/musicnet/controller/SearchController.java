package musicnet.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import musicnet.Main;
import musicnet.core.SearchResult;

/**
 * Created by Quan on 12/12/2015.
 */
public class SearchController extends BaseController {
    public TextField fieldQuery;
    public TableView tableResult;

    @Override
    public void setMain(Main main) {
        super.setMain(main);
        tableResult.setItems(getClient().results);
    }

    @FXML
    public void initialize() {
        TableColumn nameCol = new TableColumn("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<SearchResult, String>("name"));

        tableResult.getColumns().addAll(nameCol);
        tableResult.setEditable(true);
    }

    public void search(ActionEvent event) {
        String query = fieldQuery.getText().trim();
        if (query.length() > 0) {
            getClient().search(query);
        }
    }
}

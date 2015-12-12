package musicnet.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.DirectoryChooser;
import musicnet.Main;
import musicnet.model.Node;

import java.io.*;
import java.util.Optional;
import java.util.Scanner;

public class WelcomeController extends BaseController {
    private final ObservableList<Node> nodes = FXCollections.observableArrayList();
    public TableView tableNodes;
    public TextField fieldDirectory;
    public TextField fieldName;
    public TextField fieldAddress;

    @Override
    public void setMain(Main main) {
        super.setMain(main);
        fieldDirectory.setText(getClient().getDirectory().getAbsolutePath());
    }

    @FXML
    public void initialize() {
        TableColumn nameCol = new TableColumn("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<Node, String>("name"));
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        nameCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Node, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Node, String> e) {
                Node node = e.getTableView().getItems().get(e.getTablePosition().getRow());
                node.setName(e.getNewValue());
            }
        });

        TableColumn addrCol = new TableColumn("Address");
        addrCol.setPrefWidth(100);
        addrCol.setCellValueFactory(new PropertyValueFactory<Node, String>("address"));
        addrCol.setCellFactory(TextFieldTableCell.forTableColumn());
        addrCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Node, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Node, String> e) {
                Node node = e.getTableView().getItems().get(e.getTablePosition().getRow());
                node.setAddress(e.getNewValue());
            }
        });

        tableNodes.setEditable(true);
        tableNodes.getColumns().addAll(nameCol, addrCol);
        tableNodes.setItems(nodes);
    }

    public void selectDirectory(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select directory");
        File directory = chooser.showDialog(getMain().getPrimaryStage());
        getClient().setDirectory(directory);

        fieldDirectory.setText(directory.getAbsolutePath());
        loadNodes();
    }

    public void reset(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Reset list of nodes");
        alert.setHeaderText(null);
        alert.setContentText("Do you want to discard your changes?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            loadNodes();
        }
    }

    public void addNode(ActionEvent event) {
        String name = fieldName.getText();
        String address = fieldAddress.getText();
        if (name.length() > 0 && address.length() > 0) {
            nodes.add(new Node(name, address));
            fieldName.clear();
            fieldAddress.clear();
        }
    }

    public void start(ActionEvent event) throws IOException {
        saveNodes();
        getMain().showHomeView();
    }

    private void loadNodes() {
        nodes.clear();
        File file = getClient().getNodesFile();
        if (!file.exists()) return;

        try {
            Scanner scanner = new Scanner(new FileReader(file));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] tokens = line.split(" ");
                if (tokens.length >= 2) {
                    nodes.add(new Node(tokens[0], tokens[1]));
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void saveNodes() {
        File file = getClient().getNodesFile();
        try {
            PrintWriter writer = new PrintWriter(file);
            for (Node node : nodes)
                writer.println(node.getName() + " " + node.getAddress());
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

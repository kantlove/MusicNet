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
import musicnet.model.PeerInfo;

import java.io.*;
import java.util.Optional;
import java.util.Scanner;

public class WelcomeController extends BaseController {
    private final ObservableList<PeerInfo> peers = FXCollections.observableArrayList();
    public TableView tableNodes;
    public TextField fieldDirectory;
    public TextField fieldName;
    public TextField fieldAddress;

    @Override
    public void setMain(Main main) {
        super.setMain(main);
        fieldDirectory.setText(getClient().getDirectory().getAbsolutePath());
        loadNodes();
    }

    @FXML
    public void initialize() {
        TableColumn nameCol = new TableColumn("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<PeerInfo, String>("name"));
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        nameCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<PeerInfo, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<PeerInfo, String> e) {
                PeerInfo peer = e.getTableView().getItems().get(e.getTablePosition().getRow());
                peer.setName(e.getNewValue());
            }
        });

        TableColumn addrCol = new TableColumn("Address");
        addrCol.setPrefWidth(100);
        addrCol.setCellValueFactory(new PropertyValueFactory<PeerInfo, String>("address"));
        addrCol.setCellFactory(TextFieldTableCell.forTableColumn());
        addrCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<PeerInfo, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<PeerInfo, String> e) {
                PeerInfo peer = e.getTableView().getItems().get(e.getTablePosition().getRow());
                peer.setAddress(e.getNewValue());
            }
        });

        tableNodes.setEditable(true);
        tableNodes.getColumns().addAll(nameCol, addrCol);
        tableNodes.setItems(peers);
    }

    public void selectDirectory(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select directory");
        File directory = chooser.showDialog(getMain().getPrimaryStage());
        fieldDirectory.setText(directory.getAbsolutePath());

        getClient().setDirectory(directory);
        loadNodes();
    }

    public void reset(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Reset list of peers");
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
            peers.add(new PeerInfo(name, address));
            fieldName.clear();
            fieldAddress.clear();
        }
    }

    public void start(ActionEvent event) throws IOException {
        saveNodes();
        getMain().showHomeView();
    }

    private void loadNodes() {
        peers.clear();
        File file = getClient().getNodesFile();
        if (!file.exists()) return;

        try {
            Scanner scanner = new Scanner(new FileReader(file));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] tokens = line.split(" ");
                if (tokens.length >= 2) {
                    peers.add(new PeerInfo(tokens[0], tokens[1]));
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
            for (PeerInfo peer : peers)
                writer.println(peer.getName() + " " + peer.getAddress());
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

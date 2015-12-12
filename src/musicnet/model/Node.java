package musicnet.model;

import javafx.beans.property.SimpleStringProperty;

public class Node {
    private final SimpleStringProperty name;
    private final SimpleStringProperty address;

    public Node(String name, String address) {
        this.name = new SimpleStringProperty(name);
        this.address = new SimpleStringProperty(address);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getAddress() {
        return address.get();
    }

    public void setAddress(String address) {
        this.address.set(address);
    }
}

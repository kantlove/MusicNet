package musicnet.model;

import java.io.Serializable;

/**
 * Created by mt on 12/2/2015.
 * Edited by quan on 12/12/2015.
 */
public class PeerInfo implements Serializable {
    public String name;
    public Address address;

    public PeerInfo(String name, String address) {
        setName(name);
        setAddress(address);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address.toString();
    }

    public void setAddress(String address) {
        this.address = Address.fromString(address);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PeerInfo peerInfo = (PeerInfo) o;
        return name != null ? name.equals(peerInfo.name)
                : peerInfo.name == null && address.equals(peerInfo.address);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + address.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return name + "@" + getAddress();
    }
}

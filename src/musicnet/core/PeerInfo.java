package musicnet.core;

import java.io.Serializable;
import java.net.UnknownHostException;

/**
 * Created by mt on 12/2/2015.
 */
public class PeerInfo implements Serializable {
    public String name;
    public Address address;

    public PeerInfo(String name, Address address) {
        this.name = name;
        this.address = address;
    }

    public static PeerInfo fromString(String s) throws UnknownHostException {
        assert s != null : "Invalid string";

        String[] parts = s.split(" ");
        assert parts.length > 2 : "Invalid string";

        return new PeerInfo(parts[0], Address.fromString(parts[1]));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PeerInfo peerInfo = (PeerInfo) o;

        if (name != null ? !name.equals(peerInfo.name) : peerInfo.name != null) return false;
        return address.equals(peerInfo.address);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + address.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return name;
    }
}

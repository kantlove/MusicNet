package musicnet.core;

/**
 * Created by mt on 12/2/2015.
 */
public class PeerInfo {
    public String name;
    public Address address;

    public PeerInfo(String name, Address address) {
        this.name = name;
        this.address = address;
    }

    public static PeerInfo fromString(String s) {
        assert s != null : "Invalid string";

        String[] parts = s.split(" ");
        assert parts.length > 2 : "Invalid string";

        return new PeerInfo(parts[0], Address.fromString(parts[1]));
    }
}

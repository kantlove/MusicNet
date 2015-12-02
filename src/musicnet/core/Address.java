package musicnet.core;

/**
 * Created by mt on 12/2/2015.
 */
public class Address {
    public String ip;
    public int port;

    public Address(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public static Address fromString(String s) {
        assert s != null : "Invalid string";

        String[] parts = s.split(":");
        assert parts.length > 2 : "Invalid string";

        return new Address(parts[0], Integer.valueOf(parts[1]));
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null || !(obj instanceof Address))
            return false;
        if(obj == this)
            return true;
        Address a = (Address)obj;
        return ip.equals(a.ip) && port == a.port;
    }

    @Override
    public int hashCode() {
        return ip.hashCode() ^ Integer.hashCode(port);
    }
}

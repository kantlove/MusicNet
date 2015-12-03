package musicnet.core;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by mt on 12/2/2015.
 */
public class Address implements Serializable {
    public InetAddress ip;
    public int port;

    public Address(String ip, int port) throws UnknownHostException {
        this(InetAddress.getByName(ip), port);
    }

    public Address(InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public static Address fromString(String s) throws UnknownHostException {
        assert s != null : "Invalid string";

        String[] parts = s.split(":");
        assert parts.length > 2 : "Invalid string";

        return new Address(InetAddress.getByName(parts[0]), Integer.valueOf(parts[1]));
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

    @Override
    public String toString() {
        return "" + ip + ":" + port;
    }
}

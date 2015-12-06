package musicnet.core;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mt on 12/2/2015.
 */
public class Request implements Serializable {
    public RequestType type;
    public PeerInfo sender;
    public List<PeerInfo> receivers;
    public String[] params;

    public Request(RequestType type, List<PeerInfo> receivers, String...params) {
        assert receivers.size() > 0 : "At least 1 receiver";
        this.type = type;
        this.receivers = receivers;
        this.params = params;
    }
}

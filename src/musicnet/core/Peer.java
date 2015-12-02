package musicnet.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by mt on 12/2/2015.
 */
public final class Peer {
    public PeerInfo info;
    public List<PeerInfo> knownHost = new CopyOnWriteArrayList<>();

    public Peer(PeerInfo info) {
        this.info = info;
    }

    /**
     * Init with nodes.txt
     * @param nodeFile nodes.txt path
     */
    public Peer(String nodeFile) throws IOException {
        knownHost.addAll(readNodes(nodeFile));
    }

    private List<PeerInfo> readNodes(String path) throws IOException {
        final List<PeerInfo> rs = new ArrayList<>();
        Files.lines(Paths.get(path)).forEach(s -> {
            PeerInfo info = PeerInfo.fromString(s);
            if(this.info == null)
                this.info = info;
            else {
                rs.add(info);
            }
        });
        return rs;
    }
}

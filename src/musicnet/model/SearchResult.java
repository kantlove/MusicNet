package musicnet.model;

import java.io.Serializable;

public class SearchResult implements Serializable {
    public SongInfo info;
    public PeerInfo peer;
    public double score;

    public SearchResult(SongFile song, double score) {
        info = new SongInfo(song);
        this.score = score;
    }

    public String getName() {
        return info.getName();
    }

    public String getFrom() {
        return peer.toString();
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" (%.2f)", score);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SearchResult result = (SearchResult) o;
        return getName().equals(result.getName()) && peer.equals(result.peer);
    }
}
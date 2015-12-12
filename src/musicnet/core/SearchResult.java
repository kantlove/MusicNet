package musicnet.core;

import musicnet.model.SongFile;

import java.io.Serializable;

public class SearchResult implements Serializable {
    public String name;
    public String hash;
    public double score;

    public SearchResult(SongFile song, double score) {
        this.name = song.getName();
        this.hash = song.getHash();
        this.score = score;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" (%.2f)", score);
    }
}


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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" (%.2f)", score);
    }
}
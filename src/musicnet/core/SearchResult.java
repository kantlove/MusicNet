package musicnet.core;

import musicnet.model.SongFile;

import java.io.File;

/**
 * Created by Quan on 12/12/2015.
 */
public class SearchResult {
    double score;

    public SearchResult() {
    }

    public SearchResult(File file) {

    }

    public SearchResult(SongFile song, double score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" (%.2f)", score);
    }
}


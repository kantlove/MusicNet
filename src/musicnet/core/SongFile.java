package musicnet.core;

import java.io.File;
import java.io.Serializable;

public class SongFile implements Serializable {
    public String name = "unknown";
    public String hash = "nohash";
    private File file;

    protected SongFile() {
    }

    public SongFile(File file) {
        this.file = file;
        name = file.getName();
        hash = "" + file.getAbsolutePath().hashCode();
    }

    public SongFile(SongFile song) {
        this.file = song.file;
        this.name = song.name;
        this.hash = song.hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SongFile songFile = (SongFile) o;

        if (!name.equals(songFile.name)) return false;
        return hash.equals(songFile.hash);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + hash.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s - %s", name, hash);
    }
}

class SearchResult extends SongFile {
    double score;

    public SearchResult() {
    }

    public SearchResult(File file) {
        super(file);
    }

    public SearchResult(SongFile song, double score) {
        super(song);
        this.score = score;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" (%.2f)", score);
    }
}

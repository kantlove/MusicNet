package musicnet.model;

import java.io.File;
import java.io.Serializable;

public class SongFile implements Serializable {
    private String name;
    private String hash;
    private boolean shared;

    public SongFile(File file) {
        name = file.getName();
        hash = name;
        shared = true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SongFile songFile = (SongFile) o;
        return name.equals(songFile.name) && hash.equals(songFile.hash);
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
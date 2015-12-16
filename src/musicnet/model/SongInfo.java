package musicnet.model;

import java.io.Serializable;

public class SongInfo implements Serializable {
    private String name;
    private String hash;

    public SongInfo(SongFile file) {
        name = file.getName();
        hash = file.getHash();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SongInfo songInfo = (SongInfo) o;
        return name.equals(songInfo.name) && hash.equals(songInfo.hash);
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

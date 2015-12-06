package musicnet.core;

import java.io.File;
import java.io.Serializable;

public class SongFile implements Serializable {
    public String name;
    public String hash;
    private File file;

    public SongFile(File file) {
        this.file = file;
        name = file.getName();
        hash = "" + file.getPath().hashCode();
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

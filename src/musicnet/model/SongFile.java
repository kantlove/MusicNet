package musicnet.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import musicnet.core.Helper;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class SongFile {
    public File file;
    private String name;
    private String hash;
    private BooleanProperty shared;

    public SongFile(File file) throws IOException, NoSuchAlgorithmException {
        this.file = file;
        name = file.getName();
        hash = Helper.getFileCheckSum(file);
        shared = new SimpleBooleanProperty(true);
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

    public BooleanProperty sharedProperty() {
        return shared;
    }

    public boolean getShared() {
        return shared.get();
    }

    public void setShared(boolean shared) {
        this.shared.set(shared);
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
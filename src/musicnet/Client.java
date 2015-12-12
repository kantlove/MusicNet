package musicnet;

import java.io.File;

public class Client {
    private File directory;
    private File nodesFile;

    public File getDirectory() {
        if (directory == null) {
            directory = new File("");
        }
        return directory;
    }

    public void setDirectory(File directory) {
        this.directory = directory;
    }

    public File getNodesFile() {
        if (nodesFile == null) {
            nodesFile = new File(getDirectory().getAbsolutePath() + "\\nodes.txt");
        }
        return nodesFile;
    }
}

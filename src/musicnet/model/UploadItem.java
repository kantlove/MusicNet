package musicnet.model;

public class UploadItem {
    public PeerInfo to;
    public SongInfo song;

    public UploadItem(PeerInfo to, SongInfo song) {
        this.to = to;
        this.song = song;
    }

    public String getName() {
        return song.getName();
    }

    public String getTo() {
        return to.toString();
    }
}

package musicnet.model;

public class DownloadItem {
    public PeerInfo from;
    public SongInfo song;

    public DownloadItem(PeerInfo from, SongInfo song) {
        this.from = from;
        this.song = song;
    }

    public String getName() {
        return song.getName();
    }

    public String getFrom() {
        return from.toString();
    }
}

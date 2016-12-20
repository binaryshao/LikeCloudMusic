package sbingo.likecloudmusic.event;

import sbingo.likecloudmusic.bean.Playlist;

/**
 * Author: Sbingo
 * Date:   2016/12/20
 */

public class PlaylistCreatedEvent {

    private Playlist playlist;
    private int index;

    public Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public PlaylistCreatedEvent(Playlist playlist, int index) {
        this.playlist = playlist;
        this.index = index;
    }
}

package sbingo.likecloudmusic.event;

import sbingo.likecloudmusic.bean.PlayList;

/**
 * Author: Sbingo
 * Date:   2016/12/20
 */

public class PlaylistCreatedEvent {

    private PlayList playlist;
    private int index;

    public PlayList getPlaylist() {
        return playlist;
    }

    public void setPlaylist(PlayList playlist) {
        this.playlist = playlist;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public PlaylistCreatedEvent(PlayList playlist, int index) {
        this.playlist = playlist;
        this.index = index;
    }
}

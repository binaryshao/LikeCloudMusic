package sbingo.likecloudmusic.event;

import sbingo.likecloudmusic.bean.Song;

/**
 * Author: Sbingo
 * Date:   2016/12/26
 */

public class PlayingMusicUpdateEvent {

    private Song song;
    private int index;

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public PlayingMusicUpdateEvent(Song song, int index) {
        this.song = song;
        this.index = index;
    }
}

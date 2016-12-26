package sbingo.likecloudmusic.event;

import sbingo.likecloudmusic.bean.Song;

/**
 * Author: Sbingo
 * Date:   2016/12/26
 */

public class PlayingMusicUpdateEvent {

    private Song song;

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public PlayingMusicUpdateEvent(Song song) {
        this.song = song;
    }
}

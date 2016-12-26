package sbingo.likecloudmusic.event;

import java.util.List;

import sbingo.likecloudmusic.bean.Song;

/**
 * Author: Sbingo
 * Date:   2016/12/19
 */

public class DiskMusicChangeEvent {

    private int count;

    private List<Song> songs;

    public int getCount() {
        return count;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public DiskMusicChangeEvent(List<Song> songs) {
        this.songs = songs;
        count = songs.size();
    }
}

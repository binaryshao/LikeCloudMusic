package sbingo.likecloudmusic.event;

/**
 * Author: Sbingo
 * Date:   2016/12/19
 */

public class MusicChangeEvent {

    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public MusicChangeEvent(int count) {
        this.count = count;
    }
}

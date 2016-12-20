package sbingo.likecloudmusic.player;

/**
 * Author: Sbingo
 * Date:   2016/12/20
 */

public enum PlayMode {
    SINGLE,
    LOOP,
    LIST,
    SHUFFLE;

    public static PlayMode getDefault() {
        return LOOP;
    }

    public static PlayMode switchNextMode(PlayMode current) {
        if (current == null) return getDefault();

        switch (current) {
            case LOOP:
                return LIST;
            case LIST:
                return SHUFFLE;
            case SHUFFLE:
                return SINGLE;
            case SINGLE:
                return LOOP;
        }
        return getDefault();
    }
}

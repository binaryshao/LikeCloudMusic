package sbingo.likecloudmusic.ui.view;

import java.util.List;

import sbingo.likecloudmusic.bean.Song;

/**
 * Author: Sbingo
 * Date:   2016/12/18
 */

public interface DiskMusicView extends BaseView {

    void onMusicLoaded(List<Song> songs);

}

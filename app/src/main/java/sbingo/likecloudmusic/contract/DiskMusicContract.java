package sbingo.likecloudmusic.contract;

import android.database.Cursor;
import android.support.v4.app.LoaderManager;

import java.util.List;

import sbingo.likecloudmusic.bean.PlayList;
import sbingo.likecloudmusic.bean.Song;

/**
 * Author: Sbingo
 * Date:   2017/5/3
 */

public interface DiskMusicContract {

    interface Presenter<T> extends BaseContract.BasePresenter<T> {

        Song cursorToSong(Cursor cursor);

        void loadSongFromDB();

        void createPlaylist(PlayList playlist, int index);

    }

    interface View extends BaseContract.BaseView {

        void onMusicLoaded(List<Song> songs);

        void showEmptyView();

        void hideEmptyView();

        LoaderManager getLoaderManager();

        void onPlaylistDeleted(PlayList playlist);
    }

}

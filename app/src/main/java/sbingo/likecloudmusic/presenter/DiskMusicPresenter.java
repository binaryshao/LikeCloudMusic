package sbingo.likecloudmusic.presenter;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import sbingo.likecloudmusic.bean.Song;
import sbingo.likecloudmusic.common.MyApplication;
import sbingo.likecloudmusic.db.LitePalHelper;
import sbingo.likecloudmusic.event.MusicChangeEvent;
import sbingo.likecloudmusic.event.RxBus;
import sbingo.likecloudmusic.interactor.BaseInteractor;
import sbingo.likecloudmusic.interactor.DiskMusicInteractor;
import sbingo.likecloudmusic.ui.view.DiskMusicView;

/**
 * Author: Sbingo
 * Date:   2016/12/18
 */

public class DiskMusicPresenter extends BasePresenter<DiskMusicView> implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 0;
    private static final Uri MEDIA_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    private static final String WHERE = MediaStore.Audio.Media.IS_MUSIC + "=1 AND "
            + MediaStore.Audio.Media.DURATION + ">60000";
    private static final String ORDER_BY = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
    private static String[] PROJECTIONS = {
            MediaStore.Audio.Media.DATA, // the real path
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.MIME_TYPE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.IS_RINGTONE,
            MediaStore.Audio.Media.IS_MUSIC,
            MediaStore.Audio.Media.IS_NOTIFICATION,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE
    };
    private DiskMusicInteractor mInteractor;

    @Inject
    public DiskMusicPresenter(DiskMusicInteractor interactor) {
        this.mInteractor = interactor;
        mInteractor.attachPresenter(this);
    }

    public void loadMusicFromDisk() {
        mView.showLoading();
        mView.getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    public void loadMusicFromDB() {
        mInteractor.loadFromDB();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id != LOADER_ID) return null;
        return new CursorLoader(
                MyApplication.getAppContext(),
                MEDIA_URI,
                PROJECTIONS,
                WHERE,
                null,
                ORDER_BY
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mInteractor.onLoadDiskFinished(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    public void onNext(List<Song> songs) {
        mView.hideLoading();
        if (songs.isEmpty()) {
            mView.showEmptyView();
        } else {
            mView.onMusicLoaded(songs);
        }
        RxBus.getInstance().post(new MusicChangeEvent(songs.size()));
    }

    public void onError(Throwable throwable) {
        mView.hideLoading();
        mView.showMsg(throwable.getMessage());
    }

    @Override
    public CompositeSubscription provideSubscription() {
        return mInteractor.getSubscriptions();
    }
}

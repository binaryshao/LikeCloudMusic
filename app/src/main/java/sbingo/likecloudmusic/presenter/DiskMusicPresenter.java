package sbingo.likecloudmusic.presenter;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import sbingo.likecloudmusic.bean.PlayList;
import sbingo.likecloudmusic.bean.Song;
import sbingo.likecloudmusic.common.Constants;
import sbingo.likecloudmusic.common.MyApplication;
import sbingo.likecloudmusic.contract.DiskMusicContract;
import sbingo.likecloudmusic.db.LitePalHelper;
import sbingo.likecloudmusic.event.DiskMusicChangeEvent;
import sbingo.likecloudmusic.event.PlaylistCreatedEvent;
import sbingo.likecloudmusic.event.RxBus;
import sbingo.likecloudmusic.utils.FileUtils;
import sbingo.likecloudmusic.utils.PreferenceUtils;

/**
 * Author: Sbingo
 * Date:   2016/12/18
 */

public class DiskMusicPresenter extends RxPresenter<DiskMusicContract.View>
        implements DiskMusicContract.Presenter<DiskMusicContract.View>, LoaderManager.LoaderCallbacks<Cursor> {

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

    @Inject
    public DiskMusicPresenter() {

    }

    public void loadMusicFromDisk() {
        mView.getLoaderManager().initLoader(LOADER_ID, null, this);
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
        Subscription subscription = Observable.just(cursor)
                .flatMap(new Func1<Cursor, Observable<List<Song>>>() {
                    @Override
                    public Observable<List<Song>> call(Cursor cursor) {
                        List<Song> songs = new ArrayList<>();
                        if (cursor != null && cursor.getCount() > 0) {
                            cursor.moveToFirst();
                            do {
                                Song song = cursorToSong(cursor);
                                songs.add(song);
                            } while (cursor.moveToNext());
                        }
                        return LitePalHelper.clearAndInsertSongs(songs);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Song>>() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onCompleted() {
                        PreferenceUtils.putBoolean(MyApplication.getAppContext(), Constants.IS_SCANNED, true);
                        mView.onCompleted();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        mView.onError(throwable.getMessage());
                    }

                    @Override
                    public void onNext(List<Song> songs) {
                        onSongNext(songs);
                    }
                });
        addSubscribe(subscription);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    public Song cursorToSong(Cursor cursor) {
        String realPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
        File songFile = new File(realPath);
        Song song;
        if (songFile.exists()) {
            // Using song parsed from file to avoid encoding problems
            song = FileUtils.fileToMusic(songFile);
            if (song != null) {
                return song;
            }
        }
        song = new Song();
        song.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
        String displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
        if (displayName.endsWith(".mp3")) {
            displayName = displayName.substring(0, displayName.length() - 4);
        }
        song.setDisplayName(displayName);
        song.setArtist(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
        song.setAlbum(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)));
        song.setPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
        song.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
        song.setSize(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)));
        return song;
    }

    @Override
    public void loadSongFromDB() {
        Subscription subscription = LitePalHelper.querySongs()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Song>>() {
                    @Override
                    public void onCompleted() {
                        mView.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.onError(e.getMessage());
                    }

                    @Override
                    public void onNext(List<Song> songs) {
                        onSongNext(songs);
                    }
                });
        addSubscribe(subscription);
    }

    public void createPlaylist(PlayList playlist, final int index) {
        Subscription subscription = LitePalHelper.insertPlaylist(playlist)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PlayList>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.onError(e.getMessage());
                    }

                    @Override
                    public void onNext(PlayList playlist) {
                        RxBus.getInstance().post(new PlaylistCreatedEvent(playlist, index));
                    }
                });
        addSubscribe(subscription);
    }

    private void onSongNext(List<Song> songs) {
        if (songs.isEmpty()) {
            mView.showEmptyView();
        } else {
            mView.onMusicLoaded(songs);
        }
        RxBus.getInstance().post(new DiskMusicChangeEvent(songs));
    }

}

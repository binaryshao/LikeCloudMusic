package sbingo.likecloudmusic.interactor;

import android.database.Cursor;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import sbingo.likecloudmusic.bean.Song;
import sbingo.likecloudmusic.presenter.DiskMusicPresenter;
import sbingo.likecloudmusic.utils.FileUtils;

/**
 * Author: Sbingo
 * Date:   2016/12/19
 */

public class DiskMusicInteractor extends BaseInteractor<DiskMusicPresenter> {

    @Inject
    public DiskMusicInteractor() {
    }

    public void onLoadFinish(Cursor cursor) {
        Observable.just(cursor)
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
                        return Observable.just(songs);
                    }
                })
                .doOnNext(new Action1<List<Song>>() {
                    @Override
                    public void call(List<Song> songs) {
                        Collections.sort(songs, new Comparator<Song>() {
                            @Override
                            public int compare(Song left, Song right) {
                                return left.getDisplayName().compareTo(right.getDisplayName());
                            }
                        });

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
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        mPresenter.onError(throwable);
                    }

                    @Override
                    public void onNext(List<Song> songs) {
                        mPresenter.onNext(songs);
                    }
                });

    }

    private Song cursorToSong(Cursor cursor) {
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
}

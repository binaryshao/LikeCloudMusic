package sbingo.likecloudmusic.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.style.SubscriptSpan;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.Date;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import sbingo.likecloudmusic.bean.Playlist;
import sbingo.likecloudmusic.bean.Song;

/**
 * Author: Sbingo
 * Date:   2016/12/19
 */

public class LitePalHelper {

    private static SQLiteDatabase db;

    public static void initDB() {
        db = LitePal.getDatabase();
    }

    //Song

    public static Observable<List<Song>> InsertSongs(final List<Song> songs) {
        return Observable.create(new Observable.OnSubscribe<List<Song>>() {
            @Override
            public void call(Subscriber<? super List<Song>> subscriber) {
                boolean isSuccess = true;
                for (Song song : songs) {
                    if (!song.save()) {
                        isSuccess = false;
                        break;
                    }
                }
                if (isSuccess) {
                    List<Song> songList = DataSupport.findAll(Song.class);
                    subscriber.onNext(songList);
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(new Throwable("存储歌曲出错！"));
                }

            }
        });
    }

    public static Observable<List<Song>> clearAndInsertSongs(final List<Song> songs) {
        return Observable.create(new Observable.OnSubscribe<List<Song>>() {
            @Override
            public void call(Subscriber<? super List<Song>> subscriber) {
                boolean isSuccess = true;
                DataSupport.deleteAll(Song.class);
                for (Song song : songs) {
                    if (!song.save()) {
                        isSuccess = false;
                        break;
                    }
                }
                if (isSuccess) {
                    List<Song> songList = DataSupport.findAll(Song.class);
                    subscriber.onNext(songList);
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(new Throwable("存储歌曲出错！"));
                }
            }
        });
    }


    public static Observable<List<Song>> querySongs() {
        return Observable.create(new Observable.OnSubscribe<List<Song>>() {
            @Override
            public void call(Subscriber<? super List<Song>> subscriber) {
                List<Song> songs = DataSupport.findAll(Song.class);
                subscriber.onNext(songs);
                subscriber.onCompleted();
            }
        });
    }

    public static Observable<Void> deleteSong(final Song song) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                DataSupport.delete(Song.class, song.getId());
                subscriber.onCompleted();
            }
        });
    }

    public static Observable<Void> deleteAllSongs() {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                DataSupport.deleteAll(Song.class);
                subscriber.onCompleted();
            }
        });
    }

    //Playlist

    public static Observable<Playlist> insertPlaylist(final Playlist playlist) {
        return Observable.create(new Observable.OnSubscribe<Playlist>() {
            @Override
            public void call(Subscriber<? super Playlist> subscriber) {
                Date now = new Date();
                playlist.setCreatedAt(now);
                playlist.setUpdatedAt(now);
                boolean isSuccess = playlist.save();
                if (isSuccess) {
                    subscriber.onNext(playlist);
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(new Throwable("存储歌单出错！"));
                }
            }
        });
    }

    public static Observable<Playlist> queryCurrentPlaylist() {
        return Observable.create(new Observable.OnSubscribe<Playlist>() {
            @Override
            public void call(Subscriber<? super Playlist> subscriber) {
                Playlist playlist =DataSupport.findLast(Playlist.class, true);
                if (playlist==null) {
                    subscriber.onError(new Throwable("歌单库为空"));
                } else {
                    subscriber.onNext(playlist);
                    subscriber.onCompleted();
                }
            }
        });
    }
}

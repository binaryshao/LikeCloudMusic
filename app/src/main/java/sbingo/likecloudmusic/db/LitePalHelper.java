package sbingo.likecloudmusic.db;

import android.database.sqlite.SQLiteDatabase;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
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

    public static Observable<List<Song>> InsertSongs(final List<Song> songs) {
        return Observable.create(new Observable.OnSubscribe<List<Song>>() {
            @Override
            public void call(Subscriber<? super List<Song>> subscriber) {
                for (Song song : songs) {
                    song.save();
                }
                subscriber.onNext(DataSupport.findAll(Song.class));
                subscriber.onCompleted();
            }
        });
    }

    public static Observable<List<Song>> clearAndInsertSongs(final List<Song> songs) {
        return Observable.create(new Observable.OnSubscribe<List<Song>>() {
            @Override
            public void call(Subscriber<? super List<Song>> subscriber) {
                DataSupport.deleteAll(Song.class);
                for (Song song : songs) {
                    song.save();
                }
                subscriber.onNext(DataSupport.findAll(Song.class));
                subscriber.onCompleted();
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
}

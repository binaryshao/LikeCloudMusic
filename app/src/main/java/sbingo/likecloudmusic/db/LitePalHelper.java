package sbingo.likecloudmusic.db;

import android.database.sqlite.SQLiteDatabase;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.List;

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

    public static void InsertSongs(List<Song> songs) {
        for (Song song : songs) {
            song.save();
        }
    }

    public static List<Song> querySongs() {
        return DataSupport.findAll(Song.class);
    }

    public static void deleteSong(Song song) {
        DataSupport.delete(Song.class, song.getId());
    }
}

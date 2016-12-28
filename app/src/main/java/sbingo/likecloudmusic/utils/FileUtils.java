package sbingo.likecloudmusic.utils;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import sbingo.likecloudmusic.bean.Song;
import sbingo.likecloudmusic.common.MyApplication;

/**
 * Author: Sbingo
 * Date:   2016/12/18
 */

public class FileUtils {

    public static Song fileToMusic(File file) {
        if (file.length() == 0) return null;

        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(file.getAbsolutePath());

        String title = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        String displayName = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        String artist = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        String album = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        int duration = Integer.parseInt(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));

        if (duration == 0) return null;

        Song song = new Song();
        song.setTitle(title);
        song.setDisplayName(displayName);
        song.setArtist(artist);
        song.setPath(file.getAbsolutePath());
        song.setAlbum(album);
        song.setDuration(duration);
        song.setSize((int) file.length());
        return song;
    }

    public static List<Song> loadDiskMusic() {
        List<Song> songs = new ArrayList<>();
        Cursor cursor = MyApplication.getAppContext().getContentResolver()
                .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        while (cursor.moveToNext()) {
            if (cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC)) != 0) {
                Song song = new Song();
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
                songs.add(song);
            }
        }
        cursor.close();
        return songs;
    }

    public static Bitmap parseThumbToBitmap(Song song) {
        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(new File(song.getPath()).getAbsolutePath());
        byte[] thumbData = metadataRetriever.getEmbeddedPicture();
        if (thumbData != null) {
            return BitmapFactory.decodeByteArray(thumbData, 0, thumbData.length);
        }
        return null;
    }

    public static byte[] parseThumbToByte(Song song) {
        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(new File(song.getPath()).getAbsolutePath());
        return metadataRetriever.getEmbeddedPicture();
    }

    public static boolean isFileExists(String path) {
        File file = new File(path);
        if (file.exists()) {
            return true;
        }
        return false;
    }

}

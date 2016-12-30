package sbingo.likecloudmusic.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import sbingo.likecloudmusic.player.PlayMode;


/**
 * Author: Sbingo
 * Date:   2016/12/20
 */

public class Playlist extends DataSupport implements Parcelable {

    public static final int NO_POSITION = -1;

    private int id;

    private String name;

    private int numOfSongs;

    private boolean favorite;

    private Date createdAt;

    private Date updatedAt;

    private List<Song> songs = new ArrayList<>();

    private boolean currentPlaylist;

    private int playingIndex = -1;

    private PlayMode playMode = PlayMode.getDefault();

    public Playlist() {
    }

    public Playlist(Song song) {
        songs.add(song);
        numOfSongs = 1;
    }

    public Playlist(Parcel in) {
        readFromParcel(in);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumOfSongs() {
        return songs.size();
    }

    public void setNumOfSongs(int numOfSongs) {
        this.numOfSongs = numOfSongs;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isCurrentPlaylist() {
        return currentPlaylist;
    }

    public void setCurrentPlaylist(boolean currentPlaylist) {
        this.currentPlaylist = currentPlaylist;
    }

    @NonNull
    public List<Song> getSongs() {
        if (songs == null) {
            songs = new ArrayList<>();
        }
        return songs;
    }

    public void setSongs(@Nullable List<Song> songs) {
        if (songs == null) {
            songs = new ArrayList<>();
        }
        this.songs = songs;
    }

    public int getPlayingIndex() {
        return playingIndex;
    }

    public void setPlayingIndex(int playingIndex) {
        this.playingIndex = playingIndex;
    }

    public PlayMode getPlayMode() {
        return playMode;
    }

    public void setPlayMode(PlayMode playMode) {
        this.playMode = playMode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.numOfSongs);
        dest.writeByte(this.favorite ? (byte) 1 : (byte) 0);
        dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
        dest.writeLong(this.updatedAt != null ? this.updatedAt.getTime() : -1);
        dest.writeTypedList(this.songs);
        dest.writeInt(this.playingIndex);
        dest.writeInt(this.playMode == null ? -1 : this.playMode.ordinal());
        dest.writeByte(this.currentPlaylist ? (byte) 1 : (byte) 0);
    }

    public void readFromParcel(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.numOfSongs = in.readInt();
        this.favorite = in.readByte() != 0;
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        long tmpUpdatedAt = in.readLong();
        this.updatedAt = tmpUpdatedAt == -1 ? null : new Date(tmpUpdatedAt);
        this.songs = in.createTypedArrayList(Song.CREATOR);
        this.playingIndex = in.readInt();
        int tmpPlayMode = in.readInt();
        this.playMode = tmpPlayMode == -1 ? null : PlayMode.values()[tmpPlayMode];
        this.currentPlaylist = in.readByte() != 0;
    }

    public static final Creator<Playlist> CREATOR = new Creator<Playlist>() {
        @Override
        public Playlist createFromParcel(Parcel source) {
            return new Playlist(source);
        }

        @Override
        public Playlist[] newArray(int size) {
            return new Playlist[size];
        }
    };

    // Utils

    public int getItemCount() {
        return songs == null ? 0 : songs.size();
    }

    public void addSong(@Nullable Song song) {
        if (song == null) return;

        songs.add(song);
        numOfSongs = songs.size();
    }

    public void addSong(@Nullable Song song, int index) {
        if (song == null) return;

        songs.add(index, song);
        numOfSongs = songs.size();
    }

    public void addSong(@Nullable List<Song> songs, int index) {
        if (songs == null || songs.isEmpty()) return;

        this.songs.addAll(index, songs);
        this.numOfSongs = this.songs.size();
    }

    public boolean removeSong(Song song) {
        if (song == null) return false;

        int index;
        if ((index = songs.indexOf(song)) != -1) {
            if (songs.remove(index) != null) {
                numOfSongs = songs.size();
                return true;
            }
        } else {
            for (Iterator<Song> iterator = songs.iterator(); iterator.hasNext(); ) {
                Song item = iterator.next();
                if (song.getPath().equals(item.getPath())) {
                    iterator.remove();
                    numOfSongs = songs.size();
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Prepare to play
     */
    public boolean prepare() {
        if (songs.isEmpty()) return false;
        if (playingIndex == NO_POSITION) {
            playingIndex = 0;
        }
        return true;
    }

    /**
     * The current song being played or is playing based on the {@link #playingIndex}
     */
    public Song getCurrentSong() {
        if (playingIndex != NO_POSITION) {
            return songs.get(playingIndex);
        } else if (playingIndex>getNumOfSongs()-1) {
            playingIndex = 0;
            return songs.get(playingIndex);
        }
        return null;
    }

    public boolean hasLast() {
        return songs != null && songs.size() != 0;
    }

    public Song last() {
        switch (playMode) {
            case LOOP:
            case LIST:
            case SINGLE:
                int newIndex = playingIndex - 1;
                if (newIndex < 0) {
                    newIndex = songs.size() - 1;
                }
                playingIndex = newIndex;
                break;
            case SHUFFLE:
                playingIndex = randomPlayIndex();
                break;
        }
        return songs.get(playingIndex);
    }

    /**
     * @return Whether has next song to play.
     * <p/>
     * If this query satisfies these conditions
     * - comes from media player's complete listener
     * - current play mode is PlayMode.LIST (the only limited play mode)
     * - current song is already in the end of the list
     * then there shouldn't be a next song to play, for this condition, it returns false.
     * <p/>
     * If this query is from user's action, such as from play controls, there should always
     * has a next song to play, for this condition, it returns true.
     */
    public boolean hasNext(boolean fromComplete) {
        if (songs.isEmpty()) return false;
        if (fromComplete) {
            if (playMode == PlayMode.LIST && playingIndex + 1 >= songs.size()) return false;
        }
        return true;
    }

    /**
     * Move the playingIndex forward depends on the play mode
     *
     * @return The next song to play
     */
    public Song next() {
        switch (playMode) {
            case LOOP:
            case LIST:
            case SINGLE:
                int newIndex = playingIndex + 1;
                if (newIndex >= songs.size()) {
                    newIndex = 0;
                }
                playingIndex = newIndex;
                break;
            case SHUFFLE:
                playingIndex = randomPlayIndex();
                break;
        }
        return songs.get(playingIndex);
    }

    private int randomPlayIndex() {
        int randomIndex = new Random().nextInt(songs.size());
        // Make sure not play the same song twice if there are at least 2 songs
        if (songs.size() > 1 && randomIndex == playingIndex) {
            randomPlayIndex();
        }
        return randomIndex;
    }

}

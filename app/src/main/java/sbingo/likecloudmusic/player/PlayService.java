package sbingo.likecloudmusic.player;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.orhanobut.logger.Logger;

import java.io.IOException;

import sbingo.likecloudmusic.bean.PlayList;
import sbingo.likecloudmusic.bean.Song;

public class PlayService extends Service implements MediaPlayer.OnCompletionListener {

    private static final String TAG = "PlayService :";

    private static final String ACTION_PLAY_NEXT = "like_cloud_music.play_next";
    private static final String ACTION_PLAY_LAST = "like_cloud_music.play_last";
    private static final String ACTION_PLAY_TOGGLE = "like_cloud_music.play_toggle";
    private static final String ACTION_SIOP_SERVICE = "like_cloud_music.stop_service";

    private MediaPlayer mPlayer;
    private PlayerBinder mBinder;
    private PlayList mPlayList;

    private boolean isPaused;

    class PlayerBinder extends Binder {
        public PlayService getPlayService() {
            return PlayService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Logger.d(TAG + "onBind");
        return mBinder;
    }

    @Override
    public void onCreate() {
        Logger.d(TAG + "onCreate");
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(this);

        mPlayList = new PlayList();

        mBinder = new PlayerBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d(TAG + "onStartCommand");
        String action = intent.getAction();
        if (ACTION_PLAY_TOGGLE.equals(action)) {
            if (isPlaying()) {
                pause();
            } else {
                play();
            }
        } else if (ACTION_PLAY_NEXT.equals(action)) {
            playNext();
        } else if (ACTION_PLAY_LAST.equals(action)) {
            playLast();
        } else if (ACTION_SIOP_SERVICE.equals(action)) {
            stopService();
        }
        return START_STICKY;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Logger.d(TAG + "onCompletion");
        Song next = null;
        if (mPlayList.hasNext(true)) {
            if (mPlayList.getPlayMode() == PlayMode.SINGLE) {
                next = mPlayList.getCurrentSong();
                play();
            } else {
                next = mPlayList.next();
                play();
            }
        }
    }

    @Override
    public void onDestroy() {
        Logger.d(TAG + "onDestroy");

        super.onDestroy();
        mPlayer.reset();
        mPlayer.release();
    }

    public PlayList getPlayList() {
        return mPlayList;
    }

    public void setPlayList(PlayList mPlayList) {
        this.mPlayList = mPlayList;
    }

    public void play() {
        if (canResumePlay()) {
            return;
        }
        if (mPlayList.prepare()) {
            Song song = mPlayList.getCurrentSong();
            try {
                mPlayer.reset();
                mPlayer.setDataSource(song.getPath());
                mPlayer.prepare();
                mPlayer.start();
            } catch (IOException e) {
                Logger.d(TAG + e);
            }
        }
    }

    public boolean canResumePlay() {
        if (isPaused) {
            mPlayer.start();
            return true;
        }
        return false;
    }

    public void pause() {
        if (isPlaying()) {
            mPlayer.pause();
            isPaused = true;
        }
    }

    public void play(PlayList list) {
        if (list == null) return;

        isPaused = false;
        setPlayList(list);
        play();
    }

    public void play(PlayList list, int startIndex) {
        if (list == null || startIndex < 0 || startIndex >= list.getNumOfSongs()) return ;

        isPaused = false;
        list.setPlayingIndex(startIndex);
        setPlayList(list);
        play();
    }

    public void play(Song song) {
        if (song == null) return;

        isPaused = false;
        mPlayList.getSongs().clear();
        mPlayList.getSongs().add(song);
        play();
    }

    public void playLast() {
        isPaused = false;
        boolean hasLast = mPlayList.hasLast();
        if (hasLast) {
            Song last = mPlayList.last();
            play();
        }
    }

    public void playNext() {
        isPaused = false;
        boolean hasNext = mPlayList.hasNext(false);
        if (hasNext) {
            Song next = mPlayList.next();
            play();
        }
    }

    public void stopService() {
        if (isPlaying()) {
            pause();
        }
        stopForeground(true);
    }

    public boolean isPlaying() {
        return mPlayer.isPlaying();
    }

    public boolean isPaused() {
        return isPaused;
    }

    public int getProgress() {
        return mPlayer.getCurrentPosition() / mPlayer.getDuration();
    }

    public void seekTo(int progress) {
        if (mPlayList.getSongs().isEmpty()) return;

        Song currentSong = mPlayList.getCurrentSong();
        if (currentSong != null) {
            if (currentSong.getDuration() <= progress) {
                onCompletion(mPlayer);
            } else {
                mPlayer.seekTo(progress);
            }
        }
    }

    public void setPlayMode(PlayMode playMode) {
        mPlayList.setPlayMode(playMode);
    }

}

package sbingo.likecloudmusic.player;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.orhanobut.logger.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import sbingo.likecloudmusic.bean.Playlist;
import sbingo.likecloudmusic.bean.Song;
import sbingo.likecloudmusic.common.Constants;
import sbingo.likecloudmusic.common.MyApplication;
import sbingo.likecloudmusic.db.LitePalHelper;
import sbingo.likecloudmusic.event.PausePlayingEvent;
import sbingo.likecloudmusic.event.PlayingMusicUpdateEvent;
import sbingo.likecloudmusic.event.RxBus;
import sbingo.likecloudmusic.event.StartPlayingEvent;
import sbingo.likecloudmusic.utils.FileUtils;
import sbingo.likecloudmusic.utils.PreferenceUtils;
import sbingo.likecloudmusic.utils.RemindUtils;

public class PlayService extends Service implements MediaPlayer.OnCompletionListener {

    private static final String TAG = "PlayService :";

    private static final String ACTION_PLAY_NEXT = "like_cloud_music.play_next";
    private static final String ACTION_PLAY_LAST = "like_cloud_music.play_last";
    private static final String ACTION_PLAY_TOGGLE = "like_cloud_music.play_toggle";
    private static final String ACTION_SIOP_SERVICE = "like_cloud_music.stop_service";

    private MediaPlayer mPlayer;
    private PlayerBinder mBinder;
    private Playlist mPlayList;

    private boolean isPaused;

    private ExecutorService executorService;


    public class PlayerBinder extends Binder {
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

        mPlayList = new Playlist();

        mBinder = new PlayerBinder();

        executorService = Executors.newSingleThreadExecutor();

        PreferenceUtils.putBoolean(MyApplication.getAppContext(), Constants.PLAY_SERVICE_RUNNING, true);
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
    public void onDestroy() {
        Logger.d(TAG + "onDestroy");
        super.onDestroy();
        mPlayer.reset();
        mPlayer.release();
        PreferenceUtils.putBoolean(MyApplication.getAppContext(), Constants.PLAY_SERVICE_RUNNING, false);
    }

    public Playlist getPlayList() {
        return mPlayList;
    }

    public void setPlaylist(Playlist mPlayList) {
        this.mPlayList = mPlayList;
    }

    public void setPlaylist(Playlist mPlayList, int index) {
        this.mPlayList = mPlayList;
        this.mPlayList.setPlayingIndex(index);
    }

    public void play() {
        executorService.execute(playTask);
    }

    private Runnable playTask = new Runnable() {
        @Override
        public void run() {
            if (canResumePlay()) {
                return;
            }
            if (mPlayList.prepare()) {
                Song song = mPlayList.getCurrentSong();
                try {
                    if (!FileUtils.isFileExists(song.getPath())) {
                        throw new FileNotFoundException();
                    }
                    mPlayer.reset();
                    mPlayer.setDataSource(song.getPath());
                    mPlayer.prepare();
                    mPlayer.start();
                    RxBus.getInstance().post(new StartPlayingEvent());
                } catch (FileNotFoundException e) {
                    noFileWhilePlay(song);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void noFileWhilePlay(final Song song) {
        RemindUtils.showToast("跳过一首不存在的歌曲");
        LitePalHelper.deleteSong(song)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        deleteSongFromPlaylist(song);
                        play();
                    }
                });
    }

    private void deleteSongFromPlaylist(Song song) {
        if (mPlayList.getSongs().isEmpty()) return;
        for (Song s : mPlayList.getSongs()) {
            if (s.getPath().equals(song.getPath())) {
                mPlayList.getSongs().remove(song);
                break;
            }
        }
    }

    public boolean canResumePlay() {
        if (isPaused) {
            mPlayer.start();
            isPaused = false;
            return true;
        }
        return false;
    }

    public void pause() {
        if (isPlaying()) {
            mPlayer.pause();
            isPaused = true;
            RxBus.getInstance().post(new PausePlayingEvent());
        }
    }

    public void play(Playlist list) {
        if (list == null) return;

        isPaused = false;
        setPlaylist(list);
        play();
    }

    public void play(Playlist list, int startIndex) {
        if (list == null || startIndex < 0 || startIndex >= list.getNumOfSongs()) return;

        isPaused = false;
        list.setPlayingIndex(startIndex);
        setPlaylist(list);
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
        pause();
        stopForeground(true);
    }

    public boolean isPlaying() {
        return mPlayer.isPlaying();
    }

    public boolean isPaused() {
        return isPaused;
    }

    public float getProgressPercent() {
        return (float) mPlayer.getCurrentPosition() / (float) mPlayer.getDuration();
    }

    public void seekTo(int progress) {
        if (mPlayList.getSongs().isEmpty()) return;

        Song currentSong = mPlayList.getCurrentSong();
        try {
            mPlayer.reset();
            mPlayer.setDataSource(currentSong.getPath());
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (currentSong != null) {
            if (currentSong.getDuration() <= progress) {
                onCompletion(mPlayer);
            } else {
                mPlayer.seekTo(progress);
                RxBus.getInstance().post(new StartPlayingEvent());
            }
        }
    }

    public void setPlayMode(PlayMode playMode) {
        mPlayList.setPlayMode(playMode);
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
            RxBus.getInstance().post(new PlayingMusicUpdateEvent(next, mPlayList.getPlayingIndex()));
        }
    }

    private void showNotification() {

    }
}

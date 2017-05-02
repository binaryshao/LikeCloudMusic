package sbingo.likecloudmusic.player;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.orhanobut.logger.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import sbingo.likecloudmusic.R;
import sbingo.likecloudmusic.bean.PlayList;
import sbingo.likecloudmusic.bean.Song;
import sbingo.likecloudmusic.common.Constants;
import sbingo.likecloudmusic.common.MyApplication;
import sbingo.likecloudmusic.db.LitePalHelper;
import sbingo.likecloudmusic.event.PausePlayingEvent;
import sbingo.likecloudmusic.event.PlayingMusicUpdateEvent;
import sbingo.likecloudmusic.event.RxBus;
import sbingo.likecloudmusic.event.StartPlayingEvent;
import sbingo.likecloudmusic.ui.activity.MainActivity;
import sbingo.likecloudmusic.utils.FileUtils;
import sbingo.likecloudmusic.utils.PreferenceUtils;

import static android.support.v4.app.NotificationCompat.PRIORITY_HIGH;

public class PlayService extends Service implements MediaPlayer.OnCompletionListener {

    private static final String TAG = "PlayService :";

    private static final String ACTION_PLAY_NEXT = "like_cloud_music.play_next";
    private static final String ACTION_PLAY_LAST = "like_cloud_music.play_last";
    private static final String ACTION_PLAY_TOGGLE = "like_cloud_music.play_toggle";
    private static final String ACTION_SIOP_SERVICE = "like_cloud_music.stop_service";
    private static final String ACTION_FAVORITE = "like_cloud_music.favorite";
    private static final String ACTION_LYRIC = "like_cloud_music.lyric";

    private static final int NOTIFICATION_ID = 100;
    private RemoteViews mNotificationViewBig, mNotificationViewSmall;

    private MediaPlayer mPlayer;
    private PlayerBinder mBinder;
    private PlayList mPlayList;

    private boolean isPaused;

    private ExecutorService executorService;

    //仅测试用，不真正存储
    private boolean isFavorite = true;
    //仅测试用
    private boolean isShowLyric;


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

        mPlayList = new PlayList();

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
        } else if (ACTION_FAVORITE.equals(action)) {
            isFavorite = isFavorite ? false : true;
            showNotification();
        } else if (ACTION_LYRIC.equals(action)) {
            isShowLyric = isShowLyric ? false : true;
            showNotification();
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

    public PlayList getPlayList() {
        return mPlayList;
    }

    public void setPlaylist(PlayList mPlayList) {
        this.mPlayList = mPlayList;
    }

    public void setPlaylist(PlayList mPlayList, int index) {
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
                RxBus.getInstance().post(new StartPlayingEvent());
                showNotification();
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
                    showNotification();
                } catch (FileNotFoundException e) {
                    noFileWhilePlay(song);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void noFileWhilePlay(final Song song) {
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
            showNotification();
        }
    }

    public void play(PlayList list) {
        if (list == null) return;

        isPaused = false;
        setPlaylist(list);
        play();
    }

    public void play(PlayList list, int startIndex) {
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
        showNotification();
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

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.white_logo_50)
                .setWhen(System.currentTimeMillis())
                .setPriority(PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setCustomBigContentView(getBigNotificationView())
                .setCustomContentView(getSmallNotificationView())
                .setTicker("云音正在播放")
                .setOngoing(true)
                .build();

        startForeground(NOTIFICATION_ID, notification);
    }

    RemoteViews getBigNotificationView() {
        if (mNotificationViewBig == null) {
            mNotificationViewBig = new RemoteViews(getPackageName(), R.layout.notification_view);
            initNotificationView(mNotificationViewBig);
        }
        updateNotificationView(mNotificationViewBig);
        return mNotificationViewBig;
    }

    RemoteViews getSmallNotificationView() {
        if (mNotificationViewSmall == null) {
            mNotificationViewSmall = new RemoteViews(getPackageName(), R.layout.notification_view_small);
            initNotificationView(mNotificationViewSmall);
        }
        updateNotificationView(mNotificationViewSmall);
        return mNotificationViewSmall;
    }

    void initNotificationView(RemoteViews remoteViews) {
        remoteViews.setOnClickPendingIntent(R.id.close, getPendingIntent(ACTION_SIOP_SERVICE));
        remoteViews.setOnClickPendingIntent(R.id.last, getPendingIntent(ACTION_PLAY_LAST));
        remoteViews.setOnClickPendingIntent(R.id.toggle, getPendingIntent(ACTION_PLAY_TOGGLE));
        remoteViews.setOnClickPendingIntent(R.id.next, getPendingIntent(ACTION_PLAY_NEXT));
        remoteViews.setOnClickPendingIntent(R.id.favorite, getPendingIntent(ACTION_FAVORITE));
        remoteViews.setOnClickPendingIntent(R.id.lyric, getPendingIntent(ACTION_LYRIC));
    }

    void updateNotificationView(RemoteViews remoteViews) {
        Song song = mPlayList.getCurrentSong();
        Bitmap thumb = FileUtils.parseThumbToBitmap(song);
        if (thumb == null) {
            remoteViews.setImageViewResource(R.id.thumb, R.drawable.pic_error_150);
        } else {
            remoteViews.setImageViewBitmap(R.id.thumb, thumb);
        }
        remoteViews.setTextViewText(R.id.song_name, song.getTitle());
        remoteViews.setTextViewText(R.id.info, getString(R.string.song_info, song.getArtist(), song.getAlbum()));
        remoteViews.setImageViewResource(R.id.favorite, isFavorite ? R.drawable.notification_love_checked_32 : R.drawable.notification_love_32);
        remoteViews.setImageViewResource(R.id.toggle, isPlaying() ? R.drawable.notification_pause_64 : R.drawable.notification_play_64);
        remoteViews.setImageViewResource(R.id.lyric, isShowLyric ? R.drawable.notification_lyric_checked_32 : R.drawable.notification_lyric_32);
    }

    private PendingIntent getPendingIntent(String action) {
        return PendingIntent.getService(this, 0, new Intent(action), 0);
    }

}

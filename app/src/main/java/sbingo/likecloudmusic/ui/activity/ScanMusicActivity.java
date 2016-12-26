package sbingo.likecloudmusic.ui.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.orhanobut.logger.Logger;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import sbingo.likecloudmusic.R;
import sbingo.likecloudmusic.bean.Playlist;
import sbingo.likecloudmusic.bean.Song;
import sbingo.likecloudmusic.common.Constants;
import sbingo.likecloudmusic.db.LitePalHelper;
import sbingo.likecloudmusic.event.MusicChangeEvent;
import sbingo.likecloudmusic.event.PlaylistCreatedEvent;
import sbingo.likecloudmusic.event.PlaylistDeletedEvent;
import sbingo.likecloudmusic.event.RxBus;
import sbingo.likecloudmusic.player.PlayService;
import sbingo.likecloudmusic.ui.adapter.PageAdapter.LocalPagerAdapter;
import sbingo.likecloudmusic.ui.fragment.LocalMusic.DiskMusicFragment;
import sbingo.likecloudmusic.utils.PreferenceUtils;
import sbingo.likecloudmusic.utils.RemindUtils;
import sbingo.likecloudmusic.widget.OutPlayerController;

/**
 * Author: Sbingo
 * Date:   2016/12/15
 */

public class ScanMusicActivity extends BaseActivity implements OutPlayerController.OutPlayerControllerListener {

    private static final String TAG = "ScanMusicActivity :";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.player_controller)
    OutPlayerController playerController;

    private DiskMusicFragment[] diskMusicFragments = {new DiskMusicFragment(), new DiskMusicFragment(), new DiskMusicFragment(), new DiskMusicFragment()};
    private PlayService mPlayService;
    private Playlist playlist;
    private int index;
    private boolean playOnceBind;

    @Override
    protected int getLayoutId() {
        return R.layout.local_layout;
    }

    @Override
    protected void initInjector() {

    }

    @Override
    protected void initViews() {
        initPlayerController();
        initViewPager();
        registerDiskMusicEvent();
    }

    @Override
    protected void customToolbar() {
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected boolean hasToolbar() {
        return true;
    }

    @Override
    protected CompositeSubscription provideSubscription() {
        return null;
    }

    void registerDiskMusicEvent() {
        RxBus.getInstance().toObservable(MusicChangeEvent.class)
                .subscribe(new Action1<MusicChangeEvent>() {
                    @Override
                    public void call(MusicChangeEvent event) {
                        diskMusicFragments[1].onMusicLoaded(event.getSongs());
                        diskMusicFragments[2].onMusicLoaded(event.getSongs());
                        diskMusicFragments[3].onMusicLoaded(event.getSongs());
                    }
                });
    }

    void initPlayerController() {
        playerController.setPlayerListener(this);
        if (PreferenceUtils.getBoolean(this, Constants.HAS_PLAYLIST)) {
            playerController.setVisibility(View.VISIBLE);
            if (PreferenceUtils.getBoolean(this, Constants.PLAY_SERVICE_RUNNING)) {
                bindToService();
            } else {
                getPlaylistAndBind();
            }
        }
        RxBus.getInstance().toObservable(PlaylistCreatedEvent.class)
                .subscribe(new Action1<PlaylistCreatedEvent>() {
                    @Override
                    public void call(PlaylistCreatedEvent event) {
                        if (playerController.getVisibility() != View.VISIBLE) {
                            playerController.setVisibility(View.VISIBLE);
                        }
                        playlist = event.getPlaylist();
                        index = event.getIndex();
                        if (mPlayService == null) {
                            playOnceBind = true;
                            bindService(new Intent(ScanMusicActivity.this, PlayService.class), mConnection, BIND_AUTO_CREATE);
                        } else {
                            mPlayService.play(playlist, index);
                            playerController.setPlaying(true);
                            playlist = null;
                        }
                        PreferenceUtils.putBoolean(ScanMusicActivity.this, Constants.HAS_PLAYLIST, true);
                    }
                });
        RxBus.getInstance().toObservable(PlaylistDeletedEvent.class)
                .subscribe(new Action1<PlaylistDeletedEvent>() {
                    @Override
                    public void call(PlaylistDeletedEvent event) {
                        playerController.setVisibility(View.GONE);
                    }
                });
    }

    private void bindToService() {
        bindService(new Intent(ScanMusicActivity.this, PlayService.class), mConnection, BIND_AUTO_CREATE);
    }

    private void getPlaylistAndBind() {
        LitePalHelper.queryCurrentPlaylist()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Playlist>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        playerController.setVisibility(View.GONE);
                        PreferenceUtils.putBoolean(ScanMusicActivity.this, Constants.HAS_PLAYLIST, false);
                    }

                    @Override
                    public void onNext(Playlist playlist) {
                        ScanMusicActivity.this.playlist = playlist;
                        setControllerInfo( playlist.getSongs().get(0));
                        bindService(new Intent(ScanMusicActivity.this, PlayService.class), mConnection, BIND_AUTO_CREATE);
                    }
                });
    }

    void initViewPager() {
        viewPager.setAdapter(new LocalPagerAdapter(getSupportFragmentManager(), createFragments()));
        viewPager.setOffscreenPageLimit(3);
        tabs.setupWithViewPager(viewPager);
    }

    private List<DiskMusicFragment> createFragments() {
        List<DiskMusicFragment> fragments = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.LOCAL_TYPE, i + 1);
            diskMusicFragments[i].setArguments(bundle);
            fragments.add(diskMusicFragments[i]);
        }
        return fragments;
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Logger.d(TAG + "onServiceConnected");
            mPlayService = ((PlayService.PlayerBinder) service).getPlayService();
            if (playOnceBind) {
                mPlayService.play(playlist, index);
                playerController.setPlaying(true);
                setControllerInfo(mPlayService.getPlayList().getCurrentSong());
            } else if (playlist != null) {
                mPlayService.setPlayList(playlist);
                mPlayService.getPlayList().prepare();
            }
            playlist = null;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Logger.d(TAG + "onServiceDisconnected");
            mPlayService = null;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scan_menu, menu);
        return true;
    }

    //解决menu中图标不显示的问题
    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        if (menu != null) {
            if (menu.getClass() == MenuBuilder.class) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                }
            }
        }
        return super.onPrepareOptionsPanel(view, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
            case R.id.search_local:
                RemindUtils.showToast("0");
                break;
            case R.id.scan_music:
                scanFragment();
                break;
            case R.id.sort:
                RemindUtils.showToast("2");
                break;
            case R.id.cover_lyric:
                RemindUtils.showToast("3");
                break;
            case R.id.upgrade_quality:
                RemindUtils.showToast("4");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void scanFragment() {
        diskMusicFragments[0].scanDiskMusic();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayService != null) {
            unbindService(mConnection);
            mPlayService = null;
        }
        playOnceBind = false;
    }

    @Override
    public void play() {
        if (mPlayService == null) {
            return;
        }
        if (mPlayService.isPlaying()) {
            mPlayService.pause();
        } else {
            setControllerInfo(mPlayService.getPlayList().getNextSong());
            mPlayService.play();
        }
    }

    @Override
    public void next() {
        if (mPlayService == null) {
            return;
        }
        setControllerInfo(mPlayService.getPlayList().getNextSong());
        mPlayService.playNext();
    }

    @Override
    public void playList() {
        if (mPlayService == null) {
            return;
        }
        Logger.d(TAG + mPlayService.getPlayList().getSongs());
    }

    @Override
    public void controller() {
        if (mPlayService == null) {
            return;
        }
        RemindUtils.showToast(String.format("【%s】播放详情页面待续……",
                mPlayService.getPlayList().getCurrentSong().getTitle()));
    }

    private void setControllerInfo(Song song) {
        playerController.setSongName(song.getTitle());
        playerController.setSinger(song.getArtist());
    }
}

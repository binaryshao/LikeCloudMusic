package sbingo.likecloudmusic.ui.activity;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import sbingo.likecloudmusic.R;
import sbingo.likecloudmusic.bean.PlayList;
import sbingo.likecloudmusic.bean.Song;
import sbingo.likecloudmusic.common.Constants;
import sbingo.likecloudmusic.db.LitePalHelper;
import sbingo.likecloudmusic.event.PausePlayingEvent;
import sbingo.likecloudmusic.event.PlayingMusicUpdateEvent;
import sbingo.likecloudmusic.event.PlaylistCreatedEvent;
import sbingo.likecloudmusic.event.PlaylistDeletedEvent;
import sbingo.likecloudmusic.event.RxBus;
import sbingo.likecloudmusic.event.StartPlayingEvent;
import sbingo.likecloudmusic.player.PlayService;
import sbingo.likecloudmusic.ui.adapter.PageAdapter.MainPagerAdapter;
import sbingo.likecloudmusic.utils.FileUtils;
import sbingo.likecloudmusic.utils.PreferenceUtils;
import sbingo.likecloudmusic.utils.RemindUtils;
import sbingo.likecloudmusic.utils.TimeUtils;
import sbingo.likecloudmusic.widget.OutPlayerController;

/**
 * Author: Sbingo
 * Date:   2016/12/11
 */

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private static final String TAG = "MainActivity :";

    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.setting)
    TextView setting;
    @BindView(R.id.quit)
    TextView quit;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.main_view_pager)
    ViewPager mainViewPager;
    @BindView(R.id.player_controller)
    OutPlayerController playerController;

    ImageView avatar;
    TextView name;
    TextView level;
    TextView signIn;

    TextView grade;
    TextView theme;
    TextView timer;

    RadioGroup radioGroup;

    boolean fromViewPager;
    boolean fromRadioGroup;

    private static PlayService mPlayService;
    private PlayList playlist;
    private int index;
    private boolean playOnceBind;
    private static final long PROGRESS_UPDATE_INTERVAL = 1000;
    private Handler mHandler = new Handler();
    /**
     * 上次退出时保存的播放进度
     */
    private int lastProgress;
    private Intent serviceIntent;
    private boolean isBinded;
    private String lastThumb = "";
    private int timingStopIndex = 0;
    private boolean isStopAfterEnd;
    private String customTime = "假装自定义(5分钟后)";
    private MyCountDownTimer myCountDownTimer;
    private boolean finishWhileComplete;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initInjector() {

    }

    @Override
    public void initViews() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        initNavigation();
        initRadioGroup();
        initViewPager();
        initPlayerController();
        registerEvents();
        serviceIntent = new Intent(this, PlayService.class);
    }

    void initPlayerController() {
        playerController.setPlayerListener(new MyPlayerListener());
        if (PreferenceUtils.getBoolean(this, Constants.HAS_PLAYLIST) && mPlayService == null) {
            playerController.setVisibility(View.VISIBLE);
            getPlaylistAndBind();
        } else if (PreferenceUtils.getBoolean(this, Constants.HAS_PLAYLIST) && mPlayService != null) { //切换日夜间模式
            playerController.setVisibility(View.VISIBLE);
            playerController.setPlaying(true);
            mHandler.post(progressCallback);
        }
    }

    void registerEvents() {
        //之后将有多处界面可以发出PlaylistCreatedEvent事件
        Subscription createdSubscription = RxBus.getInstance().toObservable(PlaylistCreatedEvent.class)
                .subscribe(new Action1<PlaylistCreatedEvent>() {
                    @Override
                    public void call(PlaylistCreatedEvent event) {
                        if (playerController.getVisibility() != View.VISIBLE) {
                            playerController.setVisibility(View.VISIBLE);
                        }
                        playlist = event.getPlaylist();
                        index = event.getIndex();
                        if (mPlayService == null) { //列表从无到有才会进入此处
                            playOnceBind = true;
                            bindService(new Intent(MainActivity.this, PlayService.class), mConnection, BIND_AUTO_CREATE);
                        } else { //从“本地音乐”发出的事件不用执行操作
                            mPlayService.play(playlist, index);
                            playerController.setPlaying(true);
                            playlist = null;
                        }
                        PreferenceUtils.putBoolean(MainActivity.this, Constants.HAS_PLAYLIST, true);
                    }
                });
        Subscription deletedSubscription = RxBus.getInstance().toObservable(PlaylistDeletedEvent.class)
                .subscribe(new Action1<PlaylistDeletedEvent>() {
                    @Override
                    public void call(PlaylistDeletedEvent event) {
                        playerController.setVisibility(View.GONE);
                    }
                });
        Subscription updateSubscription = RxBus.getInstance().toObservable(PlayingMusicUpdateEvent.class)
                .subscribe(new Action1<PlayingMusicUpdateEvent>() {
                    @Override
                    public void call(PlayingMusicUpdateEvent event) {
                        if (finishWhileComplete) {
                            finish();
                        } else {
                            setControllerInfo(event.getSong());
                        }
                    }
                });
        Subscription startSubscription = RxBus.getInstance().toObservable(StartPlayingEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<StartPlayingEvent>() {
                    @Override
                    public void call(StartPlayingEvent event) {
                        setControllerInfo(mPlayService.getPlayList().getCurrentSong());
                        mHandler.post(progressCallback);
                    }
                });
        Subscription pauseSubscription = RxBus.getInstance().toObservable(PausePlayingEvent.class)
                .subscribe(new Action1<PausePlayingEvent>() {
                    @Override
                    public void call(PausePlayingEvent event) {
                        playerController.setPlaying(false);
                        mHandler.removeCallbacks(progressCallback);
                    }
                });
    }

    private void getPlaylistAndBind() {
        LitePalHelper.queryCurrentPlaylist()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<PlayList>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        playerController.setVisibility(View.GONE);
                        PreferenceUtils.putBoolean(MainActivity.this, Constants.HAS_PLAYLIST, false);
                    }

                    @Override
                    public void onNext(PlayList playlist) {
                        MainActivity.this.playlist = playlist;
                        bindService(new Intent(MainActivity.this, PlayService.class), mConnection, BIND_AUTO_CREATE);
                    }
                });
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Logger.d(TAG + "onServiceConnected");
            mPlayService = ((PlayService.PlayerBinder) service).getPlayService();
            isBinded = true;
            if (playOnceBind) {
                playOnceBind = false;
                mPlayService.play(playlist, index);
                setControllerInfo(mPlayService.getPlayList().getCurrentSong());
            } else if (playlist != null && playlist.getNumOfSongs() > 0) { //加载出本地歌单时
                Logger.d("读取播放歌曲序号：" + PreferenceUtils.getInt(MainActivity.this, Constants.PLAYING_INDEX));
                Logger.d("读取播放歌曲进度：" + PreferenceUtils.getInt(MainActivity.this, Constants.PLAYING_PROGRESS));
                mPlayService.setPlaylist(playlist, PreferenceUtils.getInt(MainActivity.this, Constants.PLAYING_INDEX, 0));
                mPlayService.getPlayList().prepare();
                lastProgress = PreferenceUtils.getInt(MainActivity.this, Constants.PLAYING_PROGRESS, 0);
                playerController.setPlayProgress(lastProgress);
                setControllerInfo(mPlayService.getPlayList().getCurrentSong());
            }
            playlist = null;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Logger.d(TAG + "onServiceDisconnected");
            mPlayService = null;
        }
    };

    void initNavigation() {
        navView.setNavigationItemSelectedListener(this);
        navView.setItemIconTintList(null);
        navView.getChildAt(0).setVerticalScrollBarEnabled(false);

        //HeaderView内的控件
        avatar = (ImageView) navView.getHeaderView(0).findViewById(R.id.avatar);
        name = (TextView) navView.getHeaderView(0).findViewById(R.id.name);
        level = (TextView) navView.getHeaderView(0).findViewById(R.id.level);
        signIn = (TextView) navView.getHeaderView(0).findViewById(R.id.sign_in);

        avatar.setOnClickListener(this);
        name.setOnClickListener(this);
        level.setOnClickListener(this);
        signIn.setOnClickListener(this);

        //Menu内的控件
        baseNavView = navView;
        grade = (TextView) MenuItemCompat.getActionView(navView.getMenu().findItem(R.id.shop));
        theme = (TextView) MenuItemCompat.getActionView(navView.getMenu().findItem(R.id.theme));
        timer = (TextView) MenuItemCompat.getActionView(navView.getMenu().findItem(R.id.timing_stop));

        grade.setText("100积分");
        theme.setText("典雅黑");
        timer.setText("");
    }


    void initRadioGroup() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (fromViewPager) {
                    fromViewPager = false;
                    return;
                }
                fromRadioGroup = true;
                switch (checkedId) {
                    case R.id.net_music:
                        mainViewPager.setCurrentItem(0, true);
                        break;
                    case R.id.local_music:
                        mainViewPager.setCurrentItem(1, true);
                        break;
                    case R.id.social:
                        mainViewPager.setCurrentItem(2, true);
                        break;
                    default:
                }
            }
        });
    }

    void initViewPager() {
        mainViewPager.setAdapter(new MainPagerAdapter(getSupportFragmentManager()));
        mainViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (fromRadioGroup) {
                    fromRadioGroup = false;
                    return;
                }
                fromViewPager = true;
                ((RadioButton) radioGroup.getChildAt(position)).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mainViewPager.setCurrentItem(1);
    }

    @Override
    public void customToolbar() {
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        radioGroup = (RadioGroup) LayoutInflater.from(this).inflate(R.layout.main_radio_group, null);
        radioGroup.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
        actionBar.setCustomView(radioGroup, new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @Override
    protected boolean hasToolbar() {
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPlayService == null) {
            return;
        }
        setControllerInfo(mPlayService.getPlayList().getCurrentSong());
    }

    private Runnable progressCallback = new Runnable() {
        @Override
        public void run() {
            if (mPlayService != null && mPlayService.isPlaying()) {
                playerController.setPlayProgress((int) (playerController.getProgressMax() * mPlayService.getProgressPercent()));
                mHandler.postDelayed(this, PROGRESS_UPDATE_INTERVAL);
            }
        }
    };

    //播放控制器
    class MyPlayerListener implements OutPlayerController.OutPlayerControllerListener {

        @Override
        public void play() {
            if (mPlayService == null) {
                return;
            }
            if (mPlayService.isPlaying()) {
                mPlayService.pause();
            } else if (mPlayService.isPaused()) {
                mPlayService.play();
                lastProgress = 0;
            } else {
                if (lastProgress != 0) {
                    int songProgress = (int) (mPlayService.getPlayList().getCurrentSong().getDuration() * (float) lastProgress / (float) playerController.getProgressMax());
                    mPlayService.seekTo(songProgress);
                    lastProgress = 0;
                } else {
                    mPlayService.play();
                }
            }
        }

        @Override
        public void next() {
            if (mPlayService == null) {
                return;
            }
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
    }

    private void setControllerInfo(Song song) {
        playerController.setSongName(song.getTitle());
        playerController.setSinger(song.getArtist());
        playerController.setPlaying(mPlayService.isPlaying());
        if (!lastThumb.equals(song.getPath())) {
            playerController.setThumb(FileUtils.parseThumbToByte(song));
            lastThumb = song.getPath();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            moveTaskToBack(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.message) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.member_center) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.shop) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.listen_online) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.identify_song) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.theme) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.night_mode) {
            nightSwitch.setChecked(nightSwitch.isChecked() ? false : true);
        } else if (id == R.id.timing_stop) {
            drawerLayout.closeDrawer(GravityCompat.START);
            timingStop();
        } else if (id == R.id.scan) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.music_box) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.music_clock) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.driving_mode) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }

        return true;
    }

    /**
     * 【定时停止播放】弹窗
     */
    private void timingStop() {
        TextView tv = new TextView(this);
        tv.setText(R.string.timing_stop);
        tv.setPadding(60, 20, 0, 0);
        tv.setTextSize(16);
        tv.setTextColor(ContextCompat.getColor(this, R.color.black));
        final CharSequence[] items = getResources().getStringArray(R.array.timing_stop);
        items[items.length - 1] = customTime;
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.my_dialog)
                .setCustomTitle(tv)
                .setView(R.layout.timing_stop_bottom)
                .setSingleChoiceItems(items, timingStopIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == items.length - 1) {
                            RemindUtils.showToast("自定义功能尚在开发ing");
                        } else if (which == 0) {
                            if (myCountDownTimer != null) {
                                myCountDownTimer.cancel();
                                myCountDownTimer = null;
                            }
                            timer.setText("");
                            RemindUtils.showToast("定时停止播放已取消");
                            dialog.dismiss();
                        } else {
                            timingStopIndex = which;
                            long millisInFuture = 0L;
                            switch (which) {
                                case 1:
                                    millisInFuture = 10 * 60 * 1000;
                                    break;
                                case 2:
                                    millisInFuture = 20 * 60 * 1000;
                                    break;
                                case 3:
                                    millisInFuture = 30 * 60 * 1000;
                                    break;
                                case 4:
                                    millisInFuture = 45 * 60 * 1000;
                                    break;
                                case 5:
                                    millisInFuture = 60 * 60 * 1000;
                                    break;
                                default:
                            }
                            if (myCountDownTimer != null) {
                                myCountDownTimer.cancel();
                            }
                            myCountDownTimer = new MyCountDownTimer(millisInFuture, 1000);
                            myCountDownTimer.start();
                            RemindUtils.showToast(String.format("设置成功，将于%s关闭", items[which]));
                            dialog.dismiss();
                        }
                    }
                })
                .show();
        dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        CheckBox checkBox = (CheckBox) dialog.findViewById(R.id.stop_after_end);
        checkBox.setChecked(isStopAfterEnd ? true : false);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isStopAfterEnd = isChecked;
                if (isChecked) {
                    RemindUtils.showToast(getString(R.string.stop_after_end));
                }
            }
        });
    }

    private class MyCountDownTimer extends CountDownTimer {

        MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            timer.setText(TimeUtils.getLeftTime(millisUntilFinished));
        }

        @Override
        public void onFinish() {
            if (isStopAfterEnd && mPlayService.isPlaying()) {
                finishWhileComplete = true;
            } else {
                finish();
            }
        }
    }

    @OnClick({R.id.setting, R.id.quit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setting:
                RemindUtils.showToast("设置");
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.quit:
                drawerLayout.closeDrawer(GravityCompat.START);
                finish();
                break;
            case R.id.avatar:
                RemindUtils.showToast("头像");
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.name:
                RemindUtils.showToast("昵称");
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.level:
                RemindUtils.showToast("等级");
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.sign_in:
                RemindUtils.showToast("签到");
                signIn.setEnabled(false);
                signIn.setText("已签到");
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.d("MainActivity onDestroy");
        if (isModeChanged && mPlayService != null) {
            startService(serviceIntent);
        }
        if (mPlayService != null) {
            if (mPlayService.getPlayList() != null) {
                PreferenceUtils.putInt(this, Constants.PLAYING_INDEX, mPlayService.getPlayList().getPlayingIndex());
                PreferenceUtils.putInt(this, Constants.PLAYING_PROGRESS, playerController.getPlayProgress());
                Logger.d("保存播放歌曲序号：" + PreferenceUtils.getInt(this, Constants.PLAYING_INDEX));
                Logger.d("保存播放歌曲进度：" + PreferenceUtils.getInt(this, Constants.PLAYING_PROGRESS));
            }
            if (isBinded) {
                isBinded = false;
                unbindService(mConnection);
            }
            if (!isModeChanged) {
                stopService(serviceIntent);
                mPlayService = null;
            }
            isModeChanged = false;
        }
    }
}

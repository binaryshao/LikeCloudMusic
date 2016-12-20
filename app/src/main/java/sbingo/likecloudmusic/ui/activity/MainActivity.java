package sbingo.likecloudmusic.ui.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;
import sbingo.likecloudmusic.R;
import sbingo.likecloudmusic.common.Constants;
import sbingo.likecloudmusic.event.PlaylistCreatedEvent;
import sbingo.likecloudmusic.event.PlaylistDeletedEvent;
import sbingo.likecloudmusic.event.RxBus;
import sbingo.likecloudmusic.ui.adapter.PageAdapter.MainPagerAdapter;
import sbingo.likecloudmusic.utils.PreferenceUtils;
import sbingo.likecloudmusic.utils.RemindUtils;
import sbingo.likecloudmusic.widget.OutPlayerController;

/**
 * Author: Sbingo
 * Date:   2016/12/11
 */

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, CompoundButton.OnCheckedChangeListener, View.OnClickListener {

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
    SwitchCompat nightSwitch;

    RadioGroup radioGroup;

    boolean fromViewPager;
    boolean fromRadioGroup;


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
    }

    void initPlayerController() {
        playerController.setPlayerListener(new MyPlayerListener());
        if (PreferenceUtils.getBoolean(this, Constants.HAS_PLAYLIST)) {
            playerController.setVisibility(View.VISIBLE);
        }
        RxBus.getInstance().toObservable(PlaylistCreatedEvent.class)
                .subscribe(new Action1<PlaylistCreatedEvent>() {
                    @Override
                    public void call(PlaylistCreatedEvent event) {
                        playerController.setVisibility(View.VISIBLE);
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
        grade = (TextView) MenuItemCompat.getActionView(navView.getMenu().findItem(R.id.shop));
        theme = (TextView) MenuItemCompat.getActionView(navView.getMenu().findItem(R.id.theme));
        nightSwitch = (SwitchCompat) MenuItemCompat.getActionView(navView.getMenu().findItem(R.id.night_mode));

        grade.setText("100积分");
        theme.setText("典雅黑");
        nightSwitch.setOnCheckedChangeListener(this);
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
    protected CompositeSubscription provideSubscription() {
        return null;
    }

    //播放控制器
    class MyPlayerListener implements OutPlayerController.OutPlayerControllerListener {

        @Override
        public void play() {
            if (playerController.isPlaying()) {
                RemindUtils.showToast("播放");
            } else {
                RemindUtils.showToast("暂停");
            }
        }

        @Override
        public void next() {
            RemindUtils.showToast("下一首");
        }

        @Override
        public void playList() {
            RemindUtils.showToast("播放列表");
        }

        @Override
        public void controller() {
            RemindUtils.showToast("播放详情");
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.message) {

        } else if (id == R.id.member_center) {

        } else if (id == R.id.shop) {

        } else if (id == R.id.listen_online) {

        } else if (id == R.id.identify_song) {

        } else if (id == R.id.theme) {

        } else if (id == R.id.night_mode) {
            nightSwitch.setChecked(nightSwitch.isChecked() ? false : true);
            return true;
        } else if (id == R.id.timing_stop) {

        } else if (id == R.id.scan) {

        } else if (id == R.id.music_box) {

        } else if (id == R.id.music_clock) {

        } else if (id == R.id.driving_mode) {

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
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
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
            switchToNight();
        } else {
            switchToDay();
        }
    }

    void switchToNight() {
        RemindUtils.showToast("我是黑土");
    }

    void switchToDay() {
        RemindUtils.showToast("我是白云");
    }
}

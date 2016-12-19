package sbingo.likecloudmusic.ui.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import sbingo.likecloudmusic.R;
import sbingo.likecloudmusic.common.Constants;
import sbingo.likecloudmusic.ui.adapter.PageAdapter.LocalPagerAdapter;
import sbingo.likecloudmusic.ui.fragment.LocalMusic.DiskMusicFragment;
import sbingo.likecloudmusic.utils.RemindUtils;
import sbingo.likecloudmusic.widget.OutPlayerController;

/**
 * Author: Sbingo
 * Date:   2016/12/15
 */

public class ScanMusicActivity extends BaseActivity implements OutPlayerController.OutPlayerControllerListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.player_controller)
    OutPlayerController playerController;

    @Override
    protected int getLayoutId() {
        return R.layout.local_layout;
    }

    @Override
    protected void initInjector() {

    }

    @Override
    protected void initViews() {
        initViewPager();
        playerController.setPlayerListener(this);
    }

    @Override
    protected void customToolbar() {
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected boolean hasToolbar() {
        return true;
    }

    void initViewPager() {
        viewPager.setAdapter(new LocalPagerAdapter(getSupportFragmentManager(), createFragments()));
        tabs.setupWithViewPager(viewPager);
    }

    private List<DiskMusicFragment> createFragments() {
        List<DiskMusicFragment> fragments = new ArrayList<>();
        for (int i = 1; i < 5; i++) {
            DiskMusicFragment fragment = new DiskMusicFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.LOCAL_TYPE, i);
            fragment.setArguments(bundle);
            fragments.add(fragment);
        }
        return fragments;
    }

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
                RemindUtils.showToast("1");
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    public void play() {

    }

    @Override
    public void next() {

    }

    @Override
    public void playList() {

    }

    @Override
    public void controller() {

    }
}

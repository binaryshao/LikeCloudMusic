package sbingo.likecloudmusic.ui.fragment.LocalMusic;

import android.Manifest;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import rx.subscriptions.CompositeSubscription;
import sbingo.likecloudmusic.R;
import sbingo.likecloudmusic.bean.Playlist;
import sbingo.likecloudmusic.bean.Song;
import sbingo.likecloudmusic.common.Constants;
import sbingo.likecloudmusic.event.PlaylistCreatedEvent;
import sbingo.likecloudmusic.event.RxBus;
import sbingo.likecloudmusic.presenter.DiskMusicPresenter;
import sbingo.likecloudmusic.ui.adapter.RvAdapter.DiskMusicAdapter;
import sbingo.likecloudmusic.ui.fragment.BaseFragment;
import sbingo.likecloudmusic.ui.view.DiskMusicView;
import sbingo.likecloudmusic.utils.PreferenceUtils;
import sbingo.likecloudmusic.utils.RemindUtils;

/**
 * Author: Sbingo
 * Date:   2016/12/15
 */

public class DiskMusicFragment extends BaseFragment implements DiskMusicView, DiskMusicAdapter.DiskMusicListener {

    @BindView(R.id.r_view)
    RecyclerView rView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.scan_disk)
    TextView scanDisk;
    @BindView(R.id.empty_view)
    LinearLayout emptyView;

    private int currentType;
    private DiskMusicAdapter mAdapter;

    @Inject
    DiskMusicPresenter mPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.disk_music_layout;
    }

    @Override
    protected void initInjector() {
        mFragmentComponent.inject(this);
        mPresenter.attachView(this);
    }

    @Override
    protected void initViews() {
        if (getArguments() != null) {
            currentType = getArguments().getInt(Constants.LOCAL_TYPE);
        }
        rView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new DiskMusicAdapter(currentType, this, getActivity());
        rView.setAdapter(mAdapter);
        if (currentType == 1) {
            if (PreferenceUtils.getBoolean(getActivity(), Constants.IS_SCANNED)) {
                Logger.d("loadMusicFromDB");
                mPresenter.loadMusicFromDB();
            } else {
                Logger.d("loadMusicFromDisk");
                scanDiskMusic();
            }
        }
    }

    @Override
    protected CompositeSubscription provideSubscription() {
        return mPresenter.provideSubscription();
    }

    public void scanDiskMusic() {
        checkPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, new PermissionListener() {
            @Override
            public void onGranted() {
                mPresenter.loadMusicFromDisk();
            }

            @Override
            public void onDenied(List<String> permissions) {
                RemindUtils.showToast(getString(R.string.permission_denied, "读取存储", "加载音乐失败"));
                showEmptyView();
            }
        });
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showMsg(String msg) {
        RemindUtils.showToast(msg);
    }

    @Override
    public void onMusicLoaded(List<Song> songs) {
        mAdapter.setList(songs);
        //似乎加载太快导致看不到进度条，手动延长进度条时间……
        progressBar.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideLoading();
            }
        }, 2000);
    }

    @Override
    public void showEmptyView() {
        emptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPlaylistCreated(Playlist playlist, int index) {
        RxBus.getInstance().post(new PlaylistCreatedEvent(playlist, index));
    }

    @Override
    public void onPlaylistDeleted(Playlist playlist) {

    }

    @OnClick(R.id.scan_disk)
    public void onClick() {
        scanDiskMusic();
    }

    @Override
    public void toPlayerActivity(Song song) {
        RemindUtils.showToast(String.format("【%s】播放详情页面待续……", song.getTitle()));
    }

    @Override
    public void playList(Playlist playlist, int index) {
        mPresenter.createPlaylist(playlist, index);
    }

}

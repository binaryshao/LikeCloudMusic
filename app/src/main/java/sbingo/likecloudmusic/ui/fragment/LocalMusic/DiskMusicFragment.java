package sbingo.likecloudmusic.ui.fragment.LocalMusic;

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
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import sbingo.likecloudmusic.R;
import sbingo.likecloudmusic.bean.Song;
import sbingo.likecloudmusic.common.Constants;
import sbingo.likecloudmusic.db.LitePalHelper;
import sbingo.likecloudmusic.event.MusicChangeEvent;
import sbingo.likecloudmusic.event.RxBus;
import sbingo.likecloudmusic.presenter.DiskMusicPresenter;
import sbingo.likecloudmusic.ui.adapter.RvAdapter.DiskMusicAdapter;
import sbingo.likecloudmusic.ui.fragment.BaseFragment;
import sbingo.likecloudmusic.ui.view.DiskMusicView;
import sbingo.likecloudmusic.utils.FileUtils;
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
        List<Song> songs = FileUtils.loadDiskMusic();
//        MediaPlayer player = new MediaPlayer();
//        try {
//            player.setDataSource(songs.get(0).getPath());
//            player.prepare();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        player.start();
        mAdapter.setList(songs);
        if (PreferenceUtils.getBoolean(getActivity(), Constants.IS_SCANNED)) {
            Logger.d("loadMusicFromDB");
            loadMusicFromDB();
        } else {
            Logger.d("loadMusicFromDisk");
            mPresenter.loadMusicFromDisk();
        }
    }

    private void loadMusicFromDB() {
        Observable.just(LitePalHelper.querySongs())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Song>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        showMsg(e.getMessage());
                    }

                    @Override
                    public void onNext(List<Song> songs) {
                        hideLoading();
                        if (songs.isEmpty()) {
                            showEmptyView();
                        } else {
                            onMusicLoaded(songs);
                        }
                        RxBus.getInstance().post(new MusicChangeEvent(songs.size()));
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
    }

    @Override
    public void showEmptyView() {
        emptyView.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.scan_disk)
    public void onClick() {
        mPresenter.loadMusicFromDisk();
    }

    @Override
    public void toPlayerActivity(Song song) {

    }

    @Override
    public void playMusic(Song song) {

    }
}

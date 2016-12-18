package sbingo.likecloudmusic.ui.fragment.LocalMusic;

import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import sbingo.likecloudmusic.R;
import sbingo.likecloudmusic.bean.Song;
import sbingo.likecloudmusic.common.Constants;
import sbingo.likecloudmusic.ui.adapter.RvAdapter.DiskMusicAdapter;
import sbingo.likecloudmusic.ui.fragment.BaseFragment;
import sbingo.likecloudmusic.ui.view.DiskMusicView;
import sbingo.likecloudmusic.utils.FileUtils;

/**
 * Author: Sbingo
 * Date:   2016/12/15
 */

public class DiskMusicFragment extends BaseFragment implements DiskMusicView, DiskMusicAdapter.DiskMusicListener {

    @BindView(R.id.r_view)
    RecyclerView rView;

    private int currentType;
    private DiskMusicAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.disk_music_layout;
    }

    @Override
    protected void initInjector() {

    }

    @Override
    protected void initViews() {
        if (getArguments() != null) {
            currentType = getArguments().getInt(Constants.LOCAL_TYPE);
        }
        mAdapter = new DiskMusicAdapter(currentType, this, getActivity());
        rView.setAdapter(mAdapter);
        List<Song> songs = FileUtils.loadDiskMusic();
        MediaPlayer player = new MediaPlayer();
        try {
            player.setDataSource(songs.get(0).getPath());
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.start();
    }

    public int getType() {
        return currentType;
    }


    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showMsg(String msg) {

    }

    @Override
    public void onMusicLoaded(List<Song> songs) {
        mAdapter.setList(songs);
    }
}

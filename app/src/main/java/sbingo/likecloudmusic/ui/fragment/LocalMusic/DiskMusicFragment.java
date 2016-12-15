package sbingo.likecloudmusic.ui.fragment.LocalMusic;

import android.support.v7.widget.RecyclerView;

import butterknife.BindView;
import sbingo.likecloudmusic.R;
import sbingo.likecloudmusic.common.Constants;
import sbingo.likecloudmusic.ui.fragment.BaseFragment;

/**
 * Author: Sbingo
 * Date:   2016/12/15
 */

public class DiskMusicFragment extends BaseFragment {

    @BindView(R.id.r_view)
    RecyclerView rView;

    private int currentType;

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

    }

    public int getType() {
        return currentType;
    }
}

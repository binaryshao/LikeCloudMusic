package sbingo.likecloudmusic.ui.fragment.NetMusic;

import sbingo.likecloudmusic.ui.fragment.BaseFragment;

/**
 * Author: Sbingo
 * Date:   2016/12/14
 */

public class PlaylistFragment extends BaseFragment {

    private static PlaylistFragment playlistFragment;

    public static PlaylistFragment getInstance() {
        if (null == playlistFragment) {
            synchronized (PlaylistFragment.class){
                if (null == playlistFragment) {
                    playlistFragment = new PlaylistFragment();
                }
            }
        }
        return playlistFragment;
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void initInjector() {

    }

    @Override
    protected void initViews() {

    }
}

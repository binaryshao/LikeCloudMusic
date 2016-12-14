package sbingo.likecloudmusic.ui.fragment.LocalMusic;

import sbingo.likecloudmusic.R;
import sbingo.likecloudmusic.ui.fragment.BaseFragment;

/**
 * Author: Sbingo
 * Date:   2016/12/14
 */

public class LocalMusicFragment extends BaseFragment {


    private static LocalMusicFragment localMusicFragment;

    public static LocalMusicFragment getInstance() {
        if (null == localMusicFragment) {
            synchronized (LocalMusicFragment.class) {
                if (null == localMusicFragment) {
                    localMusicFragment = new LocalMusicFragment();
                }
            }
        }
        return localMusicFragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.local_music_layout;
    }

    @Override
    protected void initInjector() {

    }

    @Override
    protected void initViews() {

    }
}

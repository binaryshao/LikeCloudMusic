package sbingo.likecloudmusic.ui.fragment.NetMusic;

import sbingo.likecloudmusic.R;
import sbingo.likecloudmusic.ui.fragment.BaseFragment;

/**
 * Author: Sbingo
 * Date:   2016/12/14
 */

public class NetMusicFragment extends BaseFragment {

    private static NetMusicFragment netMusicFragment;

    public static NetMusicFragment getInstance() {
        if (null == netMusicFragment) {
            synchronized (NetMusicFragment.class){
                if (null == netMusicFragment) {
                    netMusicFragment = new NetMusicFragment();
                }
            }
        }
        return netMusicFragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.net_music_layout;
    }

    @Override
    protected void initInjector() {

    }

    @Override
    protected void initViews() {

    }

}

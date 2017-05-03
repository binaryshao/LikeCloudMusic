package sbingo.likecloudmusic.ui.fragment.NetMusic;

import sbingo.likecloudmusic.ui.fragment.BaseFragment;

/**
 * Author: Sbingo
 * Date:   2016/12/14
 */

public class RadioFragment extends BaseFragment {

    private static RadioFragment radioFragment;

    public static RadioFragment getInstance() {
        if (null == radioFragment) {
            synchronized (RadioFragment.class){
                if (null == radioFragment) {
                    radioFragment = new RadioFragment();
                }
            }
        }
        return radioFragment;
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

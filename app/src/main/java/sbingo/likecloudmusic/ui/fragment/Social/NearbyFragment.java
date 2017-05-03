package sbingo.likecloudmusic.ui.fragment.Social;

import sbingo.likecloudmusic.ui.fragment.BaseFragment;

/**
 * Author: Sbingo
 * Date:   2016/12/14
 */

public class NearbyFragment extends BaseFragment {

    private static NearbyFragment nearbyFragment;

    public static NearbyFragment getInstance() {
        if (null == nearbyFragment) {
            synchronized (NearbyFragment.class){
                if (null == nearbyFragment) {
                    nearbyFragment = new NearbyFragment();
                }
            }
        }
        return nearbyFragment;
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

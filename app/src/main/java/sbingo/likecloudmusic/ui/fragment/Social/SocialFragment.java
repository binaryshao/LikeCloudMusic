package sbingo.likecloudmusic.ui.fragment.Social;

import sbingo.likecloudmusic.R;
import sbingo.likecloudmusic.ui.fragment.BaseFragment;

/**
 * Author: Sbingo
 * Date:   2016/12/14
 */

public class SocialFragment extends BaseFragment {

    private static SocialFragment socialFragment;

    public static SocialFragment getInstance() {
        if (null == socialFragment) {
            synchronized (SocialFragment.class) {
                if (null == socialFragment) {
                    socialFragment = new SocialFragment();
                }
            }
        }
        return socialFragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.social_layout;
    }

    @Override
    protected void initInjector() {

    }

    @Override
    protected void initViews() {

    }
}

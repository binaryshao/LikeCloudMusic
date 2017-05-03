package sbingo.likecloudmusic.ui.fragment.NetMusic;

import sbingo.likecloudmusic.ui.fragment.BaseFragment;

/**
 * Author: Sbingo
 * Date:   2016/12/14
 */

public class RecommendFragment extends BaseFragment {

    private static RecommendFragment recommendFragment;

    public static RecommendFragment getInstance() {
        if (null == recommendFragment) {
            synchronized (RecommendFragment.class){
                if (null == recommendFragment) {
                    recommendFragment = new RecommendFragment();
                }
            }
        }
        return recommendFragment;
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

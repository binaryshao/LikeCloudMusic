package sbingo.likecloudmusic.ui.fragment.NetMusic;

import sbingo.likecloudmusic.ui.fragment.BaseFragment;

/**
 * Author: Sbingo
 * Date:   2016/12/14
 */

public class RankingFragment extends BaseFragment {

    private static RankingFragment rankingFragment;

    public static RankingFragment getInstance() {
        if (null == rankingFragment) {
            synchronized (RankingFragment.class){
                if (null == rankingFragment) {
                    rankingFragment = new RankingFragment();
                }
            }
        }
        return rankingFragment;
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

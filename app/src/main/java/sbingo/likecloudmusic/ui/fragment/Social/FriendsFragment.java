package sbingo.likecloudmusic.ui.fragment.Social;

import sbingo.likecloudmusic.ui.fragment.BaseFragment;

/**
 * Author: Sbingo
 * Date:   2016/12/14
 */

public class FriendsFragment extends BaseFragment {

    private static FriendsFragment friendsFragment;

    public static FriendsFragment getInstance() {
        if (null == friendsFragment) {
            synchronized (FriendsFragment.class){
                if (null == friendsFragment) {
                    friendsFragment = new FriendsFragment();
                }
            }
        }
        return friendsFragment;
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

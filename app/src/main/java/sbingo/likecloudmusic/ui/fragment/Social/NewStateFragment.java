package sbingo.likecloudmusic.ui.fragment.Social;

import rx.subscriptions.CompositeSubscription;
import sbingo.likecloudmusic.ui.fragment.BaseFragment;

/**
 * Author: Sbingo
 * Date:   2016/12/14
 */

public class NewStateFragment extends BaseFragment {

    private static NewStateFragment newStateFragment;

    public static NewStateFragment getInstance() {
        if (null == newStateFragment) {
            synchronized (NewStateFragment.class){
                if (null == newStateFragment) {
                    newStateFragment = new NewStateFragment();
                }
            }
        }
        return newStateFragment;
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

    @Override
    protected CompositeSubscription provideSubscription() {
        return null;
    }
}

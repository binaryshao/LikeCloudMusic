package sbingo.likecloudmusic.ui.fragment;

import android.support.v4.app.Fragment;

import rx.Subscription;
import sbingo.likecloudmusic.utils.RxUtils;

/**
 * Author: Sbingo
 * Date:   2016/12/12
 */

public class BaseFragment extends Fragment {
    protected Subscription mSubscription;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxUtils.unSubscribe(mSubscription);
    }
}

package sbingo.likecloudmusic.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orhanobut.logger.Logger;

import butterknife.ButterKnife;
import rx.Subscription;
import sbingo.likecloudmusic.common.MyApplication;
import sbingo.likecloudmusic.di.component.DaggerFragmentComponent;
import sbingo.likecloudmusic.di.component.FragmentComponent;
import sbingo.likecloudmusic.di.module.FragmentModule;
import sbingo.likecloudmusic.utils.RxUtils;

/**
 * Author: Sbingo
 * Date:   2016/12/12
 */

public abstract class BaseFragment extends Fragment {

    protected Subscription mSubscription;

    protected abstract int getLayoutId();

    protected abstract void initInjector();

    protected abstract void initViews();

    protected Context mContext;

    private View mFragmentView;

    protected FragmentComponent mFragmentComponent;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mFragmentView == null) {
            mFragmentView = inflater.inflate(getLayoutId(), container, false);
            ButterKnife.bind(this, mFragmentView);
            initFragmentComponent();
            initInjector();
            initViews();
        }
        return mFragmentView;
    }

    private void initFragmentComponent() {
        mFragmentComponent = DaggerFragmentComponent.builder()
                .applicationComponent(((MyApplication) getActivity().getApplication()).getmApplicationComponent())
                .fragmentModule(new FragmentModule(this))
                .build();
    }

    protected void openActivity(Class a) {
        Intent intent = new Intent(mContext, a);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxUtils.unSubscribe(mSubscription);
    }
}

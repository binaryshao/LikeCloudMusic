package sbingo.likecloudmusic.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import rx.subscriptions.CompositeSubscription;
import sbingo.likecloudmusic.common.MyApplication;
import sbingo.likecloudmusic.di.component.DaggerFragmentComponent;
import sbingo.likecloudmusic.di.component.FragmentComponent;
import sbingo.likecloudmusic.di.module.FragmentModule;
import sbingo.likecloudmusic.utils.RemindUtils;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Author: Sbingo
 * Date:   2016/12/12
 */

public abstract class BaseFragment extends Fragment {

    protected CompositeSubscription mSubscriptions;

    protected abstract int getLayoutId();

    protected abstract void initInjector();

    protected abstract void initViews();

    protected abstract CompositeSubscription provideSubscription();

    protected Context mContext;

    private View mFragmentView;

    protected FragmentComponent mFragmentComponent;

    private final int PERMISSION_CODE = 100;

    private PermissionListener mPermissionListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
        mSubscriptions = new CompositeSubscription();
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
                .applicationComponent(((MyApplication) getActivity().getApplication()).getApplicationComponent())
                .fragmentModule(new FragmentModule(this))
                .build();
    }

    protected void openActivity(Class a) {
        Intent intent = new Intent(mContext, a);
        startActivity(intent);
    }

    //运行时权限
    protected void checkPermissions(String[] permissions, PermissionListener listener) {
        mPermissionListener = listener;
        List<String> deniedPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(getActivity(), permission) != PERMISSION_GRANTED) {
                deniedPermissions.add(permission);
            }
        }
        if (deniedPermissions.isEmpty()) {
            mPermissionListener.onGranted();
        } else {
            requestPermissions(deniedPermissions.toArray(new String[deniedPermissions.size()]), PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODE:
                if (grantResults.length > 0) {
                    List<String> deniedPermissions = new ArrayList<>();
                    for (int i = 0; i < grantResults.length; i++) {
                        String permission = permissions[i];
                        int grantResult = grantResults[i];
                        if (grantResult != PERMISSION_GRANTED) {
                            deniedPermissions.add(permission);
                        }
                    }
                    if (deniedPermissions.isEmpty()) {
                        mPermissionListener.onGranted();
                    } else {
                        mPermissionListener.onDenied(deniedPermissions);
                    }
                }
                break;
            default:
        }
    }

    protected interface PermissionListener {
        void onGranted();

        void onDenied(List<String> permissions);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSubscriptions.clear();
        if (provideSubscription() != null) {
            provideSubscription().clear();
        }
    }
}

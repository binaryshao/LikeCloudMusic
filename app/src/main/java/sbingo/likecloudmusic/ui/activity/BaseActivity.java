package sbingo.likecloudmusic.ui.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import butterknife.ButterKnife;
import rx.subscriptions.CompositeSubscription;
import sbingo.likecloudmusic.R;
import sbingo.likecloudmusic.common.Constants;
import sbingo.likecloudmusic.common.MyApplication;
import sbingo.likecloudmusic.di.component.ActivityComponent;
import sbingo.likecloudmusic.di.component.DaggerActivityComponent;
import sbingo.likecloudmusic.di.module.ActivityModule;
import sbingo.likecloudmusic.utils.PreferenceUtils;

/**
 * Author: Sbingo
 * Date:   2016/12/11
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected abstract int getLayoutId();

    protected abstract void initInjector();

    protected abstract void initViews();

    protected abstract void customToolbar();

    protected abstract boolean hasToolbar();

    protected abstract CompositeSubscription provideSubscription();

    protected CompositeSubscription mSubscriptions;

    protected ActionBar actionBar;

    protected ActivityComponent mActivityComponent;

    private boolean mIsAddedView;
    private WindowManager mWindowManager = null;
    private View mNightView = null;
    NavigationView baseNavView;
    SwitchCompat nightSwitch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarTranslucent();
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        initToolbar();
        customToolbar();
        mSubscriptions = new CompositeSubscription();
        initActivityComponent();
        initInjector();
        initViews();
        initNightMode();
    }

    private void initToolbar() {
        if (hasToolbar()) {
            setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
            actionBar = getSupportActionBar();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    protected void setStatusBarTranslucent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.colorPrimary);
        }
    }

    private void initActivityComponent() {
        mActivityComponent = DaggerActivityComponent.builder()
                .applicationComponent(((MyApplication) getApplication()).getApplicationComponent())
                .activityModule(new ActivityModule(this))
                .build();
    }

    protected void openActivity(Class a) {
        Intent intent = new Intent(this, a);
        startActivity(intent);
    }

    void initNightMode() {
        if (this instanceof MainActivity) {
            nightSwitch = (SwitchCompat) MenuItemCompat.getActionView(baseNavView.getMenu().findItem(R.id.night_mode));
            if (PreferenceUtils.getBoolean(this, Constants.IS_NIGHT)) {
                nightSwitch.setChecked(true);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                initNightView();
                mNightView.setBackgroundResource(R.color.night_mask);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }

            nightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        switchToNight();
                    } else {
                        switchToDay();
                    }
                    recreate();
                }
            });
        }
    }

    void switchToNight() {
        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        initNightView();
        mNightView.setBackgroundResource(R.color.night_mask);
        PreferenceUtils.putBoolean(this, Constants.IS_NIGHT, true);
    }

    void switchToDay() {
        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        mNightView.setBackgroundResource(R.color.transparent);
        PreferenceUtils.putBoolean(this, Constants.IS_NIGHT, false);
    }

    private void initNightView() {
        if (mIsAddedView) {
            return;
        }
        // 增加夜间模式蒙板
        WindowManager.LayoutParams nightViewParam = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSPARENT);
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mNightView = new View(this);
        mWindowManager.addView(mNightView, nightViewParam);
        mIsAddedView = true;
    }

    private void removeNightModeMask() {
        if (mIsAddedView) {
            // 移除夜间模式蒙板
            mWindowManager.removeViewImmediate(mNightView);
            mWindowManager = null;
            mNightView = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubscriptions.clear();
        if (provideSubscription() != null) {
            provideSubscription().clear();
        }
        removeNightModeMask();
    }
}

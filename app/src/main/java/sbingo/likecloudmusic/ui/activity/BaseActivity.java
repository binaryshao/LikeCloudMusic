package sbingo.likecloudmusic.ui.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
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

    protected NavigationView baseNavView;
    protected SwitchCompat nightSwitch;
    protected static boolean isModeChanged;

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
        if (PreferenceUtils.getBoolean(this, Constants.IS_NIGHT)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        if (this instanceof MainActivity && baseNavView != null) {
            nightSwitch = (SwitchCompat) MenuItemCompat.getActionView(baseNavView.getMenu().findItem(R.id.night_mode));
            nightSwitch.setChecked(PreferenceUtils.getBoolean(this, Constants.IS_NIGHT) ? true : false);
            nightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        switchToNight();
                    } else {
                        switchToDay();
                    }
                    isModeChanged = true;
                    recreate();
                }
            });
        }
    }

    void switchToNight() {
        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        PreferenceUtils.putBoolean(this, Constants.IS_NIGHT, true);
    }

    void switchToDay() {
        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        PreferenceUtils.putBoolean(this, Constants.IS_NIGHT, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubscriptions.clear();
        if (provideSubscription() != null) {
            provideSubscription().clear();
        }
    }
}

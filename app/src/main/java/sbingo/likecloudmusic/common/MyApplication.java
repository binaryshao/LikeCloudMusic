package sbingo.likecloudmusic.common;

import android.app.Application;
import android.content.Context;

import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

import sbingo.likecloudmusic.di.component.ApplicationComponent;
import sbingo.likecloudmusic.di.component.DaggerApplicationComponent;
import sbingo.likecloudmusic.di.module.ApplicationModule;

/**
 * Author: Sbingo
 * Date:   2016/12/11
 */

public class MyApplication extends Application {

    public static long mLastClickTime = 0;

    private static Context appContext;

    private ApplicationComponent mApplicationComponent;


    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
        initLogger();
        initApplicationComponent();
    }

    public static Context getAppContext() {
        return appContext;
    }

    public ApplicationComponent getmApplicationComponent() {
        return mApplicationComponent;
    }

    private void initLogger() {
        LogLevel lever = LogLevel.FULL;
        Logger.init("MyLogger")                 // default PRETTYLOGGER or use just init()
                .methodCount(3)                 // default 2
                .logLevel(lever);      // default LogLevel.FULL
    }

    private void initApplicationComponent() {
        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }
}

package sbingo.likecloudmusic.common;

import android.app.Application;
import android.content.Context;

import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

/**
 * Author: Sbingo
 * Date:   2016/12/11
 */

public class MyApplication extends Application {

    public static long mLastClickTime = 0;

    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
        initLogger();
    }

    public static Context getAppContext() {
        return appContext;
    }

    private void initLogger() {
        LogLevel lever = LogLevel.FULL;
        Logger.init("MyLogger")                 // default PRETTYLOGGER or use just init()
                .methodCount(3)                 // default 2
                .logLevel(lever);      // default LogLevel.FULL
    }
}

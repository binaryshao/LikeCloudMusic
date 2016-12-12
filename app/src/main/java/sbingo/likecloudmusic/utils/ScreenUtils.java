package sbingo.likecloudmusic.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

import sbingo.likecloudmusic.common.MyApplication;

/**
 * Author: Sbingo
 * Date:   2016/12/12
 */

public class ScreenUtils {

    private static DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    public static int getScreenWidth() {
        return getDisplayMetrics(MyApplication.getAppContext()).widthPixels;
    }

    public static int getScreenHeight() {
        return getDisplayMetrics(MyApplication.getAppContext()).heightPixels;
    }

    public static float getDensity() {
        return getDisplayMetrics(MyApplication.getAppContext()).density;
    }

    public static int dp2px(float dp) {
        return (int) (dp * getDensity() + 0.5f);
    }

    public static int px2dp(float px) {
        return (int) (px / getDensity() + 0.5f);
    }
}

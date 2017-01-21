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

    public static int getScreenWidth(Context context) {
        return getDisplayMetrics(context).widthPixels;
    }

    public static int getScreenHeight(Context context) {
        return getDisplayMetrics(context).heightPixels;
    }

    public static float getDensity(Context context) {
        return getDisplayMetrics(context).density;
    }

    public static int dp2px(Context context,float dp) {
        return (int) (dp * getDensity(context) + 0.5f);
    }

    public static int px2dp(Context context,float px) {
        return (int) (px / getDensity(context) + 0.5f);
    }
}

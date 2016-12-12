package sbingo.likecloudmusic.utils;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;

import sbingo.com.mylibrary.CustomSnackbar;
import sbingo.com.mylibrary.CustomToast;
import sbingo.likecloudmusic.common.MyApplication;

/**
 * Author: Sbingo
 * Date:   2016/12/12
 */

public class RemindUtils {

    private static Snackbar snackbar;

    public static void showToast(String msg) {
        new CustomToast(MyApplication.getAppContext()).makeText(msg).show();
    }

    public static void showCustomToast(String msg, int layoutId, int textId, @CustomToast.Duration int duration) {
        new CustomToast(MyApplication.getAppContext())
                .setLayoutResource(layoutId)
                .setToastId(textId)
                .makeText(msg)
                .setDuration(duration)
                .show();
    }

    public static Snackbar makeSnackbar(View v, final String msg, String action, Snackbar.Callback callback) {
        return snackbar = Snackbar.make(v, msg, Snackbar.LENGTH_LONG)
                .setAction(action, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                    }
                })
                .setActionTextColor(Color.RED)
                .setCallback(callback);
    }

    public static void showCustomSnackbar(View v, final String msg, String action, int backgroudColor, int messageColor, int layoutId, Snackbar.Callback callback) {
        CustomSnackbar.getInstance(makeSnackbar(v, msg, action, callback))
                .setBackgroundColor(backgroudColor)
                .setMessageColor(messageColor)
                .addView(layoutId, 0)
                .show();
    }
}

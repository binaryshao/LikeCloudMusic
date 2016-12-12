package sbingo.likecloudmusic.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import sbingo.likecloudmusic.common.MyApplication;

/**
 * Author: Sbingo
 * Date:   2016/12/12
 */

public class NetUtils {

    public static void isNetworkConnected() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) MyApplication.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnectedOrConnecting()) {
            throw new RuntimeException("连接失败,请检查您的网络连接!");
        }
    }
}

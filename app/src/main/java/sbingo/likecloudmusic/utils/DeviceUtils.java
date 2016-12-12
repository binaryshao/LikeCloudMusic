package sbingo.likecloudmusic.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;

import sbingo.likecloudmusic.common.MyApplication;

/**
 * Author: Sbingo
 * Date:   2016/12/12
 */

public class DeviceUtils {

    private static PackageInfo packageInfo;

    private static PackageInfo getPackageInfo() {
        if (packageInfo == null) {
            try {
                packageInfo = MyApplication.getAppContext().getPackageManager().getPackageInfo(MyApplication.getAppContext().getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return packageInfo;
    }

    /**
     * 获取版本号
     *
     * @return
     */
    public static String getAppVersion() {
        return getPackageInfo().versionName;
    }

    public static String getAppName() {
        return getPackageInfo().packageName;
    }

    public static String getImei() {
        String imei = ((TelephonyManager) MyApplication.getAppContext().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        return imei;
    }

}

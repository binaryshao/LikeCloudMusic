package sbingo.likecloudmusic.utils;

import java.text.DecimalFormat;

/**
 * Author: Sbingo
 * Date:   2016/12/12
 */

public class NumberUtils {

    public static String decimalFormat(float f) {
        DecimalFormat myformat = new DecimalFormat();
        myformat.applyPattern("0.00");
        return myformat.format(f);
    }

    public static String noDecimal(float f) {
        DecimalFormat myformat = new DecimalFormat();
        myformat.applyPattern("0");
        return myformat.format(f);
    }

    /**
     * 去除末尾的0或小数点
     *
     * @param a
     * @return
     */
    public static String noZeroDecimal(String a) {
        if (!a.contains(".")) {
            return a;
        } else {
            StringBuffer out = new StringBuffer(a);
            int point = out.indexOf(".");
            for (int i = out.length() - 1; i >= point; i--) {
                if (out.charAt(i) == '0' || out.charAt(i) == '.') {
                    out.deleteCharAt(i);
                    continue;
                }
                break;
            }
            return out.toString();
        }
    }
}

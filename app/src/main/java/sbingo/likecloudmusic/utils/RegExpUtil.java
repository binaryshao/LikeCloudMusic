package sbingo.likecloudmusic.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: Sbingo
 * Date:   2016/12/12
 */

public class RegExpUtil {

    /**
     * 验证是否是手机号码
     * @param str
     * @return
     */
    public static boolean isMobile(String str) {
        //Pattern pattern = Pattern.compile("^((13[0-9])|(15[0-9])|(18[0-9])|(17[0-9])|(14[0-9]))\\d{8}$");
        Pattern pattern = Pattern.compile("^[1][34578][0-9]\\d{8}$");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 小数，最多两位小数,如：10.2, 0.25, 20
     * @param str
     */
    public static boolean isDecimalTwo(String str){
        return str.matches("^[1-9]\\d*(\\.\\d{0,2})?|0\\.(0[1-9]?|[1-9]\\d?)?$");
    }
}

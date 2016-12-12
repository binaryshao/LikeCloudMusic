package sbingo.likecloudmusic.utils;

import android.app.Activity;
import android.content.Context;
import android.os.SystemClock;
import android.view.inputmethod.InputMethodManager;

import sbingo.likecloudmusic.common.MyApplication;

/**
 * Author: Sbingo
 * Date:   2016/12/12
 */

public class InputUtils {

    /**
     * 是否点击过快，防手抖
     *
     * @return
     */
    public static boolean isClickTooFast() {
        if (SystemClock.elapsedRealtime() - MyApplication.mLastClickTime < 500) {
            return true;
        }
        MyApplication.mLastClickTime = SystemClock.elapsedRealtime();
        return false;
    }

    /**
     * 判断是否为汉字
     *
     * @param c
     * @return
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);

        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.NUMBER_FORMS;
    }

    /**
     * 检查文字格式
     * 只能是中文、英文字母、数字
     *
     * @param name
     * @return
     */
    public static boolean isTextFormatCorrect(String name) {
        boolean res = true;
        char[] cTemp = name.toCharArray();
        for (int i = 0; i < name.length(); i++) {
            if (!isChinese(cTemp[i]) && !(cTemp[i] >= 32 && cTemp[i] <= 126)) {
                res = false;
                break;
            }
        }
        return res;
    }

    /**
     * 显示或隐藏键盘
     */
    public static void switchSoftInput() {
        InputMethodManager imm = (InputMethodManager) MyApplication.getAppContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }


}

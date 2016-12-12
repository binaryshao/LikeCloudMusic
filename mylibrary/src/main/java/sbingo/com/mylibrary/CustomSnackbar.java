package sbingo.com.mylibrary;

import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Author: Sbingo
 * Date:   2016/8/19
 * Use this class to custom a {@link Snackbar}, such as it's background, message color,etc.
 * Before use the {@link CustomSnackbar}, a standard Snackbar is required.
 * Just as Snackbar, you can use method chaining to create and show a Snackbar in one statement.
 */
public class CustomSnackbar {

    private static Snackbar snackbar;
    private static CustomSnackbar customSnackbar;

    /**
     * get the instance of {@link CustomSnackbar}.
     *
     * @param snackbar a snackbar have been created, it is required.
     * @return
     */
    public static CustomSnackbar getInstance(@NonNull Snackbar snackbar) {
        if (null == customSnackbar) {
            synchronized (CustomSnackbar.class) {
                if (null == customSnackbar) {
                    customSnackbar = new CustomSnackbar();
                }
            }
        }
        CustomSnackbar.snackbar = snackbar;
        return customSnackbar;
    }

    /**
     * set the {@link CustomSnackbar}'s background color
     *
     * @param color the color used as the background
     * @return
     */
    public CustomSnackbar setBackgroundColor(@ColorInt int color) {
        getBackground().setBackgroundColor(color);
        return this;
    }

    /**
     * set the {@link CustomSnackbar}'s background
     *
     * @param drawable the drawable used as the background
     * @return
     */
    public CustomSnackbar setBackground(@DrawableRes Drawable drawable) {
        getBackground().setBackground(drawable);
        return this;
    }

    /**
     * set the message's color
     *
     * @param color the color to be set
     * @return
     */
    public CustomSnackbar setMessageColor(@ColorInt int color) {
        ((TextView) (getBackground().findViewById(R.id.snackbar_text))).setTextColor(color);
        return this;
    }

    /**
     * add a view to the {@link CustomSnackbar}.
     * Notice: This method is not recommended, especially the layout is complicated.
     *
     * @param layoutId the resource id of the layout.
     * @param index    the position at which to add the new view or -1 to add last
     * @return
     */
    public CustomSnackbar addView(int layoutId, int index) {
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) getBackground();
        View addView = LayoutInflater.from(getBackground().getContext()).inflate(layoutId, null);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        p.gravity = Gravity.CENTER_VERTICAL;
        snackbarLayout.addView(addView, index, p);
        return this;
    }

    /**
     * show the {@link CustomSnackbar}.
     */
    public void show() {
        snackbar.show();
    }

    private View getBackground() {
        return snackbar.getView();
    }
}

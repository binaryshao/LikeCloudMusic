package sbingo.likecloudmusic.ui.view;

/**
 * Author: Sbingo
 * Date:   2016/12/12
 */

public interface BaseView<T> {

    void showLoading();

    void hideLoading();

    void showMsg(String msg);
}

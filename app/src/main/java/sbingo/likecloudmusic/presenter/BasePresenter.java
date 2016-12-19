package sbingo.likecloudmusic.presenter;


import sbingo.likecloudmusic.ui.view.BaseView;

/**
 * Author: Sbingo
 * Date:   2016/12/12
 */

public class BasePresenter<T extends BaseView> {

     protected T mView;

    public void attachView(T mView) {
        this.mView = mView;
    }
}

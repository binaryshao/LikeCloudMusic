package sbingo.likecloudmusic.interactor;

import sbingo.likecloudmusic.presenter.BasePresenter;

/**
 * Author: Sbingo
 * Date:   2016/12/12
 */

public class BaseInteractor<T extends BasePresenter> {

    protected T mPresenter;

    public void attachPresenter(T mPresenter) {
        this.mPresenter = mPresenter;
    }

}

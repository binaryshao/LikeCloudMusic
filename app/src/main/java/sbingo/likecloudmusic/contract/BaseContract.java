package sbingo.likecloudmusic.contract;

/**
 * Author: Sbingo
 * Date:   2017/5/3
 */

public interface BaseContract {

    interface BasePresenter<T> {
        void attachView(T view);

        void detachView();
    }

    interface BaseView {
        void onError(String err);

        void onCompleted();
    }
}

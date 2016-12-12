package sbingo.likecloudmusic.network;


import rx.Observer;
import sbingo.likecloudmusic.utils.RemindUtils;

/**
 * Author: Sbingo
 * Date:   2016/12/12
 */

public abstract class RxHttpObserver<T> implements Observer<T> {


    @Override
    public void onError(Throwable e) {
        RemindUtils.showToast(e.getMessage());
    }

}

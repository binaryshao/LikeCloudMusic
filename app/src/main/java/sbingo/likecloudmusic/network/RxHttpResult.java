package sbingo.likecloudmusic.network;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import sbingo.likecloudmusic.bean.ResultBean;

/**
 * Author: Sbingo
 * Date:   2016/12/12
 */

public class RxHttpResult<T> implements Func1<ResultBean<T>, Observable<T>> {
    @Override
    public Observable<T> call(final ResultBean<T> resultBean) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                if (resultBean.isSuccess()) {
                    //注意：data为null时只调用onCompleted().
                    if (resultBean.getData() != null) {
                        subscriber.onNext(resultBean.getData());
                    }
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(new RuntimeException(resultBean.getMessage()));
                }
            }
        });
    }
}

package sbingo.likecloudmusic.utils;

import rx.Subscription;

/**
 * Author: Sbingo
 * Date:   2016/12/12
 */

public class RxUtils {

    public static void unSubscribe(Subscription subscription) {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }
}

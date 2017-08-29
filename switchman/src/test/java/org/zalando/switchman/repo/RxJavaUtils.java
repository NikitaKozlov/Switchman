package org.zalando.switchman.repo;

import rx.Observable;
import rx.Single;

public class RxJavaUtils {
    public static <T> Single<T> createNeverCompletedSingle() {
        return Observable.<T>never().toSingle();
    }
}

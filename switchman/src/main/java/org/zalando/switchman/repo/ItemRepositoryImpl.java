package org.zalando.switchman.repo;

import org.zalando.switchman.Item;
import org.zalando.switchman.ItemId;
import org.zalando.switchman.api.Api;

import java.util.Set;

import rx.Observable;
import rx.Single;
import rx.Subscriber;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;

public class ItemRepositoryImpl implements ItemRepository {

    public ItemRepositoryImpl(final Api api, final ApiErrorConverter apiErrorConverter) {
    }


    @Override
    public Single<Set<? extends Item<ItemId>>> getItemList() {
        return null;
    }

    @Override
    public Observable<Integer> getCounter() {
        return null;
    }

    @Override
    public boolean hasItem(final ItemId id) {
        return false;
    }

    @Override
    public Single<Response> addItem(final ItemId id) {
        return null;
    }

    @Override
    public Single<Response> removeItem(final ItemId id) {
        return null;
    }
}

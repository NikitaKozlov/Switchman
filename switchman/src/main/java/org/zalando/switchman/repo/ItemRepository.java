package org.zalando.switchman.repo;

import org.zalando.switchman.Item;
import org.zalando.switchman.ItemId;

import java.util.List;
import java.util.Set;

import rx.Observable;
import rx.Single;

/**
 * Repository that represents collection on Items that allows to add and remove item from it.
 */
public interface ItemRepository {

    Single<Set<? extends Item<ItemId>>> getItemList();

    /**
     * @return Observable that emits amount of items that expected to be in the collection
     * every time it changes.
     */
    Observable<Integer> getCounter();

    boolean hasItem(ItemId id);

    Single<Response> addItem(ItemId id);

    Single<Response> removeItem(ItemId id);
}

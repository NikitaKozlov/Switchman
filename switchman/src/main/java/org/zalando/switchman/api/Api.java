package org.zalando.switchman.api;

import org.zalando.switchman.Item;
import org.zalando.switchman.ItemId;

import java.util.Set;

import rx.Single;

public interface Api {
    Single<Set<? extends Item<ItemId>>> getItemList();

    Single<ApiResponse> addItem(ItemId id);

    Single<ApiResponse> removeItem(ItemId id);
}

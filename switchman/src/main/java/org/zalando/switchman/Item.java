package org.zalando.switchman;

public interface Item<T extends ItemId> {
    T getId();
}

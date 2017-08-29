package org.zalando.switchman.repo;

import org.zalando.switchman.ItemId;
import org.zalando.switchman.api.Api;


import rx.Single;

public abstract class Command {

    protected final LocalStorage<ItemId> localStorage;
    protected final Api api;
    protected final ApiErrorConverter apiErrorConverter;
    protected final ItemId itemId;
    private final long timestamp;

    public Command(LocalStorage<ItemId> localStorage, Api api, ApiErrorConverter apiErrorConverter,
                   ItemId itemId, long timestamp) {
        this.localStorage = localStorage;
        this.api = api;
        this.apiErrorConverter = apiErrorConverter;
        this.itemId = itemId;
        this.timestamp = timestamp;
    }

    ItemId getKey() {
        return itemId;
    }

    abstract void preExecute();

    abstract Single<Response> execute();

    long getTimestamp() {
        return timestamp;
    }

    /**
     * Works same as equals, but doesn't take into account "timestamp".
     */
    boolean isSame(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Command command = (Command) o;

        return itemId.equals(command.itemId);
    }

    boolean hasSameItemId(Command other) {
        return itemId.equals(other.itemId);
    }

    boolean isNotBefore(Command command) {
        return timestamp >= command.getTimestamp();
    }
}

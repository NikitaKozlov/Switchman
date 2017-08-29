package org.zalando.switchman.repo;

import org.zalando.switchman.ItemId;
import org.zalando.switchman.api.Api;
import org.zalando.switchman.api.ApiResponse;
import org.zalando.switchman.api.HttpStatus;

import rx.Single;

/**
 * Represent a command for removing. It is responsible for interacting with {@link Api#removeItem(ItemId)}
 * while keeping the {@link LocalStorage} in the proper state.
 */
public class Remove extends Command {

    Remove(final ItemId itemId, final long timestamp, final LocalStorage<ItemId> localStorage,
                  final Api api, final ApiErrorConverter apiErrorConverter) {
        super(localStorage, api, apiErrorConverter, itemId, timestamp);
    }

    @Override
    void preExecute() {
        localStorage.addItemIdForRemoving(itemId);
        localStorage.removeItemIdFromAddition(itemId);
        localStorage.publishCounter();
    }

    @Override
    Single<Response> execute() {
        return api.removeItem(itemId)
                .doOnError(err -> localStorage.removeItemIdFromRemoving(itemId))
                .map(response -> {
                    if (response.isSuccessful() || isAlreadyRemoved(response)) {
                        localStorage.removeItemIdFromRemovalAndAdded(itemId);
                        return Response.createSuccessfulResponse();
                    } else {
                        localStorage.removeItemIdFromRemoving(itemId);
                        return Response.createFailedResponse(
                                apiErrorConverter.convertApiError(response.getCause()));
                    }
                })
                .onErrorReturn(throwable -> Response.createFailedResponse(
                        apiErrorConverter.convertApiError(new ExceptionApiError(throwable))));
    }

    private boolean isAlreadyRemoved(ApiResponse response) {
        return response.getCode() == HttpStatus.NOT_FOUND.value();
    }

    @Override
    public String toString() {
        return "Remove{itemId = " + getKey() + "}";
    }
}

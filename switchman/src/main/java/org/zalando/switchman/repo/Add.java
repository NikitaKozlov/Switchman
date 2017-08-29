package org.zalando.switchman.repo;

import org.zalando.switchman.ItemId;
import org.zalando.switchman.api.Api;
import org.zalando.switchman.api.ApiResponse;
import org.zalando.switchman.api.HttpStatus;

import rx.Single;

/**
 * Represent a command for addition. It is responsible for interacting with {@link Api#addItem(ItemId)}
 * while keeping the {@link LocalStorage} in the proper state.
 */
public class Add extends Command {

    Add(final ItemId itemId, final long timestamp, final LocalStorage<ItemId> localStorage,
               final Api api, final ApiErrorConverter apiErrorConverter) {
        super(localStorage, api, apiErrorConverter, itemId, timestamp);
    }

    @Override
    void preExecute() {
        localStorage.addItemIdForAddition(itemId);
        localStorage.removeItemIdFromRemoving(itemId);
        localStorage.publishCounter();
    }

    @Override
    Single<Response> execute() {
        return api.addItem(itemId)
                .doOnError(err -> localStorage.removeItemIdFromAddition(itemId))
                .map(response -> {
                    if (response.isSuccessful() || isAlreadyAdded(response)) {
                        localStorage.moveItemIdFromAdditionToAdded(itemId);
                        return Response.createSuccessfulResponse();
                    } else {
                        localStorage.removeItemIdFromAddition(itemId);
                        return Response.createFailedResponse(
                                apiErrorConverter.convertApiError(response.getCause()));
                    }
                })
                .onErrorReturn(throwable -> Response.createFailedResponse(
                        apiErrorConverter.convertApiError(new ExceptionApiError(throwable))));
    }

    private boolean isAlreadyAdded(ApiResponse response) {
        return response.getCode() == HttpStatus.CONFLICT.value();
    }

    @Override
    public String toString() {
        return "Add{itemId = " + getKey() + "}";
    }
}

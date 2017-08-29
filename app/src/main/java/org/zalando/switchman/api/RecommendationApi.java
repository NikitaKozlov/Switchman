package org.zalando.switchman.api;

import android.util.Log;

import org.zalando.switchman.Item;
import org.zalando.switchman.ItemId;

import java.util.HashSet;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Single;

public class RecommendationApi implements Api {

    private final String TAG = "Switchman";

    private final CompleteItemProvider completeItemProvider;
    private Set<ItemId> itemIds = new CopyOnWriteArraySet<>();

    private final Random random = new Random();

    public RecommendationApi(CompleteItemProvider completeItemProvider) {
        this.completeItemProvider = completeItemProvider;
    }

    @Override
    public Single<Set<? extends Item<ItemId>>> getItemList() {
        return Observable.from(itemIds)
                .flatMap(id -> completeItemProvider.getCompleteItem(id).toObservable())
                .toList()
                .toSingle()
                .map(HashSet::new);
    }

    @Override
    public Single<ApiResponse> addItem(ItemId id) {
        return prepareNextResponse(id, String.format(Locale.US,"Failed to recommend %s", id.toString()))
                .map(it -> {
                    if (itemIds.contains(it)) {
                        return ApiResponse.createFailedResponse(HttpStatus.CONFLICT.value(),
                                new ApiResponse.ApiError() { });
                    }
                    itemIds.add(it);
                    Log.d(TAG, String.format(Locale.US,"%s was recommended", id.toString()));
                    return ApiResponse.createSuccessfulResponse();
                });
    }

    @Override
    public Single<ApiResponse> removeItem(ItemId id) {
        return prepareNextResponse(id, String.format(Locale.US,"Failed to stop recommending %s", id.toString()))
                .map(it -> {
                    if (!itemIds.contains(it)) {
                        return ApiResponse.createFailedResponse(HttpStatus.NOT_FOUND.value(),
                                new ApiResponse.ApiError() { });
                    }
                    itemIds.remove(it);
                    Log.d(TAG, String.format(Locale.US,"%s is not recommended anymore", id.toString()));
                    return ApiResponse.createSuccessfulResponse();
                });
    }

    /**
     * @return Simulates delay and errors from API.
     */
    private Single<ItemId> prepareNextResponse(ItemId id, String logMessageIfFailed) {
        return Single.just(id)
                .delay(getRandomDelay(), TimeUnit.MILLISECONDS)
                .doOnSuccess(ignored -> {
                    if (isRequestFailed()) {
                        Log.d(TAG, logMessageIfFailed);
                        throw new RuntimeException("Something went wrong on backend side");
                    }
                });
    }

    private int getRandomDelay() {
        return random.nextInt(500) + 500;
    }

    private boolean isRequestFailed() {
        return random.nextFloat() > 0.8f;
    }
}

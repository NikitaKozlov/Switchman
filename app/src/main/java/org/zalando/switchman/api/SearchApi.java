package org.zalando.switchman.api;

import org.zalando.switchman.Recipe;

import java.util.List;

import rx.Single;

public class SearchApi {

    private final CompleteItemProvider completeItemProvider;

    public SearchApi(CompleteItemProvider completeItemProvider) {
        this.completeItemProvider = completeItemProvider;
    }

    public Single<List<Recipe>> search() {
        return completeItemProvider.getAllCompleteItems();
    }

}

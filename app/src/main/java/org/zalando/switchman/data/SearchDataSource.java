package org.zalando.switchman.data;

import org.zalando.switchman.Recipe;
import org.zalando.switchman.api.SearchApi;

import java.util.List;

import rx.Single;

public class SearchDataSource {

    private final SearchApi searchApi;

    public SearchDataSource(SearchApi searchApi) {
        this.searchApi = searchApi;
    }

    public Single<List<Recipe>> search() {
        return searchApi.search();
    }
}

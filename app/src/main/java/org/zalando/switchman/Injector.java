package org.zalando.switchman;

import org.zalando.switchman.api.ApiErrorConverterImpl;
import org.zalando.switchman.api.CompleteItemProvider;
import org.zalando.switchman.api.RecommendationApi;
import org.zalando.switchman.api.SearchApi;
import org.zalando.switchman.data.RecommendationDataSource;
import org.zalando.switchman.data.SearchDataSource;

public final class Injector {
    private static CompleteItemProvider COMPLETE_ITEM_PROVIDER = new CompleteItemProvider();

    private Injector() {}
    public static RecommendationDataSource createRecommendationDataSource() {
        return new RecommendationDataSource(new RecommendationApi(COMPLETE_ITEM_PROVIDER), new ApiErrorConverterImpl());
    }

    public static SearchDataSource createSearchDataSource() {
        return new SearchDataSource(new SearchApi(COMPLETE_ITEM_PROVIDER));
    }
}

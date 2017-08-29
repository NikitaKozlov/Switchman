package org.zalando.switchman.data;

import org.zalando.switchman.api.Api;
import org.zalando.switchman.repo.ApiErrorConverter;
import org.zalando.switchman.repo.ItemRepositoryImpl;

public class RecommendationDataSource extends ItemRepositoryImpl {
    public RecommendationDataSource(Api api, ApiErrorConverter apiErrorConverter) {
        super(api, apiErrorConverter);
    }
}

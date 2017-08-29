package org.zalando.switchman.ui;

import org.zalando.switchman.ItemId;
import org.zalando.switchman.data.RecommendationDataSource;

public class RecommendationStateChecker {
    private final RecommendationDataSource recommendationDataSource;

    public RecommendationStateChecker(RecommendationDataSource recommendationDataSource) {
        this.recommendationDataSource = recommendationDataSource;
    }

    public boolean isRecommended(ItemId itemId) {
        return recommendationDataSource.hasItem(itemId);
    }
}

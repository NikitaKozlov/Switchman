package org.zalando.switchman.ui;

import org.zalando.switchman.Recipe;

import java.util.List;

interface MainView {
    void displayRecipes(List<Recipe> recipes, RecommendationStateChecker recommendationStateChecker, RecommendationView.RecommendationListener recommendationListener);

    void showNotification(String message);
}

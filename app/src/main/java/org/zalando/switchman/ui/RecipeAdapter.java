package org.zalando.switchman.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.zalando.switchman.R;
import org.zalando.switchman.Recipe;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeViewHolder> {

    private final List<Recipe> recipes;
    private final RecommendationStateChecker recommendationStateChecker;
    private final RecommendationView.RecommendationListener recommendationListener;

    public RecipeAdapter(List<Recipe> recipes, RecommendationStateChecker recommendationStateChecker,
                         RecommendationView.RecommendationListener recommendationListener) {
        this.recipes = recipes;
        this.recommendationStateChecker = recommendationStateChecker;
        this.recommendationListener = recommendationListener;
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new RecipeViewHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recipe_layout, viewGroup, false),
                recommendationStateChecker, recommendationListener);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder recipeViewHolder, int i) {
        recipeViewHolder.bind(recipes.get(i));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }
}

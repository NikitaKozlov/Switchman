package org.zalando.switchman.ui;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.zalando.switchman.R;
import org.zalando.switchman.Recipe;

class RecipeViewHolder extends RecyclerView.ViewHolder {

    private ImageView recipeImage;
    private TextView textView;
    private RecommendationView recommendationView;

    RecipeViewHolder(View itemView, RecommendationStateChecker recommendationStateChecker,
                     RecommendationView.RecommendationListener recommendationListener) {
        super(itemView);
        recipeImage = (ImageView) itemView.findViewById(R.id.recipe_image);
        textView = (TextView) itemView.findViewById(R.id.recipe_title);
        recommendationView = (RecommendationView) itemView.findViewById(R.id.recommendation_view);
        recommendationView.setRecommendationStateChecker(recommendationStateChecker);
        recommendationView.setRecommendationListener(recommendationListener);
    }

    void bind(Recipe recipe) {
        Picasso.with(itemView.getContext()).load(recipe.getUrl()).fit().into(recipeImage);
        textView.setText(recipe.getTitle());

        recommendationView.updateItemId(recipe.getId());
    }
}

package org.zalando.switchman.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import org.zalando.switchman.ItemId;
import org.zalando.switchman.R;
import org.zalando.switchman.Recipe;
import org.zalando.switchman.repo.Response;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity {

    final Presenter presenter = new Presenter();
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));

        presenter.inject();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.search(this);
    }

    void displayRecipes(List<Recipe> recipes, RecommendationStateChecker recommendationStateChecker, RecommendationView.RecommendationListener recommendationListener) {
        recyclerView.setAdapter(
                new RecipeAdapter(recipes, recommendationStateChecker, recommendationListener));
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.getSubscription().clear();
    }

    RecommendationView.RecommendationListener getRecommendationListener() {
        return new RecommendationView.RecommendationListener() {

            @Override
            public void recommend(ItemId itemId, RecommendationView.RequestListener requestListener) {
                presenter.getSubscription().add(presenter.getRecommendationDataSource().addItem(itemId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                response -> onRecommendationRequestSuccess(response, requestListener,
                                        "Due to an error request to start recommending is failed"),
                                throwable -> onRecommendationRequestFail(requestListener, throwable.getMessage())
                        ));
            }

            @Override
            public void unrecommend(ItemId itemId, RecommendationView.RequestListener requestListener) {
                presenter.getSubscription().add(presenter.getRecommendationDataSource().removeItem(itemId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                response -> onRecommendationRequestSuccess(response, requestListener,
                                        "Due to an error request to stop recommending is failed"),
                                throwable -> onRecommendationRequestFail(requestListener, throwable.getMessage())
                        ));
            }
        };
    }

    private void onRecommendationRequestFail(RecommendationView.RequestListener requestListener, String message) {
        requestListener.onFail();
        showNotification(message);
    }

    private void onRecommendationRequestSuccess(Response response,
                                                RecommendationView.RequestListener requestListener,
                                                String errorNotification) {
        if (response.isSuccessful()) {
            requestListener.onSuccess();
        } else if (!response.isSkipped()) {
            onRecommendationRequestFail(requestListener, errorNotification);
        }
    }

    private void showNotification(String message) {
        Toast.makeText(this, message, LENGTH_SHORT).show();
    }
}

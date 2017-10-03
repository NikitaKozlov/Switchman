package org.zalando.switchman.ui;

import org.zalando.switchman.Injector;
import org.zalando.switchman.ItemId;
import org.zalando.switchman.data.RecommendationDataSource;
import org.zalando.switchman.data.SearchDataSource;
import org.zalando.switchman.repo.Response;

import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class Presenter {
    private RecommendationDataSource recommendationDataSource;
    private MainView mainView;

    public RecommendationDataSource getRecommendationDataSource() {
        return recommendationDataSource;
    }

    public void setRecommendationDataSource(RecommendationDataSource recommendationDataSource) {
        this.recommendationDataSource = recommendationDataSource;
    }

    private SearchDataSource searchDataSource;

    public SearchDataSource getSearchDataSource() {
        return searchDataSource;
    }

    public void setSearchDataSource(SearchDataSource searchDataSource) {
        this.searchDataSource = searchDataSource;
    }

    private CompositeSubscription subscription = new CompositeSubscription();

    public CompositeSubscription getSubscription() {
        return subscription;
    }

    public Presenter() {
    }

    void inject() {
        setRecommendationDataSource(Injector.createRecommendationDataSource());
        setSearchDataSource(Injector.createSearchDataSource());
    }

    void search() {
        getSubscription().add(getSearchDataSource().search()
                .subscribe(recipes -> mainView.displayRecipes(recipes, getRecommendationStateChecker(), getRecommendationListener())));
    }

    RecommendationStateChecker getRecommendationStateChecker() {
        return new RecommendationStateChecker(getRecommendationDataSource());
    }

    RecommendationView.RecommendationListener getRecommendationListener() {
        return new RecommendationView.RecommendationListener() {

            @Override
            public void recommend(ItemId itemId, RecommendationView.RequestListener requestListener) {
                getSubscription().add(getRecommendationDataSource().addItem(itemId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                response -> onRecommendationRequestSuccess(response, requestListener,
                                        "Due to an error request to start recommending is failed"),
                                throwable -> onRecommendationRequestFail(requestListener, throwable.getMessage())
                        ));
            }

            @Override
            public void unrecommend(ItemId itemId, RecommendationView.RequestListener requestListener) {
                getSubscription().add(getRecommendationDataSource().removeItem(itemId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                response -> onRecommendationRequestSuccess(response, requestListener,
                                        "Due to an error request to stop recommending is failed"),
                                throwable -> onRecommendationRequestFail(requestListener, throwable.getMessage())
                        ));
            }
        };
    }

    void onRecommendationRequestSuccess(Response response,
                                        RecommendationView.RequestListener requestListener,
                                        String errorNotification) {
        if (response.isSuccessful()) {
            requestListener.onSuccess();
        } else if (!response.isSkipped()) {
            onRecommendationRequestFail(requestListener, errorNotification);
        }
    }

    void onRecommendationRequestFail(RecommendationView.RequestListener requestListener, String message) {
        requestListener.onFail();
        mainView.showNotification(message);
    }

    void onViewAttached(MainView mainView) {
        this.mainView = mainView;
        search();
    }

    void onViewDetached() {
        this.mainView = null;
        getSubscription().clear();
    }
}
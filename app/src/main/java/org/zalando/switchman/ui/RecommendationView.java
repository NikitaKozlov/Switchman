package org.zalando.switchman.ui;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import org.zalando.switchman.ItemId;
import org.zalando.switchman.R;

public class RecommendationView extends AppCompatImageView {

    private RecommendationStateChecker recommendationStateChecker;

    private ItemId itemId;
    private boolean isRecommended;

    private RequestListenerImpl lastRequestListener;

    public RecommendationView(Context context) {
        super(context);
    }

    public RecommendationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecommendationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setRecommendationListener(final RecommendationListener listener) {
        if (listener == null) {
            setOnClickListener(null);
        } else {
            setOnClickListener(view -> {
                if (isRecommended) {
                    isRecommended = false;
                    listener.unrecommend(itemId, getRequestListener());
                } else {
                    isRecommended = true;
                    listener.recommend(itemId, getRequestListener());
                }

                updateIcon();
            });
        }
    }

    public void setRecommendationStateChecker(RecommendationStateChecker recommendationStateChecker) {
        this.recommendationStateChecker = recommendationStateChecker;
    }

    public void updateItemId(ItemId itemId) {
        this.itemId = itemId;
        lastRequestListener = null;
        updateState();
    }

    private void updateState() {
        setState(recommendationStateChecker.isRecommended(itemId));
    }

    private void setState(boolean isRecommended) {
        this.isRecommended = isRecommended;
        updateIcon();
    }

    private void updateIcon() {
        setImageResource(isRecommended ? R.drawable.ic_star : R.drawable.ic_star_empty);
    }

    private RequestListener getRequestListener() {
        if (lastRequestListener != null) {
            lastRequestListener.disable();
        }

        lastRequestListener = new RequestListenerImpl();
        return lastRequestListener;
    }

    public interface RecommendationListener {
        void recommend(ItemId itemId, RequestListener requestListener);

        void unrecommend(ItemId itemId, RequestListener requestListener);
    }

    public interface RequestListener {
        void onSuccess();

        void onFail();
    }

    private class RequestListenerImpl implements RequestListener {

        private boolean isEnabled = true;

        void disable() {
            isEnabled = false;
        }

        @Override
        public void onSuccess() {
            updateStateIfEnabled();
        }

        @Override
        public void onFail() {
            updateStateIfEnabled();
        }

        private void updateStateIfEnabled() {
            if (isEnabled) {
                updateState();
            }
        }
    }
}

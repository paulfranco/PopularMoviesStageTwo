package co.paulfran.paulfranco.popularmoviesstagetwo.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import co.paulfran.paulfranco.popularmoviesstagetwo.R;
import co.paulfran.paulfranco.popularmoviesstagetwo.holders.ReviewViewHolder;
import co.paulfran.paulfranco.popularmoviesstagetwo.models.Movie.Review;
import co.paulfran.paulfranco.popularmoviesstagetwo.models.Movie.Reviews;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewViewHolder> {
    private Reviews mReviews;

    // constructor
    public ReviewsAdapter(Reviews reviews) {
        this.mReviews = reviews;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_list_item, parent, false);

        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        final Review review = mReviews.getResults().get(position);

        holder.mTvReviewAuthor.setText(review.getAuthor());
        holder.mTvReviewContent.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        return mReviews.getResults().size();
    }

}
package co.paulfran.paulfranco.popularmoviesstagetwo.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import co.paulfran.paulfranco.popularmoviesstagetwo.R;

public class ReviewViewHolder extends RecyclerView.ViewHolder {
    public TextView mTvReviewAuthor;
    public TextView mTvReviewContent;

    public ReviewViewHolder(View itemView) {
        super(itemView);

        mTvReviewAuthor = itemView.findViewById(R.id.tvReviewAuthor);
        mTvReviewContent = itemView.findViewById(R.id.tvReviewContent);
    }
}
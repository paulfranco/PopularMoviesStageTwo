package co.paulfran.paulfranco.popularmoviesstagetwo.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import co.paulfran.paulfranco.popularmoviesstagetwo.R;

public class VideoViewHolder extends RecyclerView.ViewHolder {
    public ImageView mIvVideoThumb;
    public TextView mTvVideoTitle;

    public VideoViewHolder(View itemView) {
        super(itemView);

        mIvVideoThumb = itemView.findViewById(R.id.cvVideo);
        mTvVideoTitle = itemView.findViewById(R.id.tvVideoTitle);
    }
}
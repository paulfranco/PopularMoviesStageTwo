package co.paulfran.paulfranco.popularmoviesstagetwo.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import co.paulfran.paulfranco.popularmoviesstagetwo.R;
import com.squareup.picasso.Picasso;

import co.paulfran.paulfranco.popularmoviesstagetwo.holders.VideoViewHolder;
import co.paulfran.paulfranco.popularmoviesstagetwo.models.Movie.Video;
import co.paulfran.paulfranco.popularmoviesstagetwo.models.Movie.VideoResults;

public class VideosAdapter extends RecyclerView.Adapter<VideoViewHolder> {
    private Context mContext;
    private VideoResults mVideoResults;

    public VideosAdapter(Context context, VideoResults videoResults) {
        this.mContext = context;
        this.mVideoResults = videoResults;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_list_item, parent, false);

        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        final Video video = mVideoResults.getResults().get(position);
        Picasso.with(mContext)
                .load(buildThumbnailUrl(video.getKey()))
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.not_found)
                .into(holder.mIvVideoThumb);

        holder.mTvVideoTitle.setText(video.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=".concat(video.getKey())));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mVideoResults.getResults().size();
    }

    private String buildThumbnailUrl(String videoId) {
        return "https://img.youtube.com/vi/" + videoId + "/hqdefault.jpg";
    }
}
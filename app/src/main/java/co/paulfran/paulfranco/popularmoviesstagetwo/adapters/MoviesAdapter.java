package co.paulfran.paulfranco.popularmoviesstagetwo.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import co.paulfran.paulfranco.popularmoviesstagetwo.api.MoviesAPICallback;
import co.paulfran.paulfranco.popularmoviesstagetwo.api.MoviesAPIManager;
import co.paulfran.paulfranco.popularmoviesstagetwo.controllers.MovieDetailsActivity;
import co.paulfran.paulfranco.popularmoviesstagetwo.controllers.MovieDetailsFragment;
import co.paulfran.paulfranco.popularmoviesstagetwo.controllers.MovieListActivity;
import co.paulfran.paulfranco.popularmoviesstagetwo.holders.MovieViewHolder;
import co.paulfran.paulfranco.popularmoviesstagetwo.models.Movie.Movie;
import co.paulfran.paulfranco.popularmoviesstagetwo.models.Movies;
import co.paulfran.paulfranco.popularmoviesstagetwo.utils.ImageUtils;
import co.paulfran.paulfranco.popularmoviesstagetwo.utils.Misc;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import co.paulfran.paulfranco.popularmoviesstagetwo.R;

public class MoviesAdapter extends RecyclerView.Adapter<MovieViewHolder> {
    private MovieListActivity mParentActivity;
    private Movies mMovies;
    private boolean mTwoPane;
    private Call<Movie> callRequest;

    public MoviesAdapter(MovieListActivity parent, Movies movies, boolean twoPane) {
        this.mParentActivity = parent;
        this.mMovies = movies;
        this.mTwoPane = twoPane;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_item, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MovieViewHolder holder, int position) {
        final int pos = position;
        holder.mIvMovie.post(new Runnable() {
            @Override
            public void run() {
                Picasso.with(mParentActivity.getApplicationContext())
                        .load(ImageUtils.buildPosterImageUrl(mMovies.getResults().get(pos).getPosterPath(), holder.mIvMovie.getWidth()))
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.not_found)
                        .into(holder.mIvMovie);
            }
        });

        holder.itemView.setTag(mMovies.getResults().get(position).getId());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callRequest != null) {
                    callRequest.cancel();
                }

                getMovieAndShowDetails((int) view.getTag(), holder);
            }
        });

        if (position == 0 && mTwoPane) {
            holder.itemView.callOnClick();
        }
    }

    @Override
    public int getItemCount() {
        return mMovies.getResults().size();
    }

    public void updateMovies(Movies movies) {
        int position = this.mMovies.getResults().size() + 1;
        this.mMovies.appendMovies(movies);
        notifyItemRangeInserted(position, movies.getResults().size());
    }

    private void getMovieAndShowDetails(final int movieId, final MovieViewHolder movieViewHolder) {
        final Context context = mParentActivity;

        if (Misc.isNetworkAvailable(context)) {

            movieViewHolder.showProgress(true);
            callRequest = MoviesAPIManager.getInstance().getMovie(movieId, new MoviesAPICallback<Movie>() {
                @Override
                public void onResponse(Movie result) {

                    if (result != null) {
                        if (mTwoPane) {
                            Bundle arguments = new Bundle();
                            arguments.putParcelable(MovieDetailsFragment.EXTRA_MOVIE_KEY, result);
                            MovieDetailsFragment fragment = new MovieDetailsFragment();
                            fragment.setArguments(arguments);
                            mParentActivity.getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.movieDetailContainer, fragment)
                                    .commit();
                        } else {
                            Intent intent = new Intent(context, MovieDetailsActivity.class);
                            intent.putExtra(MovieDetailsFragment.EXTRA_MOVIE_KEY, result);

                            context.startActivity(intent);
                        }
                    } else {
                        Toasty.error(context, "Data is not  Available", Toast.LENGTH_SHORT, true).show();
                    }

                    callRequest = null;
                    movieViewHolder.showProgress(false);
                }

                @Override
                public void onCancel() {
                    callRequest = null;
                    movieViewHolder.showProgress(false);
                }

            });
        } else {
            Toasty.error(context, "No Internet Connection", Toast.LENGTH_SHORT, true).show();
        }

    }
}

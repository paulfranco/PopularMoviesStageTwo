package co.paulfran.paulfranco.popularmoviesstagetwo.adapters;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import co.paulfran.paulfranco.popularmoviesstagetwo.api.MoviesAPICallback;
import co.paulfran.paulfranco.popularmoviesstagetwo.api.MoviesAPIManager;
import co.paulfran.paulfranco.popularmoviesstagetwo.controllers.MovieDetailsActivity;
import co.paulfran.paulfranco.popularmoviesstagetwo.controllers.MovieDetailsFragment;
import co.paulfran.paulfranco.popularmoviesstagetwo.controllers.MovieListActivity;
import co.paulfran.paulfranco.popularmoviesstagetwo.data.MoviesContract;
import co.paulfran.paulfranco.popularmoviesstagetwo.holders.MovieViewHolder;
import co.paulfran.paulfranco.popularmoviesstagetwo.models.Movie.Movie;
import co.paulfran.paulfranco.popularmoviesstagetwo.R;
import co.paulfran.paulfranco.popularmoviesstagetwo.utils.ImageUtils;
import co.paulfran.paulfranco.popularmoviesstagetwo.utils.Misc;
import retrofit2.Call;

public class FavoriteMoviesAdapter extends RecyclerView.Adapter<MovieViewHolder> {
    private MovieListActivity mParentActivity;
    private boolean mTwoPane;

    private List<Movie> mFavouriteMovies;
    private Call<Movie> callRequest;


    public FavoriteMoviesAdapter(MovieListActivity parent, Cursor cursor, boolean twoPane) {
        this.mParentActivity = parent;
        this.mTwoPane = twoPane;

        mFavouriteMovies = new ArrayList<>();
        loadMoviesFromCursor(cursor);

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
                        .load(ImageUtils.buildPosterImageUrl(mFavouriteMovies.get(pos).getPosterPath(), holder.mIvMovie.getWidth()))
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .error(R.drawable.ic_launcher_foreground)
                        .into(holder.mIvMovie);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callRequest != null) {
                    callRequest.cancel();
                }
                getMovieAndShowDetails(pos, holder);
            }
        });

        if (position == 0 && mTwoPane) {
            holder.itemView.callOnClick();
        }
    }

    @Override
    public int getItemCount() {
        return mFavouriteMovies.size();
    }


    private void getMovieAndShowDetails(final int position, final MovieViewHolder movieViewHolder) {
        if (Misc.isNetworkAvailable(mParentActivity)) {
            movieViewHolder.showProgress(true);

            callRequest = MoviesAPIManager.getInstance().getMovie(mFavouriteMovies.get(position).getId(), new MoviesAPICallback<Movie>() {
                @Override
                public void onResponse(Movie result) {
                    if (result != null) {
                        showMovieDetails(result);
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
            showMovieDetails(mFavouriteMovies.get(position));
        }


    }

    private void showMovieDetails(Movie movie) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(MovieDetailsFragment.EXTRA_MOVIE_KEY, movie);
            MovieDetailsFragment fragment = new MovieDetailsFragment();
            fragment.setArguments(arguments);
            mParentActivity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movieDetailContainer, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(mParentActivity, MovieDetailsActivity.class);
            intent.putExtra(MovieDetailsFragment.EXTRA_MOVIE_KEY, movie);

            mParentActivity.startActivity(intent);
        }
    }

    private void loadMoviesFromCursor(Cursor cursor) {
        for (int i = 0; i < cursor.getCount(); i++) {
            int movieIdIndex = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_ID);
            int titleIndex = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_TITLE);
            int overviewIndex = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_OVERVIEW);
            int posterPathIndex = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_POSTER_PATH);
            int backdropPathIndex = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_BACKDROP_PATH);
            int releaseDateIndex = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE);
            int runtimeIndex = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_RUNTIME);
            int voteAverageIndex = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE);

            cursor.moveToPosition(i);

            mFavouriteMovies.add(new Movie(
                    cursor.getInt(movieIdIndex),
                    cursor.getString(titleIndex),
                    cursor.getString(overviewIndex),
                    cursor.getString(posterPathIndex),
                    cursor.getString(backdropPathIndex),
                    cursor.getString(releaseDateIndex),
                    cursor.getInt(runtimeIndex),
                    cursor.getInt(voteAverageIndex),
                    new VideoResults(),
                    new Reviews(),
                    new ArrayList<Genre>()
            ));
        }
    }
}
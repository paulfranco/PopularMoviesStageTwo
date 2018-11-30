package co.paulfran.paulfranco.popularmoviesstagetwo.api;

import android.support.annotation.NonNull;

import com.orhanobut.logger.Logger;

import java.io.Serializable;


import co.paulfran.paulfranco.popularmoviesstagetwo.Constants;
import co.paulfran.paulfranco.popularmoviesstagetwo.R;
import co.paulfran.paulfranco.popularmoviesstagetwo.models.Movie.Movie;
import co.paulfran.paulfranco.popularmoviesstagetwo.models.Movies;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public final class MoviesAPIManager implements Serializable {
    private static volatile MoviesAPIManager sharedInstance = new MoviesAPIManager();

    private MovieAPIService movieApiService;

    private MoviesAPIManager() {

        if (sharedInstance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.TMDB_API_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        movieApiService = retrofit.create(MovieAPIService.class);
    }

    public static MoviesAPIManager getInstance() {
        if (sharedInstance == null) {
            synchronized (MoviesAPIManager.class) {
                if (sharedInstance == null) sharedInstance = new MoviesAPIManager();
            }
        }

        return sharedInstance;
    }

    public enum SortBy {
        MostPopular(0),
        TopRated(1),
        Favorite(2);

        private int mValue;

        // contructor
        SortBy(int value) {
            this.mValue = value;
        }

        public int id() {
            return mValue;
        }

        public static SortBy fromId(int value) {
            for (SortBy color : values()) {
                if (color.mValue == value) {
                    return color;
                }
            }
            return MostPopular;
        }
    }

    public Call<Movie> getMovie(int movieId, final MoviesAPICallback<Movie> moviesApiCallback) {
        Call<Movie> call = movieApiService.getMovie(movieId, Constants.TMDB_API_KEY);

        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(@NonNull Call<Movie> call, @NonNull Response<Movie> response) {
                moviesApiCallback.onResponse(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<Movie> call, @NonNull Throwable t) {
                if (call.isCanceled()) {
                    Logger.e("Request was cancelled");
                    moviesApiCallback.onCancel();
                } else {
                    Logger.e(t.getMessage());
                    moviesApiCallback.onResponse(null);
                }
            }
        });

        return call;
    }

    public void getMovies(SortBy sortBy, int page, MoviesAPICallback<Movies> moviesApiCallback) {

        switch (sortBy) {
            case MostPopular:
                getPopularMovies(page, moviesApiCallback);
                break;
            case TopRated:
                getTopRatedMovies(page, moviesApiCallback);
                break;
        }

    }

    private void getPopularMovies(int page, final MoviesAPICallback<Movies> moviesApiCallback) {
        movieApiService.getPopularMovies(Constants.TMDB_API_KEY, page).enqueue(new Callback<Movies>() {

            @Override
            public void onResponse(@NonNull Call<Movies> call, @NonNull Response<Movies> response) {
                moviesApiCallback.onResponse(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<Movies> call, @NonNull Throwable t) {
                if (call.isCanceled()) {
                    Logger.e("Request was cancelled");
                    moviesApiCallback.onCancel();
                } else {
                    Logger.e(t.getMessage());
                    moviesApiCallback.onResponse(null);
                }
            }

        });
    }

    private void getTopRatedMovies(int page, final MoviesAPICallback<Movies> moviesApiCallback) {
        movieApiService.getTopRatedMovies(Constants.TMDB_API_KEY, page).enqueue(new Callback<Movies>() {

            @Override
            public void onResponse(@NonNull Call<Movies> call, @NonNull Response<Movies> response) {
                moviesApiCallback.onResponse(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<Movies> call, @NonNull Throwable t) {
                if (call.isCanceled()) {
                    Logger.e("Request was cancelled");
                    moviesApiCallback.onCancel();
                } else {
                    Logger.e(t.getMessage());
                    moviesApiCallback.onResponse(null);
                }
            }

        });
    }

}
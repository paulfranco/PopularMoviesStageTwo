package co.paulfran.paulfranco.popularmoviesstagetwo.api;

import co.paulfran.paulfranco.popularmoviesstagetwo.models.Movie.Movie;
import co.paulfran.paulfranco.popularmoviesstagetwo.models.Movies;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

interface MovieAPIService {

    // Top Rated Movies
    @GET("movie/top_rated")
    Call<Movies> getTopRatedMovies(@Query("api_key") String apiKey, @Query("page") int page);

    // Popular Movies
    @GET("movie/popular")
    Call<Movies> getPopularMovies(@Query("api_key") String apiKey, @Query("page") int page);

    // Movie Reviews
    @GET("movie/{movieId}?append_to_response=videos,reviews")
    Call<Movie> getMovie(@Path("movieId") int movieId, @Query("api_key") String apiKey);
}
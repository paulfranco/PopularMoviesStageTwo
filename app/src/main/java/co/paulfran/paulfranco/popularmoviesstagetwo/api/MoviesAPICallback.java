package co.paulfran.paulfranco.popularmoviesstagetwo.api;

public interface MoviesAPICallback<T> {
    void onResponse(T result);

    void onCancel();
}
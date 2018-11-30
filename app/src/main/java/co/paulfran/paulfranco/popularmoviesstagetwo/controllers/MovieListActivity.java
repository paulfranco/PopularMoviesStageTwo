package co.paulfran.paulfranco.popularmoviesstagetwo.controllers;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import co.paulfran.paulfranco.popularmoviesstagetwo.R;

import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.orhanobut.logger.Logger;

import co.paulfran.paulfranco.popularmoviesstagetwo.adapters.FavoriteMoviesAdapter;
import co.paulfran.paulfranco.popularmoviesstagetwo.adapters.MoviesAdapter;
import co.paulfran.paulfranco.popularmoviesstagetwo.api.MoviesAPICallback;
import co.paulfran.paulfranco.popularmoviesstagetwo.api.MoviesAPIManager;
import co.paulfran.paulfranco.popularmoviesstagetwo.data.MoviesContract;
import co.paulfran.paulfranco.popularmoviesstagetwo.models.Movies;
import co.paulfran.paulfranco.popularmoviesstagetwo.utils.ItemSpacingDecoration;
import co.paulfran.paulfranco.popularmoviesstagetwo.utils.Misc;
import co.paulfran.paulfranco.popularmoviesstagetwo.utils.RecyclerViewScrollListener;
import es.dmoral.toasty.Toasty;

public class MovieListActivity extends AppCompatActivity implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

    private RecyclerViewScrollListener mScrollListener;
    private SwipyRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private View mNoDataContainer;
    private TextView mNoDataContainerMsg;

    private MoviesAPIManager.SortBy sortBy = MoviesAPIManager.SortBy.MostPopular;

    public static final String BUNDLE_MOVIES_KEY = "movies";
    public static final String BUNDLE_RECYCLER_POSITION_KEY = "recycler_position";
    public static final int FAVORITES_MOVIE_LOADER_ID = 89;

    private Movies mMovies = new Movies();

    // mTwoPane is true if its on a tablet
    private boolean mTwoPane;

    // Receivers
    private final BroadcastReceiver networkChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // uf Movies not loaded
            if (mRecyclerView.getAdapter() == null) {
                // if there is internet connection
                if (isNetworkAvailable()) {
                    // load the Movies
                    loadMovies();
                } else {
                    // if no internet connection we can still load favorite movies
                    if (sortBy == MoviesAPIManager.SortBy.Favorite) {
                        // load favorite movies
                        loadMovies();
                    }
                    mNoDataContainerMsg.setText(R.string.no_internet);
                }
                toggleNoDataContainer();
            }

            // if scroll listener is not null data is not loading anymore
            if (mScrollListener != null) {
                mScrollListener.setLoading(false);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        loadSortSelected();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        if (findViewById(R.id.movieDetailContainer) != null) {
            // if this view is present, then the activity should be in two-pane mode.
            mTwoPane = true;
        }

        mNoDataContainer = findViewById(R.id.noDataContainer);
        mNoDataContainerMsg = findViewById(R.id.tvNoDataMsg);

        mRecyclerView = findViewById(R.id.rvMovieList);
        setupRecyclerView();

        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        setupSwipeRefreshLayout();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // if the scroll listener is not empty and the list of Movies is not empty
        if (mScrollListener != null && !mMovies.getResults().isEmpty()) {
            outState.putInt(BUNDLE_RECYCLER_POSITION_KEY, mScrollListener.getFirstCompletelyVisibleItemPosition());
            outState.putParcelable(BUNDLE_MOVIES_KEY, mMovies);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Movies tempMovie = savedInstanceState.getParcelable(BUNDLE_MOVIES_KEY);
        int position = savedInstanceState.getInt(BUNDLE_RECYCLER_POSITION_KEY);
        if (tempMovie != null) {
            mMovies = tempMovie;

            setRecyclerViewAdapter(new MoviesAdapter(this, mMovies, mTwoPane));
            toggleNoDataContainer();
            mRecyclerView.getLayoutManager().scrollToPosition(position);
        }
    }

    @Override
    protected void onResume() {
        Logger.i("onResume()");

        super.onResume();
        try {
            registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        } catch (Exception ex) {
            Logger.e(ex.getMessage());
        }
    }

    @Override
    protected void onPause() {
        Logger.i("onPause()");
        try {
            unregisterReceiver(networkChangeReceiver);
        } catch (Exception ex) {
            Logger.e(ex.getMessage());
        }

        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // instantiate the MenuInflater
        MenuInflater inflater = getMenuInflater();
        // inflate the menu
        inflater.inflate(R.menu.activity_movie_list_menu, menu);

        // if sort by is top rated
        if (sortBy == MoviesAPIManager.SortBy.TopRated)
            // find the item and check true
            menu.findItem(R.id.menu_sort_top_rated).setChecked(true);
        // if sort by is favorite
        else if (sortBy == MoviesAPIManager.SortBy.Favorite)
            // find the item and check true
            menu.findItem(R.id.menu_sort_favorite).setChecked(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            // if sort by most popular
            case R.id.menu_sort_popularity:
                if (sortBy != MoviesAPIManager.SortBy.MostPopular) {
                    if (isNetworkAvailable()) {
                        sortBy = MoviesAPIManager.SortBy.MostPopular;
                        loadMovies();
                        item.setChecked(true);
                        saveSortSelected();
                    } else {
                        Toasty.error(getApplicationContext(), getString(R.string.toast_no_internet), Toast.LENGTH_SHORT, true).show();
                    }
                }
                break;
            // if sort by top rated
            case R.id.menu_sort_top_rated:
                if (sortBy != MoviesAPIManager.SortBy.TopRated) {
                    if (isNetworkAvailable()) {
                        sortBy = MoviesAPIManager.SortBy.TopRated;
                        loadMovies();
                        item.setChecked(true);
                        saveSortSelected();
                    } else {
                        Toasty.error(getApplicationContext(), getString(R.string.toast_no_internet), Toast.LENGTH_SHORT, true).show();
                    }
                }
                break;
            // if sort by favorite
            case R.id.menu_sort_favorite:
                if (sortBy != MoviesAPIManager.SortBy.Favorite) {
                    sortBy = MoviesAPIManager.SortBy.Favorite;
                    loadMovies();
                    item.setChecked(true);
                    saveSortSelected();
                }
                break;
        }
        // set the time to the type of sorted by option chosen
        setTitleAccordingSort();
        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView() {
        int spanCount = getHandySpanCount();

        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), spanCount);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new ItemSpacingDecoration((int) getResources().getDimension(R.dimen.movie_list_items_margin), ItemSpacingDecoration.GRID));
        mScrollListener = new RecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int totalItemsCount, RecyclerView view) {
                // if there is internet connection
                if (isNetworkAvailable()) {
                    // load more movies because we want it to be an endless scroll
                    loadMoreMovies();
                }
            }
        };
        mRecyclerView.addOnScrollListener(mScrollListener);
    }

    private void setRecyclerViewAdapter(RecyclerView.Adapter adapter) {
        mRecyclerView.clearOnScrollListeners();

        if (adapter != null) {
            if (adapter instanceof MoviesAdapter) {
                mRecyclerView.addOnScrollListener(mScrollListener);
                mSwipeRefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTH);
            } else if (adapter instanceof FavoriteMoviesAdapter) {
                mSwipeRefreshLayout.setDirection(SwipyRefreshLayoutDirection.TOP);
            }
        }
        mRecyclerView.setAdapter(adapter);
        toggleNoDataContainer();
        mScrollListener.resetState();
        mSwipeRefreshLayout.setRefreshing(false);

        // if the adapter is empty
        if (adapter == null) {
            // find the movieDetailContainer
            ViewGroup view = findViewById(R.id.movieDetailContainer);
            // if the adapter is not empty
            if (view != null) {
                // clear it
                view.removeAllViews();
            }
        }
    }

    private void setupSwipeRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if (direction == SwipyRefreshLayoutDirection.TOP) {
                    loadMovies();
                } else {
                    loadMoreMovies();
                }
            }
        });
    }

    private void loadMovies() {
        // get the movies
        getMovies(1);
    }

    private void loadMoreMovies() {
        // load more movies by incrementing he page number
        getMovies(mMovies.getPage() + 1);
    }

    private void getMovies(final int page) {
        // if sort by favorite
        if (sortBy != MoviesAPIManager.SortBy.Favorite) {
            // if there is internet connection
            if (isNetworkAvailable()) {
                getSupportLoaderManager().destroyLoader(FAVORITES_MOVIE_LOADER_ID);

                MoviesAPIManager.getInstance().getMovies(sortBy, page, new MoviesAPICallback<Movies>() {
                    @Override
                    public void onResponse(Movies result) {
                        if (result != null) {
                            if (page == 1) {
                                mMovies = result;
                                setRecyclerViewAdapter(new MoviesAdapter(MovieListActivity.this, mMovies, mTwoPane));
                            } else {
                                if (mRecyclerView.getAdapter() instanceof MoviesAdapter) {
                                    ((MoviesAdapter) mRecyclerView.getAdapter()).updateMovies(result);
                                }
                            }
                        } else {
                            // display error message
                            mNoDataContainerMsg.setText(R.string.movie_detail_error_message);
                            Toast.makeText(getApplicationContext(), R.string.movie_detail_error_message, Toast.LENGTH_SHORT).show();
                            Toasty.error(getApplicationContext(), getString(R.string.movie_detail_error_message), Toast.LENGTH_SHORT, true).show();

                        }
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onCancel() {
                    }
                });
            } else {
                mSwipeRefreshLayout.setRefreshing(false);
                mNoDataContainerMsg.setText(R.string.no_internet);
                Toasty.error(getApplicationContext(), getString(R.string.toast_no_internet), Toast.LENGTH_SHORT, true).show();
            }
        } else {
            mMovies = new Movies();
            // set recycler adapter to null
            setRecyclerViewAdapter(null);
            getSupportLoaderManager().initLoader(FAVORITES_MOVIE_LOADER_ID, null,  this);
        }

    }

    private void toggleNoDataContainer() {
        if (mRecyclerView.getAdapter() != null && mRecyclerView.getAdapter().getItemCount() > 0) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mNoDataContainer.setVisibility(View.GONE);
        } else {
            mRecyclerView.setVisibility(View.GONE);
            mNoDataContainer.setVisibility(View.VISIBLE);
        }
    }

    private int getHandySpanCount() {
        // Get width regarding ratio 2:3
        double aa = (int) getResources().getDimension(R.dimen.movie_image_height) / 1.5;
        double ab = getResources().getDisplayMetrics().widthPixels;
        if (mTwoPane) {
            // If in Two Pane Mode Recycler View width is Guidelines Percentage
            TypedValue typedValue = new TypedValue();
            getResources().getValue(R.dimen.movies_list_detail_separator_percent, typedValue, true);
            float ac = typedValue.getFloat();
            ab = ac * ab;
        }
        double ac = (ab / aa);
        return (int) Math.round(ac);
    }

    private boolean isNetworkAvailable() {
        return Misc.isNetworkAvailable(getApplicationContext());
    }

    private void saveSortSelected() {
        getPreferences(Context.MODE_PRIVATE).edit().putInt(getString(R.string.saved_sort_by_key), sortBy.ordinal()).apply();
    }
    // set the title according to the sorted choice
    private void setTitleAccordingSort() {
        switch (sortBy) {
            case MostPopular:
                setTitle(getString(R.string.most_popular) + " " + getString(R.string.movies));
                break;
            case TopRated:
                setTitle(getString(R.string.top_rated) + " " + getString(R.string.movies));
                break;
            case Favorite:
                setTitle(getString(R.string.favorite) + " " + getString(R.string.movies));
                break;
        }
    }

    private void loadSortSelected() {
        sortBy = MoviesAPIManager.SortBy.fromId(getPreferences(Context.MODE_PRIVATE).getInt(getString(R.string.saved_sort_by_key), 0));
        setTitleAccordingSort();
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new android.support.v4.content.AsyncTaskLoader<Cursor>(this) {

            // initialize a Cursor,
            Cursor mTaskData = null;

            // onStartLoading() is called when a loader first starts loading data
            @Override
            protected void onStartLoading() {
                // if mTaskData not empty
                if (mTaskData != null) {
                    // deliver loaded data
                    deliverResult(mTaskData);
                } else {
                    // force a new data to be loaded
                    forceLoad();
                }
            }

            // loadInBackground() performs asynchronous loading of data
            @Override
            public Cursor loadInBackground() {

                try {
                    return getContentResolver().query(MoviesContract.MoviesEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            MoviesContract.MoviesEntry._ID);

                } catch (Exception e) {
                    Logger.e(getString(R.string.failed_to_load_data));
                    e.printStackTrace();
                    return null;
                }
            }

            // deliverResult sends cursor to the registered listener
            public void deliverResult(Cursor data) {
                mTaskData = data;
                super.deliverResult(data);
            }
        };

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.getCount() > 0) {
            setRecyclerViewAdapter(new FavoriteMoviesAdapter(this, data, mTwoPane));
        } else {
            mNoDataContainerMsg.setText(R.string.no_favorite_movies_message);
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}


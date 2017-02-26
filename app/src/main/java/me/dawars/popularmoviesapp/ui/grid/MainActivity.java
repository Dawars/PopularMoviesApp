package me.dawars.popularmoviesapp.ui.grid;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.dawars.popularmoviesapp.R;
import me.dawars.popularmoviesapp.data.Movie;
import me.dawars.popularmoviesapp.data.MovieContract;
import me.dawars.popularmoviesapp.ui.DetailActivity;
import me.dawars.popularmoviesapp.utils.NetworkUtils;

public class MainActivity extends AppCompatActivity
        implements MovieAdapter.ListItemClickListener, LoaderManager.LoaderCallbacks<List<Movie>> {

    public static final String TAG = MainActivity.class.getSimpleName();

    public static final String ARG_MOVIE = "MOVIE_KEY";

    private static final String MOVIE_PAGE_KEY = "PAGE_KEY";
    private static final String MOVIE_SORT_KEY = "SORT_KEY";

    private static final String MOVIES_STATUS_CODE = "status_code";
    public static final int LOADER_MOVIE_ID = 1;

    public static final int POPULARITY = 0;
    public static final int RATING = 1;
    public static final int FAVOURITE = 2;

    @IntDef({POPULARITY, RATING, FAVOURITE})
    public @interface SortCriteria {

    }

    @SortCriteria
    int sortBy = POPULARITY;

    @BindView(R.id.rv_movie_thumbnails)
    RecyclerView recyclerView;

    private GridLayoutManager layoutManager;
    private MovieAdapter movieAdapter;

    private EndlessRecyclerViewScrollListener scrollListener;

    @BindView(R.id.tv_error)
    TextView errorMessageDisplay;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.menu)
    FloatingActionMenu fabMenu;
    @BindView(R.id.fab_sort_popular)
    FloatingActionButton fabSortPopular;
    @BindView(R.id.fab_sort_rating)
    FloatingActionButton fabSortRating;
    @BindView(R.id.fab_sort_favourites)
    FloatingActionButton fabSortFavourite;

    @BindView(R.id.coordinator)
    CoordinatorLayout coordinator;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipreRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        // disable fab menu icon rotation
        fabMenu.setIconAnimated(false);

        layoutManager = new GridLayoutManager(this, 3);

        // change span count based on orientation
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager.setSpanCount(5);
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager.setSpanCount(3);
        }

        movieAdapter = new MovieAdapter(this, this);
        recyclerView.setAdapter(movieAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        swipreRefresh.setColorSchemeResources(R.color.loader1, R.color.loader2, R.color.loader3);
        swipreRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.v(TAG, "refreshing");
                loadMovieData(1, sortBy);
            }
        });


        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i(TAG, "Loading page " + page);

                if (sortBy != FAVOURITE) {
                    loadMovieData(page, sortBy);
                }
            }
        };
        recyclerView.setOnScrollListener(scrollListener);

        Log.v(TAG, "Init loader");
        LoaderManager loaderManager = getSupportLoaderManager();
        Bundle bundle = new Bundle();
        bundle.putInt(MOVIE_PAGE_KEY, 1);
        bundle.putInt(MOVIE_SORT_KEY, sortBy);

        loaderManager.initLoader(LOADER_MOVIE_ID, bundle, this);

        setFabColor(fabSortPopular, R.color.colorAccentDark, R.color.colorAccent);

    }

    private void snackbar(@StringRes int resId) {
        Snackbar.make(coordinator, resId, Snackbar.LENGTH_SHORT).show();
    }

    private void loadMovieData(int page, @SortCriteria int sortBy) {

        if (NetworkUtils.isNetworkAvailable(this)) {
            if (page == 1) { // if (re)loading 1st page replace data, otherwise add new pages
                invalidateData();
                scrollListener.resetState();
            }

            LoaderManager loaderManager = getSupportLoaderManager();

            Bundle bundle = new Bundle();
            bundle.putInt(MOVIE_PAGE_KEY, page);
            bundle.putInt(MOVIE_SORT_KEY, sortBy);

            loaderManager.restartLoader(LOADER_MOVIE_ID, bundle, this);
        } else {
            swipreRefresh.setRefreshing(false);
            snackbar(R.string.no_network);
        }
    }

    private void invalidateData() {
        movieAdapter.clearMovies();
    }

    @Override
    public void onItemClick(View v, int position) {
        ImageView posterIv = (ImageView) v.findViewById(R.id.im_poster);
        TextView titleTv = (TextView) v.findViewById(R.id.tv_title);

        Movie movie = movieAdapter.getMovie(position);

        Intent intent = DetailActivity.prepareIntent(this, movie);

        Pair<View, String> p1 = Pair.create((View) posterIv, getString(R.string.transition_poster));
        Pair<View, String> p2 = Pair.create((View) titleTv, getString(R.string.transition_title));
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, p1, p2);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // wait for async to load
//            supportPostponeEnterTransition(); //FIXME shared element transition postpone - going back from details freezes
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }

    /**
     * Sets the FAB to the given colors
     *
     * @param fabView     fab view from the FAB lib by clans
     * @param bgColor     background color
     * @param rippleColor ripple color
     */
    private void setFabColor(@NonNull View fabView, @ColorRes int bgColor, @ColorRes int rippleColor) {
        if (fabView instanceof FloatingActionButton) {
            FloatingActionButton fab = (FloatingActionButton) fabView;

            fab.setColorNormalResId(bgColor);
            fab.setColorPressedResId(rippleColor);
            fab.setColorRippleResId(rippleColor);

        }
    }

    @OnClick({R.id.fab_sort_rating, R.id.fab_sort_popular, R.id.fab_sort_favourites})
    public void onSortCritChange(View view) {
        fabMenu.close(true);
        switch (view.getId()) {
            case R.id.fab_sort_popular:
                sortBy = POPULARITY;

                setFabColor(fabSortPopular, R.color.colorAccentDark, R.color.colorAccent);
                setFabColor(fabSortRating, R.color.colorPrimary, R.color.colorPrimaryDark);
                setFabColor(fabSortFavourite, R.color.colorPrimary, R.color.colorPrimaryDark);

                break;
            case R.id.fab_sort_rating:
                sortBy = RATING;

                setFabColor(fabSortRating, R.color.colorAccentDark, R.color.colorAccent);
                setFabColor(fabSortPopular, R.color.colorPrimary, R.color.colorPrimaryDark);
                setFabColor(fabSortFavourite, R.color.colorPrimary, R.color.colorPrimaryDark);
                break;
            case R.id.fab_sort_favourites:

                sortBy = FAVOURITE;
                setFabColor(fabSortRating, R.color.colorPrimary, R.color.colorPrimaryDark);
                setFabColor(fabSortPopular, R.color.colorPrimary, R.color.colorPrimaryDark);
                setFabColor(fabSortFavourite, R.color.colorAccentDark, R.color.colorAccent);
        }
        loadMovieData(1, sortBy);
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<List<Movie>>(this) {
            public List<Movie> movieData;

            int page;
            int sortCrit;

            @Override
            protected void onStartLoading() {
                Log.i(TAG, "Loader started loading");

                page = args.getInt(MOVIE_PAGE_KEY);
                sortCrit = args.getInt(MOVIE_SORT_KEY);

                swipreRefresh.setRefreshing(true);

                if (movieData != null) {
                    Log.i(TAG, "Data already present");
                    deliverResult(movieData);
                } else {
                    forceLoad();
                }
            }

            @Override
            public List<Movie> loadInBackground() {

                if (sortCrit == FAVOURITE) {
                    Cursor cursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            null
                    );
                    if (cursor == null) return null;

                    List<Movie> data = new ArrayList<>();
                    cursor.moveToFirst();
                    try {
                        while (cursor.moveToNext()) {
                            data.add(new Movie(cursor));
                        }
                    } finally {
                        cursor.close();
                    }
                    return data;
                }

                if (!NetworkUtils.isNetworkAvailable(MainActivity.this)) {
                    swipreRefresh.setRefreshing(false);
                    snackbar(R.string.no_network);
                }
                URL url = NetworkUtils.buildMovieUrl(page, sortCrit);

                String jsonResponse = null;

                try {
                    jsonResponse = NetworkUtils.getResponseFromHttpUrl(url);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;// timeout
                }

                try {
                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    if (jsonObject.has(MOVIES_STATUS_CODE)) {
                        int errorCode = jsonObject.getInt(MOVIES_STATUS_CODE);
                        switch (errorCode) {
                            case 34: // The resource you requested could not be found.
                                snackbar(R.string.error_resource_not_found);
                                return null;
                            case 7: // Invalid API key: You must be granted a valid key.
                                snackbar(R.string.error_invalid_api_key);
                                return null;
                            default:
                                // Server probably down
                                return null;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }

                Movie.Result response = new Gson().fromJson(jsonResponse, Movie.Result.class);

                if (response == null) {
                    return null;
                }
                Log.i(TAG, "Loading in background");

                return response.movies;
            }

            @Override
            public void deliverResult(List<Movie> data) {
                Log.i(TAG, "Loader deliver result");

                movieData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movieData) {
        movieAdapter.addMovieData(movieData);
        swipreRefresh.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
    }
}

package me.dawars.popularmoviesapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.dawars.popularmoviesapp.data.Movie;
import me.dawars.popularmoviesapp.utils.NetworkUtils;

public class MainActivity extends AppCompatActivity
        implements MovieAdapter.ListItemClickListener, LoaderManager.LoaderCallbacks<List<Movie>> {

    public static final String TAG = MainActivity.class.getSimpleName();

    public static final String EXTRA_MOVIE = "EXTRA_MOVIE_KEY";
    private static final String MOVIES_STATUS_CODE = "status_code";
    public static final int LOADER_MOVIE_ID = 2;


    String sortBy = NetworkUtils.SORT_POPULAR;

    @BindView(R.id.rv_movie_thumbnails)
    RecyclerView recyclerView;

    private GridLayoutManager layoutManager;
    private MovieAdapter movieAdapter;

//    @BindView(R.id.pb_loader)
//    ProgressBar loadingIndicator;

    @BindView(R.id.tv_error)
    TextView errorMessageDisplay;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.fab_sort)
    FloatingActionButton fabSort;
    @BindView(R.id.fab_sort_popular)
    FloatingActionButton fabSortPopular;
    @BindView(R.id.fab_sort_rating)
    FloatingActionButton fabSortRating;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipreRefresh;

    private boolean isFABOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        layoutManager = new GridLayoutManager(this, 3);

        movieAdapter = new MovieAdapter(this);
        recyclerView.setAdapter(movieAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        swipreRefresh.setColorSchemeResources(R.color.pink, R.color.indigo, R.color.lime);
        swipreRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.v(TAG, "refreshing");
                loadMovieData(sortBy);
            }
        });

        swipreRefresh.setRefreshing(true);
        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.initLoader(LOADER_MOVIE_ID, null, this);

        // FIXME code duplication with loadMovieData()
        if (!NetworkUtils.isNetworkAvailable(this)) {
            swipreRefresh.setRefreshing(false);
            snackbar(R.string.no_network);
        }
    }

    private void snackbar(@StringRes int resId) {
        Snackbar.make(fabSort, resId, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Log.v(TAG, "onConfigurationChanged");

        // change span count based on orientation
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager.setSpanCount(5);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager.setSpanCount(3);
        }
    }

    private void loadMovieData(String sortBy) {
        if (NetworkUtils.isNetworkAvailable(this)) {
            invalidateData();
            showMovieDataView();
            LoaderManager loaderManager = getSupportLoaderManager();
            loaderManager.restartLoader(LOADER_MOVIE_ID, null, this);
        } else {
            swipreRefresh.setRefreshing(false);
            snackbar(R.string.no_network);
        }
    }

    private void invalidateData() {
        movieAdapter.setMovieData(null);
    }

    void showMovieDataView() {
        errorMessageDisplay.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    void showErrorView() {
        errorMessageDisplay.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onItemClick(int position) {
        Movie movie = movieAdapter.getMovie(position);

        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(EXTRA_MOVIE, movie);

        startActivity(intent);
    }

    @OnClick(R.id.fab_sort)
    public void onFabClick(View view) {
        if (!isFABOpen) {
            showFABMenu();
            // TODO change icon to close
            fabSort.setImageResource(R.drawable.close);
        } else {
            closeFABMenu();
            fabSort.setImageResource(R.drawable.sort);
        }
    }

    private void showFABMenu() {
        isFABOpen = true;
        fabSortRating.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        fabSortPopular.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
    }

    private void closeFABMenu() {
        isFABOpen = false;
        fabSortRating.animate().translationY(0);
        fabSortPopular.animate().translationY(0);
    }

    @OnClick({R.id.fab_sort_rating, R.id.fab_sort_popular})
    public void onSortCritChange(View view) {
        switch (view.getId()) {
            case R.id.fab_sort_popular:
                sortBy = NetworkUtils.SORT_POPULAR;
                break;
            case R.id.fab_sort_rating:
                sortBy = NetworkUtils.SORT_RATING;
                break;
        }
        loadMovieData(sortBy);
    }

    @Override
    public void onBackPressed() {
        if (!isFABOpen) {
            super.onBackPressed();
        } else {
            closeFABMenu();
        }
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<List<Movie>>(this) {
            public List<Movie> movieData;

            @Override
            protected void onStartLoading() {
                if (movieData != null) {
                    deliverResult(movieData);
                } else {
//                    loadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Override
            public List<Movie> loadInBackground() {
                URL url = NetworkUtils.buildUrl(sortBy); // FIXME add parameter in bundle

                String jsonResponse = null;

                try {
                    jsonResponse = NetworkUtils.getResponseFromHttpUrl(url);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
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

                Movie.Response response = new Gson().fromJson(jsonResponse, Movie.Response.class);

                if (response == null) {
                    return null;
                }
                return response.movies;
            }

            @Override
            public void deliverResult(List<Movie> data) {
                movieData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movieData) {
        movieAdapter.setMovieData(movieData);
        swipreRefresh.setRefreshing(false);

        if (movieData != null && movieData.size() > 0) {
            showMovieDataView();
        } else {
            showErrorView();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
    }
}

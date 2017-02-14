package me.dawars.popularmoviesapp.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.graphics.drawable.DrawableCompat;
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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.dawars.popularmoviesapp.R;
import me.dawars.popularmoviesapp.adapter.MovieAdapter;
import me.dawars.popularmoviesapp.data.Movie;
import me.dawars.popularmoviesapp.utils.NetworkUtils;

public class MainActivity extends AppCompatActivity
        implements MovieAdapter.ListItemClickListener, LoaderManager.LoaderCallbacks<List<Movie>> {

    public static final String TAG = MainActivity.class.getSimpleName();

    public static final String ARG_MOVIE = "MOVIE_KEY";
    private static final String MOVIES_STATUS_CODE = "status_code";
    public static final int LOADER_MOVIE_ID = 1;

    String sortBy = NetworkUtils.SORT_POPULAR;

    @BindView(R.id.rv_movie_thumbnails)
    RecyclerView recyclerView;

    private GridLayoutManager layoutManager;
    private MovieAdapter movieAdapter;

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
        Snackbar.make(coordinator, resId, Snackbar.LENGTH_SHORT).show();
    }

    // FIXME orientation change column size
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
        swipreRefresh.setRefreshing(true);
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
        movieAdapter.setData(null);
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
//            supportPostponeEnterTransition(); FIXME shared element transition
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }

    @OnClick({R.id.fab_sort_rating, R.id.fab_sort_popular})
    public void onSortCritChange(View view) {
        fabMenu.close(true);
        final Drawable wrappedFabPopular = DrawableCompat.wrap(fabSortPopular.getDrawable());
        final Drawable wrappedFabRating = DrawableCompat.wrap(fabSortRating.getDrawable());
        switch (view.getId()) {
            case R.id.fab_sort_popular:
                sortBy = NetworkUtils.SORT_POPULAR;
/*
//FIXME selected fab color
                DrawableCompat.setTint(wrappedFabPopular, getResources().getColor(R.color.colorAccent));
                DrawableCompat.setTint(wrappedFabRating, getResources().getColor(R.color.colorWhite));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    fabSortPopular.setBackground(wrappedFabPopular);
                    fabSortRating.setBackground(wrappedFabRating);
                } else {
                    fabSortPopular.setBackgroundDrawable(wrappedFabPopular);
                    fabSortRating.setBackgroundDrawable(wrappedFabRating);
                }

                fabSortPopular.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));
                fabSortRating.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
*/

                break;
            case R.id.fab_sort_rating:
                sortBy = NetworkUtils.SORT_RATING;

                /*DrawableCompat.setTint(wrappedFabPopular, getResources().getColor(R.color.colorWhite));
                DrawableCompat.setTint(wrappedFabRating, getResources().getColor(R.color.colorAccent));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    fabSortPopular.setBackground(wrappedFabPopular);
                    fabSortRating.setBackground(wrappedFabRating);
                } else {
                    fabSortPopular.setBackgroundDrawable(wrappedFabPopular);
                    fabSortRating.setBackgroundDrawable(wrappedFabRating);
                }
                fabSortRating.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));
                fabSortPopular.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                */
                break;
        }
        loadMovieData(sortBy);
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
                URL url = NetworkUtils.buildMovieUrl(sortBy); // FIXME add parameter in bundle

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
        movieAdapter.setData(movieData);
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

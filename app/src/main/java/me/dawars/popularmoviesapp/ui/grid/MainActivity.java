package me.dawars.popularmoviesapp.ui.grid;

import android.content.Intent;
import android.content.res.Configuration;
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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.dawars.popularmoviesapp.R;
import me.dawars.popularmoviesapp.data.Movie;
import me.dawars.popularmoviesapp.ui.DetailActivity;
import me.dawars.popularmoviesapp.utils.NetworkUtils;

import static me.dawars.popularmoviesapp.ui.grid.MovieLoader.MOVIE_PAGE_KEY;
import static me.dawars.popularmoviesapp.ui.grid.MovieLoader.MOVIE_SORT_KEY;

public class MainActivity extends AppCompatActivity
        implements MovieAdapter.ListItemClickListener, MovieLoaderCallback {

    public static final String TAG = MainActivity.class.getSimpleName();

    public static final String ARG_MOVIE = "MOVIE_KEY";

    public static final int LOADER_MOVIE_ID = 1;
    public static final int LOADER_FAVOURITE_ID = 2;

    private MovieLoader movieLoader = new MovieLoader(this, this);
    private FavouriteLoader favouriteLoader = new FavouriteLoader(this, this);

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

    // TODO use data binding
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
    SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        initFab(savedInstanceState);

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

        swipeRefresh.setColorSchemeResources(R.color.loader1, R.color.loader2, R.color.loader3);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.v(TAG, "refreshing");
                // reset data
                invalidateData();

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
        invalidateData();
        swipeRefresh.setRefreshing(true);
        LoaderManager loaderManager = getSupportLoaderManager();
        if (sortBy == FAVOURITE) {
            loaderManager.initLoader(LOADER_FAVOURITE_ID, null, favouriteLoader);
        } else {
            Bundle bundle = new Bundle();
            bundle.putInt(MOVIE_PAGE_KEY, 1);
            bundle.putInt(MOVIE_SORT_KEY, sortBy);
            loaderManager.initLoader(LOADER_MOVIE_ID, bundle, movieLoader);
        }
    }

    private void initFab(Bundle savedInstanceState) {
        // disable fab menu icon rotation
        fabMenu.setIconAnimated(false);

        // restore Sorting criteria and fab menu
        View fab = fabSortPopular;
        if (savedInstanceState != null) {
            // noinspection ResourceType
            sortBy = savedInstanceState.getInt(MOVIE_SORT_KEY);
            switch (sortBy) {
                case POPULARITY:
                    fab = fabSortPopular;
                    break;
                case RATING:
                    fab = fabSortRating;
                    break;
                case FAVOURITE:
                    fab = fabSortFavourite;
                    break;
            }
        }
        setFabColor(fab, R.color.colorAccentDark, R.color.colorAccent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(MOVIE_SORT_KEY, sortBy);
    }


    private void snackbar(@StringRes int resId) {
        Snackbar.make(coordinator, resId, Snackbar.LENGTH_SHORT).show();
        // FIXME move fab menu when opening snackbar
    }

    /**
     * Loads movie data at page
     *
     * @param page
     * @param sortBy
     */
    private void loadMovieData(int page, @SortCriteria int sortBy) {
        if (!NetworkUtils.isNetworkAvailable(MainActivity.this)) {
            swipeRefresh.setRefreshing(false);
            snackbar(R.string.no_network);
        }

        swipeRefresh.setRefreshing(true);

        LoaderManager loaderManager = getSupportLoaderManager();
        if (sortBy == FAVOURITE) {
            // using initLoader because the arguments are the same, needs reload
            loaderManager.initLoader(LOADER_FAVOURITE_ID, null, favouriteLoader); // will it run?
        } else {
            Bundle bundle = new Bundle();
            bundle.putInt(MOVIE_PAGE_KEY, page);
            bundle.putInt(MOVIE_SORT_KEY, sortBy);
            // the arguments change
            loaderManager.restartLoader(LOADER_MOVIE_ID, bundle, movieLoader);
        }
    }

    private void invalidateData() {
        movieAdapter.clearMovies();
        scrollListener.resetState();
    }

    @Override
    public void onItemClick(View v, int position) {
        ImageView posterIv = (ImageView) v.findViewById(R.id.im_poster);
        TextView titleTv = (TextView) v.findViewById(R.id.tv_title);

        Movie movie = movieAdapter.getMovie(position);

        Intent intent = DetailActivity.prepareIntent(this, movie);

        Pair<View, String> p1 = Pair.create((View) posterIv, getString(R.string.transition_poster));
        Pair<View, String> p2 = Pair.create((View) titleTv, getString(R.string.transition_title));// TODO: change to bg card
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
                swipeRefresh.setEnabled(true);

                setFabColor(fabSortPopular, R.color.colorAccentDark, R.color.colorAccent);
                setFabColor(fabSortRating, R.color.colorPrimary, R.color.colorPrimaryDark);
                setFabColor(fabSortFavourite, R.color.colorPrimary, R.color.colorPrimaryDark);

                break;
            case R.id.fab_sort_rating:
                sortBy = RATING;
                swipeRefresh.setEnabled(true);

                setFabColor(fabSortRating, R.color.colorAccentDark, R.color.colorAccent);
                setFabColor(fabSortPopular, R.color.colorPrimary, R.color.colorPrimaryDark);
                setFabColor(fabSortFavourite, R.color.colorPrimary, R.color.colorPrimaryDark);
                break;
            case R.id.fab_sort_favourites:
                sortBy = FAVOURITE;
                swipeRefresh.setEnabled(false); // disable for cursor loader, updates automatically

                setFabColor(fabSortRating, R.color.colorPrimary, R.color.colorPrimaryDark);
                setFabColor(fabSortPopular, R.color.colorPrimary, R.color.colorPrimaryDark);
                setFabColor(fabSortFavourite, R.color.colorAccentDark, R.color.colorAccent);
        }
        invalidateData();
        loadMovieData(1, sortBy);
    }


    @Override
    public void onComplete(List<Movie> movieData) {
        movieAdapter.addMovieData(movieData);
        swipeRefresh.setRefreshing(false);
    }
}

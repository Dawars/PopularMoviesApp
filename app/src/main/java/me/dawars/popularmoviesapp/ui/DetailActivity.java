package me.dawars.popularmoviesapp.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.dawars.popularmoviesapp.R;
import me.dawars.popularmoviesapp.data.Movie;
import me.dawars.popularmoviesapp.utils.DisplayUtils;
import me.dawars.popularmoviesapp.utils.FavouritesUtils;
import me.dawars.popularmoviesapp.utils.NetworkUtils;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();

    @BindView(R.id.iv_backdrop)
    ImageView backdropImage;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.fab_favourite)
    FloatingActionButton fab;

    @BindView(R.id.coordinator)
    CoordinatorLayout coordinatorLayout;

    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        favouritesUtils = new FavouritesUtils(getApplicationContext());

        movie = getIntent().getParcelableExtra(MainActivity.ARG_MOVIE);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, DetailFragment.newInstance(movie))
                    .commit();
        }
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        if (intent != null && intent.hasExtra(MainActivity.ARG_MOVIE)) {
            Movie movie = intent.getExtras().getParcelable(MainActivity.ARG_MOVIE);

            int width = DisplayUtils.getScreenMetrics(this).widthPixels;
            Uri backdropUri = NetworkUtils.getImageUri(movie.getBackdropPath(), width);
            Log.v(TAG, "backdrop " + backdropUri.toString());
            Glide.with(this).load(backdropUri).diskCacheStrategy(DiskCacheStrategy.ALL).into(backdropImage);
        }

        updateFab();
    }

    private FavouritesUtils favouritesUtils;

    @OnClick(R.id.fab_favourite)
    void onFabClicked() {
        if (favouritesUtils.isFavorite(movie)) {
            favouritesUtils.removeFromFavorites(movie);
            showSnackbar(R.string.message_removed_from_favorites);
        } else {
            favouritesUtils.addToFavorites(movie);
            showSnackbar(R.string.message_added_to_favorites);
        }
        updateFab();
    }


    private void updateFab() {
        if (favouritesUtils.isFavorite(movie)) {
            fab.setImageResource(R.drawable.heart);
        } else {
            fab.setImageResource(R.drawable.heart_outline);
        }
    }

    private void showSnackbar(@StringRes int resId) {
        Snackbar.make(coordinatorLayout, resId, Snackbar.LENGTH_SHORT).show();
    }

    public static Intent prepareIntent(Context context, Movie movie) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(MainActivity.ARG_MOVIE, movie);
        return intent;
    }
}

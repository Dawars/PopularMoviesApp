package me.dawars.popularmoviesapp.ui.detail;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.dawars.popularmoviesapp.R;
import me.dawars.popularmoviesapp.data.Movie;
import me.dawars.popularmoviesapp.ui.grid.MainActivity;
import me.dawars.popularmoviesapp.utils.DisplayUtils;
import me.dawars.popularmoviesapp.utils.FavouritesUtils;
import me.dawars.popularmoviesapp.utils.NetworkUtils;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();

    @BindView(R.id.iv_backdrop)
    ImageView backdropImage;

    @BindView(R.id.fab_favourite)
    FloatingActionButton fab;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.coordinator)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;

    private Movie movie;

    private FavouritesUtils favouritesUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        favouritesUtils = new FavouritesUtils(this);

        Intent intent = getIntent();

        if (intent != null && intent.hasExtra(MainActivity.ARG_MOVIE)) {
            movie = intent.getExtras().getParcelable(MainActivity.ARG_MOVIE);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, DetailFragment.newInstance(movie))
                    .commit();
        }

        initToolbar(toolbar);

        updateFab();
    }

    private void initToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
        collapsingToolbar.setTitle(movie.getTitle());
        collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.transparent));
        setTitle("");
        getSupportActionBar().setTitle(movie.getTitle());

        int width = DisplayUtils.getScreenMetrics(this).widthPixels;
        Uri backdropUri = NetworkUtils.getImageUri(movie.getBackdropPath(), width);
        Glide.with(this).load(backdropUri).diskCacheStrategy(DiskCacheStrategy.ALL).into(backdropImage);

    }

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

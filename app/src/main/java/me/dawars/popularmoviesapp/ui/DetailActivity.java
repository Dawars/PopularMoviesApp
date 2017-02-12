package me.dawars.popularmoviesapp.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.dawars.popularmoviesapp.R;
import me.dawars.popularmoviesapp.data.Movie;
import me.dawars.popularmoviesapp.utils.DisplayUtils;
import me.dawars.popularmoviesapp.utils.NetworkUtils;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();

    @BindView(R.id.iv_backdrop)
    ImageView backdropImage;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        movie = getIntent().getParcelableExtra(MainActivity.ARG_MOVIE);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.movies_grid_container, DetailFragment.newInstance(movie))
                .commit();

        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        if (intent != null && intent.hasExtra(MainActivity.ARG_MOVIE)) {
            Movie movie = intent.getExtras().getParcelable(MainActivity.ARG_MOVIE);

            int width = DisplayUtils.getScreenMetrics(this).widthPixels;
            Uri backdropUri = NetworkUtils.getImageUri(movie.getBackdropPath(), width);
            Log.v(TAG, "backdrop " + backdropUri.toString());
            Glide.with(this).load(backdropUri).into(backdropImage);
            // TODO add caching
/*
            backdropImage.getViewTreeObserver().addOnPreDrawListener(
                    new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            backdropImage.getViewTreeObserver().removeOnPreDrawListener(this);
                            supportStartPostponedEnterTransition();
                            return true;
                        }
                    }
            );*/
        }
    }

    private void snackbar(@StringRes int resId) {
        Snackbar.make(toolbar, resId, Snackbar.LENGTH_SHORT).show(); // FIXME root view
    }

    public static Intent prepareIntent(Context context, Movie movie) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(MainActivity.ARG_MOVIE, movie);
        return intent;
    }
}

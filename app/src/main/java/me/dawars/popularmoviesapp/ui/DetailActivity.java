package me.dawars.popularmoviesapp.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.dawars.popularmoviesapp.R;
import me.dawars.popularmoviesapp.data.Movie;
import me.dawars.popularmoviesapp.utils.NetworkUtils;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();

    @BindView(R.id.tv_backdrop)
    ImageView backdropImage;
    @BindView(R.id.tv_title)
    TextView titleTextView;
    @BindView(R.id.im_poster)
    ImageView posterImageView;
    @BindView(R.id.tv_overview)
    TextView overviewTextView;
    @BindView(R.id.rb_rating)
    RatingBar ratingTextView;
    @BindView(R.id.tv_release_date)
    TextView releaseDateTextView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        if (intent != null && intent.hasExtra(MainActivity.EXTRA_MOVIE)) {
            Movie movie = intent.getExtras().getParcelable(MainActivity.EXTRA_MOVIE);

            bindData(movie);
        }
    }

    private void bindData(Movie movie) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;

        Uri backdropUri = NetworkUtils.getImageUri(movie.getBackdropPath(), width);
        Glide.with(this).load(backdropUri).into(backdropImage);

        Uri posterUri = NetworkUtils.getImageUri(movie.getPosterPath(), width / 2);
        Glide.with(this).load(posterUri).into(posterImageView);

        // wait for async to load
        supportPostponeEnterTransition();
        posterImageView.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        posterImageView.getViewTreeObserver().removeOnPreDrawListener(this);
                        supportStartPostponedEnterTransition();
                        return true;
                    }
                }
        );

        titleTextView.setText(movie.getOrigTitle());
        overviewTextView.setText(movie.getOverview());
        ratingTextView.setRating(movie.getVoteAvg());
        releaseDateTextView.setText(movie.getReleaseDate());
    }
}

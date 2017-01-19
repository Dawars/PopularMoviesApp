package me.dawars.popularmoviesapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
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
    @BindView(R.id.tv_rating)
    TextView ratingTextView;
    @BindView(R.id.tv_release_date)
    TextView releaseDateTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        // check intent

        if (intent != null && intent.hasExtra(MainActivity.EXTRA_MOVIE)) {
            Movie movie = intent.getExtras().getParcelable(MainActivity.EXTRA_MOVIE);
            Log.v(TAG, "Movie: " + movie.getTitle());
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

            bindData(movie);
        }
    }

    private void bindData(Movie movie) {
        Uri backdropUri = NetworkUtils.getImageUri(movie.getBackdropPath(), backdropImage.getWidth());
        Glide.with(this).load(backdropUri).into(backdropImage);

        titleTextView.setText(movie.getOrigTitle());

        Uri posterUri = NetworkUtils.getImageUri(movie.getPosterPath(), posterImageView.getWidth());
        Glide.with(this).load(posterUri).into(posterImageView);

        overviewTextView.setText(movie.getOverview());
        ratingTextView.setText(String.valueOf(movie.getVoteAvg())); // TODO add stars
        releaseDateTextView.setText(movie.getReleaseDate());
    }
}

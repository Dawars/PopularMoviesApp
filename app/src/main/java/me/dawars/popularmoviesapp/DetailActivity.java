package me.dawars.popularmoviesapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;

import me.dawars.popularmoviesapp.data.Movie;
import me.dawars.popularmoviesapp.utils.NetworkUtils;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

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

    }
}

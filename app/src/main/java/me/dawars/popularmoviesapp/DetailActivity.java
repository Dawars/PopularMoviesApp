package me.dawars.popularmoviesapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.net.URL;

import me.dawars.popularmoviesapp.utils.MovieJsonUtils;
import me.dawars.popularmoviesapp.utils.NetworkUtils;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        // check intent

        int id = intent.getIntExtra(Intent.EXTRA_TEXT, -1);
        Log.v("DetaulsActivity", "Movie id: " + id);
    }

    class DetailLoader extends AsyncTask<String, Void, MovieRecord[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            loadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected MovieRecord[] doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            URL url = NetworkUtils.buildUrl(params[0]);
            try {
                // TODO check for internet connectivity
                String jsonResponse = NetworkUtils.getResponseFromHttpUrl(url);
                MovieRecord[] movieRecords = MovieJsonUtils
                        .getMovieObjectFromJson(jsonResponse);

                return movieRecords;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(MovieRecord[] movieData) {
//            loadingIndicator.setVisibility(View.INVISIBLE);

            if (movieData != null && movieData.length > 0) {
//                showMovieDataView();
//                movieAdapter.setMovieData(movieData);
            } else {
//                showErrorView();
            }
        }
    }
}

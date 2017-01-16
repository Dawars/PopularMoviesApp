package me.dawars.popularmoviesapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;

import me.dawars.popularmoviesapp.utils.MovieJsonUtils;
import me.dawars.popularmoviesapp.utils.NetworkUtils;

public class MainActivity extends AppCompatActivity {

    private static final String SORT_POPULAR = "popular";

    private RecyclerView recyclerView;

    private MovieAdapter movieAdapter;

    private ProgressBar loadingIndicator;

    private TextView errorMessageDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.rv_movie_thumbnails);
        loadingIndicator = (ProgressBar) findViewById(R.id.pb_loader);
        errorMessageDisplay = (TextView) findViewById(R.id.tv_error);

        // TODO landscape grid count
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);

        movieAdapter = new MovieAdapter();
        recyclerView.setAdapter(movieAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        loadMovieData();
    }

    private void loadMovieData() {
        showMovieDataView();
        // TODO: add option to change sorting criteria
        new MovieLoader().execute(SORT_POPULAR);
    }

    void showMovieDataView() {
        errorMessageDisplay.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    void showErrorView() {
        errorMessageDisplay.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
    }

    class MovieLoader extends AsyncTask<String, Void, MovieRecord[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingIndicator.setVisibility(View.VISIBLE);
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
            loadingIndicator.setVisibility(View.INVISIBLE);

            if (movieData != null && movieData.length > 0) {
                showMovieDataView();
                movieAdapter.setMovieData(movieData);
            } else {
                showErrorView();
            }
        }
    }
}

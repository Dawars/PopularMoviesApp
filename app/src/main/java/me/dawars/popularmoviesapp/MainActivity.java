package me.dawars.popularmoviesapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;

import me.dawars.popularmoviesapp.utils.MovieJsonUtils;
import me.dawars.popularmoviesapp.utils.NetworkUtils;

public class MainActivity extends AppCompatActivity {

    private static final String SORT_POPULAR = "popular";

    private RecyclerView mRecyclerView;

    private MovieAdapter mAdapter;

    private ProgressBar mLoadingIndicator;

    private TextView mErrorMessageDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movie_thumbnails);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loader);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error);

        // TODO change to GridLayoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mAdapter = new MovieAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);

        loadMovieData();
    }

    private void loadMovieData() {
        showMovieDataView();
        // TODO: add option to change sorting criteria
        new MovieLoader().execute(SORT_POPULAR);
    }

    void showMovieDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    void showErrorView() {
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    class MovieLoader extends AsyncTask<String, Void, MovieRecord[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
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
            mLoadingIndicator.setVisibility(View.INVISIBLE);

            if (movieData != null && movieData.length > 0) {
                showMovieDataView();
                mAdapter.setMovieData(movieData);
            } else {
                showErrorView();
            }
        }
    }
}

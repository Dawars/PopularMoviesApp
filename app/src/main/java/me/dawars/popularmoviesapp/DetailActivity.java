package me.dawars.popularmoviesapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

        if (intent.hasExtra(Intent.EXTRA_TEXT)) {
            String id = intent.getStringExtra(Intent.EXTRA_TEXT);
            Log.v(TAG, "Movie id: " + id);
            loadMovie(id);
        }
    }

    private void loadMovie(String id) {
        new DetailLoader().execute(id);
    }

    class DetailLoader extends AsyncTask<String, Void, Movie[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            loadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected Movie[] doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            URL url = NetworkUtils.buildUrl(params[0]);
            try {
                // TODO check for internet connectivity
                String jsonResponse = NetworkUtils.getResponseFromHttpUrl(url);

                final String MOVIES_TITLE = "original_title";
                final String MOVIES_POSTER = "poster_path";
                final String MOVIES_BACKDROP = "backdrop_path";
                final String MOVIES_RATING = "vote_average";
                final String MOVIES_SUMMARY = "overview";
                final String MOVIES_RELEASE_DATE = "release_date";
                final String MOVIES_GENRES = "genres";

                final String MOVIES_STATUS_CODE = "status_code";

                final String MOVIES_STATUS_MESSAGE = "status_message";

                JSONObject moviesJson = new JSONObject(jsonResponse);


                if (moviesJson.has(MOVIES_STATUS_CODE)) {
                    int errorCode = moviesJson.getInt(MOVIES_STATUS_CODE);
                    switch (errorCode) {
                        case 34: /* The resource you requested could not be found. */
                            return null;
                        case 7: /* Invalid API key: You must be granted a valid key. */
                            return null;
                        default:
                    /* Server probably down */
                            return null;
                    }
                }

                String title = moviesJson.getString(MOVIES_TITLE);
                String backdropUrl = moviesJson.getString(MOVIES_BACKDROP);
                String posterUrl = moviesJson.getString(MOVIES_POSTER);
                String summary = moviesJson.getString(MOVIES_SUMMARY);
                String releaseDate = moviesJson.getString(MOVIES_RELEASE_DATE);
                String rating = moviesJson.getString(MOVIES_RATING);

                if (moviesJson.has(MOVIES_GENRES)) {

                    JSONArray genresJson = moviesJson.getJSONArray(MOVIES_GENRES);
                    String[] genres = new String[genresJson.length()];

                    for (int i = 0; i < genresJson.length(); i++) {
                        JSONObject genre = genresJson.getJSONObject(i);

                        genres[i] = genre.getString("name");
                    }

                }
                return null;// TODO remove null

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Movie[] movieData) {
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

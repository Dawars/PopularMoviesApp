package me.dawars.popularmoviesapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.dawars.popularmoviesapp.data.Movie;
import me.dawars.popularmoviesapp.utils.NetworkUtils;

import static me.dawars.popularmoviesapp.utils.NetworkUtils.SORT_POPULAR;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ListItemClickListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String EXTRA_MOVIE = "EXTRA_MOVIE_KEY";
    private static final String MOVIES_STATUS_CODE = "status_code";

    @BindView(R.id.rv_movie_thumbnails)
    RecyclerView recyclerView;

    private GridLayoutManager layoutManager;
    private MovieAdapter movieAdapter;

    @BindView(R.id.pb_loader)
    ProgressBar loadingIndicator;

    @BindView(R.id.tv_error)
    TextView errorMessageDisplay;

    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        Log.v(TAG, "onCreate");

        layoutManager = new GridLayoutManager(this, 3);

        movieAdapter = new MovieAdapter(this);
        recyclerView.setAdapter(movieAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        loadMovieData(SORT_POPULAR);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Log.v(TAG, "onConfigurationChanged");

        // change span count based on orientation
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager.setSpanCount(5);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager.setSpanCount(3);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sort_menu_item) {
            // TODO: add option to change sorting criteria
//            loadMovieData();
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadMovieData(String sortBy) {
        // TODO check for internet connectivity - snack bar

        showMovieDataView();
        new MovieLoader().execute(sortBy);
    }

    void showMovieDataView() {
        errorMessageDisplay.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    void showErrorView() {
        errorMessageDisplay.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onItemClick(int position) {
        Movie movie = movieAdapter.getMovie(position);

        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(EXTRA_MOVIE, movie);

        startActivity(intent);
    }

    class MovieLoader extends AsyncTask<String, Void, List<Movie>> {
        // TODO Change to AsyncTaskLoader
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Movie> doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            URL url = NetworkUtils.buildUrl(params[0]);

            String jsonResponse = null;

            try {
                jsonResponse = NetworkUtils.getResponseFromHttpUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            // TODO display snack bar with error if possible here
            try {
                JSONObject jsonObject = new JSONObject(jsonResponse);
                if (jsonObject.has(MOVIES_STATUS_CODE)) {
                    int errorCode = jsonObject.getInt(MOVIES_STATUS_CODE);
                    switch (errorCode) {
                        case 34: // The resource you requested could not be found.
                            return null;
                        case 7: // Invalid API key: You must be granted a valid key.
                            return null;
                        default:
                            // Server probably down
                            return null;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

            Movie.Response response = new Gson().fromJson(jsonResponse, Movie.Response.class);

            if (response.movies == null) {
                return null;
            }
            return response.movies;
        }

        @Override
        protected void onPostExecute(List<Movie> movieData) {
            loadingIndicator.setVisibility(View.INVISIBLE);

            if (movieData != null && movieData.size() > 0) {
                showMovieDataView();
                movieAdapter.setMovieData(movieData);
            } else {
                showErrorView();
            }
        }
    }
}

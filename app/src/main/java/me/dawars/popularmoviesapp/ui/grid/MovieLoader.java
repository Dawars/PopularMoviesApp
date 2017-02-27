package me.dawars.popularmoviesapp.ui.grid;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import me.dawars.popularmoviesapp.data.Movie;
import me.dawars.popularmoviesapp.utils.NetworkUtils;

/**
 * Created by dawars on 2/27/17.
 */

public class MovieLoader implements LoaderManager.LoaderCallbacks<List<Movie>> {
    public static final String TAG = MovieLoader.class.getSimpleName();

    public static final String MOVIE_PAGE_KEY = "PAGE_KEY";
    public static final String MOVIE_SORT_KEY = "SORT_KEY";

    private static final String MOVIES_STATUS_CODE = "status_code";

    private final MovieLoaderCallback listener;
    private Context context;

    protected MovieLoader(Context context, MovieLoaderCallback listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<List<Movie>>(context) {
            public List<Movie> movieData;

            int page;
            int sortCrit;

            @Override
            protected void onStartLoading() {
                Log.i(TAG, "Loader started loading");

                page = args.getInt(MOVIE_PAGE_KEY);
                sortCrit = args.getInt(MOVIE_SORT_KEY);

                if (movieData != null) {
                    Log.i(TAG, "Data already present");
                    deliverResult(movieData);
                } else {
                    forceLoad();
                }
            }

            @Override
            public List<Movie> loadInBackground() {
                Log.i(TAG, "Loading in background");

                URL url = NetworkUtils.buildMovieUrl(page, sortCrit);

                String jsonResponse;

                try {
                    jsonResponse = NetworkUtils.getResponseFromHttpUrl(url);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;// timeout
                }

                try {
                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    if (jsonObject.has(MOVIES_STATUS_CODE)) {
                        return null;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }

                Movie.Result response = new Gson().fromJson(jsonResponse, Movie.Result.class);

                if (response == null) {
                    return null;
                }

                return response.movies;
            }

            @Override
            public void deliverResult(List<Movie> data) {
                Log.i(TAG, "Loader deliver result");

                movieData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movieData) {
        Log.i(TAG, "onFinished loading");
        listener.onComplete(movieData);
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
    }

}

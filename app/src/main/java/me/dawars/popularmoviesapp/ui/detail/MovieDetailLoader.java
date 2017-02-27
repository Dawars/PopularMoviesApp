package me.dawars.popularmoviesapp.ui.detail;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import me.dawars.popularmoviesapp.data.MovieDetail;
import me.dawars.popularmoviesapp.utils.NetworkUtils;

/**
 * Created by dawars on 2/12/17.
 */

public abstract class MovieDetailLoader implements LoaderManager.LoaderCallbacks<MovieDetail> {

    private static final String STATUS_CODE = "status_code";

    public static final String MOVIE_ID_KEY = "id_key";


    private final Context context;

    public MovieDetailLoader(Context context) {
        this.context = context;
    }

    @Override
    public Loader<MovieDetail> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<MovieDetail>(context) {
            public MovieDetail data;

            @Override
            protected void onStartLoading() {
                if (data != null) {
                    deliverResult(data);
                } else {
                    forceLoad();
                }
            }

            @Override
            public MovieDetail loadInBackground() {
                String id = args.getString(MOVIE_ID_KEY);
                URL url = NetworkUtils.buildDetailUrl(id);

                String jsonResponse = null;

                try {
                    jsonResponse = NetworkUtils.getResponseFromHttpUrl(url);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }

                try {
                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    if (jsonObject.has(STATUS_CODE)) {
                        int errorCode = jsonObject.getInt(STATUS_CODE);
                        switch (errorCode) {
                            case 34: // The resource you requested could not be found.
//                                snackbar(R.string.error_resource_not_found); FIXME: display error
                                return null;
                            case 7: // Invalid API key: You must be granted a valid key.
//                                snackbar(R.string.error_invalid_api_key);
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

                MovieDetail response = new Gson().fromJson(jsonResponse, MovieDetail.class);

                if (response == null) {
                    return null;
                }
                return response;
            }

            @Override
            public void deliverResult(MovieDetail data) {
                this.data = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<MovieDetail> loader, MovieDetail data) {
        onFinish(data);
    }

    public abstract void onFinish(MovieDetail data);

    @Override
    public void onLoaderReset(Loader<MovieDetail> loader) {
    }
}


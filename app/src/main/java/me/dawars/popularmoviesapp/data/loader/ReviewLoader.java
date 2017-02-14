package me.dawars.popularmoviesapp.data.loader;

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
import java.util.List;

import me.dawars.popularmoviesapp.adapter.ReviewAdapter;
import me.dawars.popularmoviesapp.data.Review;
import me.dawars.popularmoviesapp.utils.NetworkUtils;

/**
 * Created by dawars on 2/12/17.
 */

public class ReviewLoader implements LoaderManager.LoaderCallbacks<List<Review>> {

    private static final String STATUS_CODE = "status_code";

    public static final String MOVIE_ID_KEY = "id_key";


    private final Context context;
    private final ReviewAdapter adapter;

    public ReviewLoader(Context context, ReviewAdapter adapter) {
        this.context = context;
        this.adapter = adapter;
    }

    @Override
    public Loader<List<Review>> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<List<Review>>(context) {
            public List<Review> reviewData;

            @Override
            protected void onStartLoading() {
                if (reviewData != null) {
                    deliverResult(reviewData);
                } else {
//                    loadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Override
            public List<Review> loadInBackground() {
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

                Review.Result response = new Gson().fromJson(jsonResponse, Review.Result.class);

                if (response == null) {
                    return null;
                }
                return response.reviews;
            }

            @Override
            public void deliverResult(List<Review> data) {
                reviewData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Review>> loader, List<Review> review) {
        adapter.setReviewData(review);
//        swipreRefresh.setRefreshing(false);

//        if (review != null && review.size() > 0) {
//            showMovieDataView();
//        } else {
//            showErrorView();
//        }
    }

    @Override
    public void onLoaderReset(Loader<List<Review>> loader) {
    }
}


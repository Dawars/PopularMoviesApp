package me.dawars.popularmoviesapp.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import me.dawars.popularmoviesapp.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by dawars on 1/15/17.
 */

public class NetworkUtils {
    public static final String TAG = NetworkUtils.class.getSimpleName();

    // TODO: remove api key when publishing
    private static final String MOVIES_API_KEY = BuildConfig.MOVIE_DB_API_KEY;


    private static final String MOVIES_BASE_URL = "http://api.themoviedb.org/3/movie/";

    public static final String SORT_POPULAR = "popular";
    public static final String SORT_RATING = "top_rated";

    private static final String API_PARAM = "api_key";
    private static final String PAGE_PARAM = "page";


    /**
     * Builds url for most popular/highly rated movies and
     *
     * @param param can be SORT_POPULAR, SORT_RATING or a movie id
     * @return
     */
    public static URL buildUrl(String param) {
        Uri uri = Uri.parse(MOVIES_BASE_URL + param).buildUpon()
                .appendQueryParameter(API_PARAM, MOVIES_API_KEY).build();

        Log.v(TAG, "Url: " + uri.toString());

        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();

        return response.body().string();
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Get poster url in appropriate size
     *
     * @param posterUrl
     * @return
     */
    public static Uri getImageUri(String posterUrl, int width) {
        //"w92", "w154", "w185", "w342", "w500", "w780"
        String w;

        if (width <= 92) {
            w = "w92";
        } else if (width <= 154) {
            w = "w154";
        } else if (width <= 185) {
            w = "w185";
        } else if (width <= 342) {
            w = "w342";
        } else if (width <= 500) {
            w = "w500";
        } else {
            w = "w780";
        }
        return Uri.parse("http://image.tmdb.org/t/p/" + w + "/" + posterUrl);
    }
}

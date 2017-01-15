package me.dawars.popularmoviesapp.utils;


import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by dawars on 1/15/17.
 */

public class NetworkUtils {
    public static final String TAG = NetworkUtils.class.getSimpleName();

    // TODO: remove api key when publishing
    private static final String MOVIES_API_KEY = "";


    private static final String MOVIES_BASE_URL = "http://api.themoviedb.org/3/movie/";

    private static final String API_PARAM = "api_key";
    private static final String PAGE_PARAM = "page";


    public static URL buildUrl(String sort) {
        Uri uri = Uri.parse(MOVIES_BASE_URL + sort).buildUpon()
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
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static Uri getPosterUri(String posterUrl) {
        // could change image size based on screen size
        return Uri.parse("http://image.tmdb.org/t/p/w342/" + posterUrl);
    }
}

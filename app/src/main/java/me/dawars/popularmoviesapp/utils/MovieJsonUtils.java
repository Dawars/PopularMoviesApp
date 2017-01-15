package me.dawars.popularmoviesapp.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import me.dawars.popularmoviesapp.MovieRecord;

/**
 * Created by dawars on 1/15/17.
 */
public class MovieJsonUtils {
    public static MovieRecord[] getMovieObjectFromJson(String jsonResponse) throws JSONException {
        final String MOVIES_RESULTS = "results";

        final String MOVIES_POSTER = "poster_path";
        final String MOVIES_TITLE = "title";
        final String MOVIES_RATING = "vote_average";

        MovieRecord[] movieRecords;

        JSONObject moviesJson = new JSONObject(jsonResponse);

        JSONArray moviesArray = moviesJson.getJSONArray(MOVIES_RESULTS);

        movieRecords = new MovieRecord[moviesArray.length()];


        for (int i = 0; i < moviesArray.length(); i++) {
            String title;
            String imageUrl;
            float rating;

            JSONObject movie = moviesArray.getJSONObject(i);

            title = movie.getString(MOVIES_TITLE);
            imageUrl = movie.getString(MOVIES_POSTER);
            rating = (float) movie.getDouble(MOVIES_RATING);

            movieRecords[i] = new MovieRecord(title, imageUrl, rating);
        }

        return movieRecords;
    }
}

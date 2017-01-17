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

        final String MOVIES_ID = "id";
        final String MOVIES_POSTER = "poster_path";
        final String MOVIES_TITLE = "title";
        final String MOVIES_RATING = "vote_average";

        final String MOVIES_STATUS_CODE = "status_code";

        final String MOVIES_STATUS_MESSAGE = "status_message";

        MovieRecord[] movieRecords;

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
        JSONArray moviesArray = moviesJson.getJSONArray(MOVIES_RESULTS);

        movieRecords = new MovieRecord[moviesArray.length()];


        for (int i = 0; i < moviesArray.length(); i++) {
            int id;
            String title;
            String imageUrl;
            float rating;

            JSONObject movie = moviesArray.getJSONObject(i);

            id = Integer.parseInt(movie.getString(MOVIES_ID));
            title = movie.getString(MOVIES_TITLE);
            imageUrl = movie.getString(MOVIES_POSTER);
            rating = (float) movie.getDouble(MOVIES_RATING);

            movieRecords[i] = new MovieRecord(id, title, imageUrl, rating);
        }

        return movieRecords;
    }
}

package me.dawars.popularmoviesapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {

    // The authority, which is how your code knows which Content Provider to access
    public static final String AUTHORITY = "me.dawars.popularmovies";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);


    public static final String PATH_FAVOURITES = "favourites";

    // Define the possible paths for accessing data in this contract
    // This is the path for the "tasks" directory
    public static final class MovieEntry implements BaseColumns {

        // TaskEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVOURITES).build();


        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_MOVIE_ID = "movie_id";// TODO movie id?
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_URL = "poster";
        public static final String COLUMN_BACKDROP_URL = "backdrop";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_VOTE = "vote";
        public static final String COLUMN_RELEASE_DATE = "release";
    }
}
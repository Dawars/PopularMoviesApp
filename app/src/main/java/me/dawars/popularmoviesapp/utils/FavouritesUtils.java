package me.dawars.popularmoviesapp.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import me.dawars.popularmoviesapp.data.Movie;
import me.dawars.popularmoviesapp.data.MovieContract;

/**
 * Created by dawars on 2/25/17.
 */

public class FavouritesUtils {
    private final Context context;

    public FavouritesUtils(Context context) {
        this.context = context.getApplicationContext();
    }

    public void addToFavorites(Movie movie) {
        context.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, movie.toContentValues());
    }

    public void removeFromFavorites(Movie movie) {
        context.getContentResolver().delete(
                MovieContract.MovieEntry.buildMovieUriWithId(movie.getId()),
                null,
                null
        );
    }

    public boolean isFavorite(Movie movie) {
        boolean favorite = false;
        Cursor cursor = context.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = " + movie.getId(),
                null,
                null
        );
        if (cursor != null) {
            favorite = cursor.getCount() != 0;
            cursor.close();
        }
        return favorite;
    }
}

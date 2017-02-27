package me.dawars.popularmoviesapp.ui.grid;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import java.util.ArrayList;
import java.util.List;

import me.dawars.popularmoviesapp.data.Movie;
import me.dawars.popularmoviesapp.data.MovieContract;

/**
 * Created by dawars on 2/27/17.
 */

public class FavouriteLoader implements LoaderManager.LoaderCallbacks<Cursor> {
    private final Context context;
    private final MovieLoaderCallback listener;

    public FavouriteLoader(Context context, MovieLoaderCallback callback) {
        this.context = context;
        this.listener = callback;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(context, MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        List<Movie> movies = new ArrayList<>();

        data.moveToFirst();
        while(data.moveToNext()){
            movies.add(new Movie(data));
        }

        this.listener.onComplete(movies);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}

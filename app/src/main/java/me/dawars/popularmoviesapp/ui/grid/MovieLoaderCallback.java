package me.dawars.popularmoviesapp.ui.grid;

import android.support.v4.content.Loader;

import java.util.List;

import me.dawars.popularmoviesapp.data.Movie;

/**
 * Created by dawars on 2/27/17.
 */
public interface MovieLoaderCallback {
    void onComplete(List<Movie> movieData);
}

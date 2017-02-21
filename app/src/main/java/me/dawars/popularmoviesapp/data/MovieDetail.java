package me.dawars.popularmoviesapp.data;

/**
 * Created by dawars on 2/12/17.
 */

public class MovieDetail {
    int id;
    int budget;
    String backdrop_path;
    String poster_path;
    String homepage;
    String overview;
    String release_date;
    String tagline;

    public Review.Result reviews;
    public Video.Result videos;

}

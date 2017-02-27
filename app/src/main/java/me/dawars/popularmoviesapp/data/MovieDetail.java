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

    public int getRuntime() {
        return runtime;
    }

    int runtime;

    public Review.Result reviews;
    public Video.Result videos;
    public Cast.Result cast;

    public String getTagline() {
        return tagline;
    }

    public int getBudget() {
        return budget;
    }

    public String getBackdropPath() {
        return backdrop_path;
    }

    public String getPosterPath() {
        return poster_path;
    }

    public String getHomepage() {
        return homepage;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return release_date;
    }
}

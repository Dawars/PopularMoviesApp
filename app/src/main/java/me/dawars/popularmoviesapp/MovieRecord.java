package me.dawars.popularmoviesapp;

/**
 * Created by dawars on 1/15/17.
 */

public class MovieRecord {
    String title;
    String imageUrl;
    float rating;

    public MovieRecord(String title, String imageUrl, float rating) {

        this.title = title;
        this.imageUrl = imageUrl;
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterUrl() {
        return imageUrl;
    }

    public float getRating() {
        return rating;
    }
}

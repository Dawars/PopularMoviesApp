package me.dawars.popularmoviesapp;

/**
 * Created by dawars on 1/15/17.
 */

public class MovieRecord {
    String mTitle;
    String mImageUrl;
    float rating;

    public MovieRecord(String mTitle, String mImageUrl, float rating) {

        this.mTitle = mTitle;
        this.mImageUrl = mImageUrl;
        this.rating = rating;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getPosterUrl() {
        return mImageUrl;
    }

    public float getRating() {
        return rating;
    }
}

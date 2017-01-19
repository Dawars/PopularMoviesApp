package me.dawars.popularmoviesapp.data;

/**
 * Created by dawars on 1/15/17.
 */

public class Movie {
    String id;
    String title;
    String imageUrl;
    float rating;

    // TODO GSON

    public Movie(String id, String title, String imageUrl, float rating) {
        this.id = id;
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

    public String getId() {
        return id;
    }
}

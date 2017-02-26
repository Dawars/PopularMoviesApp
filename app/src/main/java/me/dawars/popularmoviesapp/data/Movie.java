package me.dawars.popularmoviesapp.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dawars on 1/15/17.
 */

public class Movie implements Parcelable {

    @SerializedName("poster_path")
    String posterPath;

    @SerializedName("adult")
    boolean adult;

    @SerializedName("overview")
    String overview;

    @SerializedName("release_date")
    String releaseDate;

    @SerializedName("genre_ids")
    List<Integer> genreIds;

    @SerializedName("id")
    int id;

    @SerializedName("original_title")
    String origTitle;

    @SerializedName("original_language")
    String origLang;

    @SerializedName("title")
    String title;

    @SerializedName("backdrop_path")
    String backdropPath;

    @SerializedName("popularity")
    float popularity;

    @SerializedName("vote_count")
    int voteCount;

    @SerializedName("video")
    boolean video;

    @SerializedName("vote_average")
    float voteAvg;

    public Movie(Cursor cursor) {
        this.setId((int) cursor.getLong(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID)));
        this.setTitle(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)));
        this.setOverview(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW)));
        this.setReleaseDate(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)));
        this.setPosterPath(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_URL)));
        this.setVoteAvg((float) cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE)));
        this.setBackdropPath(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_BACKDROP_URL)));

    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, id);
        values.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
        values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview);
        values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
        values.put(MovieContract.MovieEntry.COLUMN_POSTER_URL, posterPath);
        values.put(MovieContract.MovieEntry.COLUMN_VOTE, voteAvg);
        values.put(MovieContract.MovieEntry.COLUMN_BACKDROP_URL, backdropPath);
        return values;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setGenreIds(List<Integer> genreIds) {
        this.genreIds = genreIds;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setOrigTitle(String origTitle) {
        this.origTitle = origTitle;
    }

    public void setOrigLang(String origLang) {
        this.origLang = origLang;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public void setPopularity(float popularity) {
        this.popularity = popularity;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public void setVoteAvg(float voteAvg) {
        this.voteAvg = voteAvg;
    }


    public static final class Result {

        @SerializedName("page")
        public int page;

        @SerializedName("total_pages")
        public int totalPages;

        @SerializedName("total_results")
        public int totalMovies;

        @SerializedName("results")
        public ArrayList<Movie> movies = new ArrayList<>();
    }

    public Movie(int id,
                 boolean adult,
                 String poster_path,
                 String overview,
                 String release_date,
                 List<Integer> genre_ids,
                 String original_title,
                 String original_language,
                 String title,
                 String backdrop_path,
                 float popularity,
                 boolean video,
                 float vote_average,
                 int vote_count) {

        this.id = id;
        this.adult = adult;
        this.posterPath = poster_path;
        this.overview = overview;
        this.releaseDate = release_date;
        this.genreIds = genre_ids;
        this.origTitle = original_title;
        this.origLang = original_language;
        this.title = title;
        this.backdropPath = backdrop_path;
        this.popularity = popularity;
        this.video = video;
        this.voteAvg = vote_average;
        this.voteCount = vote_count;

    }

    public boolean isAdult() {
        return adult;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public float getVoteAvg() {
        return voteAvg;
    }

    public float getPopularity() {
        return popularity;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public List<Integer> getGenreIds() {
        return genreIds;
    }

    public String getOrigLang() {
        return origLang;
    }

    public String getOrigTitle() {
        return origTitle;
    }

    public String getOverview() {
        return overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public boolean hasVideo() {
        return video;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeByte((byte) (adult ? 1 : 0));
        dest.writeString(posterPath);
        dest.writeString(overview);
        dest.writeString(releaseDate);
        dest.writeList(genreIds);
        dest.writeString(origTitle);
        dest.writeString(origLang);
        dest.writeString(title);
        dest.writeString(backdropPath);
        dest.writeFloat(popularity);
        dest.writeByte((byte) (video ? 1 : 0));
        dest.writeFloat(voteAvg);
        dest.writeInt(voteCount);
    }

    //creator
    public static final Creator CREATOR = new Creator() {

        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }

    };

    public Movie(Parcel in) {
        id = in.readInt();
        adult = in.readByte() != 0;
        posterPath = in.readString();
        overview = in.readString();
        releaseDate = in.readString();

        genreIds = new ArrayList<>();
        in.readList(genreIds, null);

        origTitle = in.readString();
        origLang = in.readString();
        title = in.readString();
        backdropPath = in.readString();
        popularity = in.readFloat();
        video = in.readByte() != 0;
        voteAvg = in.readFloat();
        voteCount = in.readInt();
    }
}

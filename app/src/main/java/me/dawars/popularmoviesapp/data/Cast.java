package me.dawars.popularmoviesapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dawars on 1/15/17.
 */

public class Cast implements Parcelable {

    String name;
    String character;
    String profile_path;

    public String getName() {
        return name;
    }

    public String getCharacter() {
        return character;
    }

    public String getProfilePath() {
        return profile_path;
    }

    public Cast(String name, String character, String profile_path) {
        this.name = name;
        this.character = character;
        this.profile_path = profile_path;
    }


    public static final class Credits {
        @SerializedName("cast")
        public ArrayList<Cast> cast = new ArrayList<>();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.character);
        dest.writeString(this.profile_path);
    }

    protected Cast(Parcel in) {
        this.name = in.readString();
        this.character = in.readString();
        this.profile_path = in.readString();
    }

    public static final Creator<Cast> CREATOR = new Creator<Cast>() {
        @Override
        public Cast createFromParcel(Parcel source) {
            return new Cast(source);
        }

        @Override
        public Cast[] newArray(int size) {
            return new Cast[size];
        }
    };
}

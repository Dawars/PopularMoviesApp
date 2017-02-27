package me.dawars.popularmoviesapp.utils;

import android.support.annotation.StringRes;

import me.dawars.popularmoviesapp.R;

/**
 * Created by dawars on 2/27/17.
 */

public class GenreHelper {

    @StringRes
    public static int getGenreStringId(int id) {

        int name = -1;

        switch (id) {
            case 28:
                name = R.string.action;
                break;
            case 12:
                name = R.string.adventure;
                break;
            case 16:
                name = R.string.animation;
                break;
            case 35:
                name = R.string.comedy;
                break;
            case 80:
                name = R.string.crime;
                break;
            case 99:
                name = R.string.documentary;
                break;
            case 18:
                name = R.string.drama;
                break;
            case 10751:
                name = R.string.family;
                break;
            case 14:
                name = R.string.fantasy;
                break;
            case 10769:
                name = R.string.foreign;
                break;
            case 36:
                name = R.string.history;
                break;
            case 27:
                name = R.string.horror;
                break;
            case 10402:
                name = R.string.music;
                break;
            case 9648:
                name = R.string.mystery;
                break;
            case 10749:
                name = R.string.romance;
                break;
            case 878:
                name = R.string.sci_fi;
                break;
            case 10770:
                name = R.string.tv_movie;
                break;
            case 53:
                name = R.string.thriller;
                break;
            case 10752:
                name = R.string.war;
                break;
            case 37:
                name = R.string.western;
                break;
        }
        return name;
    }
}

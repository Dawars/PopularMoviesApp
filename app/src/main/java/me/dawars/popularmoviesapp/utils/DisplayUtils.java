package me.dawars.popularmoviesapp.utils;

import android.app.Activity;
import android.util.DisplayMetrics;

/**
 * Created by dawars on 2/12/17.
 */

public class DisplayUtils {
    public static DisplayMetrics getScreenMetrics(Activity activity) {

        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics;
    }
}

package movies.popular.jd.com.udacitypopularmovies.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import static movies.popular.jd.com.udacitypopularmovies.tasks.MovieTaskHelper.CURRENT_SELECTED_MOVIE;
import static movies.popular.jd.com.udacitypopularmovies.tasks.MovieTaskHelper.POPULAR_CRITERIA;
import static movies.popular.jd.com.udacitypopularmovies.tasks.MovieTaskHelper.VIEW_CRITERIA;

/**
 * Created by chuondao on 1/14/17.
 */

public class SharedPreferenceHelper {

    public static int getViewCriteriaFromPref(Context ctx){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        return sp.getInt(VIEW_CRITERIA,POPULAR_CRITERIA);
    }

    public static String getCurrectSelectedMovieId(Context ctx){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        return sp.getString(VIEW_CRITERIA,null);
    }


    public static void storeViewCriteriaToPref(Context ctx, int i){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        sp.edit().putInt(VIEW_CRITERIA,i).commit();
    }

    public static void storeCurrentSelectedMovie(Context ctx, String movieId){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        sp.edit().putString(CURRENT_SELECTED_MOVIE,movieId);
    }
}



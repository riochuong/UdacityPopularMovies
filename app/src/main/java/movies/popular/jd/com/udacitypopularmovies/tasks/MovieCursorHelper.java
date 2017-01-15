package movies.popular.jd.com.udacitypopularmovies.tasks;

import android.database.Cursor;

import movies.popular.jd.com.udacitypopularmovies.data.MovieContract;

/**
 * Created by chuondao on 1/15/17.
 */

public class MovieCursorHelper {

    /**
     * helper to get movie Id to
     * @param cursor
     * @return
     */
    public static String getMovieIdFromCursor(Cursor cursor){
        if (cursor != null){
            return cursor.getString(
                    cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID));
        }
        return null;
    }


    public static String getMovieNameFromCursor(Cursor cursor){
        if (cursor != null){
            return cursor.getString(
                    cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));
        }
        return null;
    }

    /**
     * check if movie is favorite
     * @param cursor
     * @return
     */
    public static int isMovieFavorite(Cursor cursor){
        if (cursor != null){
            int isFav =  cursor.getInt(
                    cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_FAVORITE));
            return isFav;
        }
        return 0;
    }
}

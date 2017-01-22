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

    public static String getMovieReleaseDateFromCursor(Cursor cursor){
        if (cursor != null){
            return cursor.getString(
                    cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE));
        }
        return null;
    }


    public static String getMovieRatingFromCursor(Cursor cursor){
        if (cursor != null){
            double rating = cursor.getDouble(
                    cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVG));
            return String.format("%.1f",rating);
        }
        return null;
    }

    public static String getMoviePopluarityFromCursor(Cursor cursor){
        if (cursor != null){
            double popular =  cursor.getDouble(
                    cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POPULARITY));
            return String.format("%.1f",popular);
        }
        return null;
    }

    public static String getMoviePosterPath(Cursor cursor){
        if (cursor != null){
            return cursor.getString(
                    cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH));
        }
        return null;
    }


    /**
     * check if movie is favorite
     * @param cursor
     * @return
     */
    public static int getMovieFavorField(Cursor cursor){
        if (cursor != null){
            int isFav =  cursor.getInt(
                    cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_FAVORITE));
            return isFav;
        }
        return 0;
    }

    public static String getMovieOverView(Cursor cursor) {
        if (cursor != null){
            return cursor.getString(
                    cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW));
        }
        return null;

    }
}

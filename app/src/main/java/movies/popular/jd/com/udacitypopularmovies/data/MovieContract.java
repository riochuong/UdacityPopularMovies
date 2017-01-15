package movies.popular.jd.com.udacitypopularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by chuondao on 1/11/17.
 */

public class MovieContract {


    // the name of the content provider
    public static final String CONTENT_AUTHORITY = "movies.popular.jd.com.udacitypopularmovies";


    // base uri used for contact content provider
    public static  final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // valid query for table which is append after BASE_CONTENT_URI
    public  static final String PATH_MOVIES = "movies";
    // MOVIE TABLE
    public static final class MovieEntry implements BaseColumns {

        public static final String IMG_BASE_URI = "http://image.tmdb.org/t/p/w185";

        public static Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String TABLE_NAME = "movies";


        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                        +CONTENT_AUTHORITY+"/"+ PATH_MOVIES;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                        +CONTENT_AUTHORITY+"/"+ PATH_MOVIES;

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_ADULT = "adult";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_VIDEO = "video";
        public static final String COLUMN_VOTE_AVG = "vote_avg";
        public static final String COLUMN_VOTE_COUNT = "vote_count";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_FAVORITE = "favor";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_IMAGE = "image";

        /* Query all movies */
        public static Uri buildMovieListUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }

        public static Uri buildMovieFavUpdateUri(String movieId){
           return CONTENT_URI.buildUpon().appendPath(movieId).appendPath("fav").build();
        }



        public static String getMovieIdFromUri (Uri uri){
            return uri.getPathSegments().get(1);
        }

    }

}

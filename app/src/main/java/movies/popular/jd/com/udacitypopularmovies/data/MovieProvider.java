package movies.popular.jd.com.udacitypopularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by chuondao on 1/11/17.
 */

public class MovieProvider  extends ContentProvider{

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private  MovieDBHelper mMovieDBHelper;
    // URI ID
    static final int MOVIES = 400;
    static final int MOVIES_FAVORITE = 403;
    static final int MOVIES_HIGHEST_RATED = 401;
    static final int MOVIES_MOST_POPULAR = 406;
    static final int MOVIE_SPECIFIC_TRAILERS = 402;
    static final int MOVIE_SPECIFIC_REVIEWS = 405;
    static final int MOVIE_ITEM_INFO = 404;


    @Override
    public boolean onCreate() {
        //create new db
        mMovieDBHelper = new MovieDBHelper(getContext());
        return true;
    }



    /**
     * deteremine the URI type here
     * @param uri
     * @return
     */
    @Nullable
    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match){
            case MOVIES:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_ITEM_INFO:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case MOVIE_SPECIFIC_TRAILERS:
                return MovieContract.TrailerEntry.CONTENT_TYPE;
            case MOVIE_SPECIFIC_REVIEWS:
                return MovieContract.ReviewEntry.CONTENT_TYPE;
            case MOVIES_HIGHEST_RATED:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    /**
     * insert on ContentValues to DB in one transaction which proves to be
     * more efficient.
     * @param uri
     * @param values
     * @return
     */
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {

        final SQLiteDatabase db = mMovieDBHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        // match case to insert data
        switch (match){
            case MOVIES:
                int totalInserted = 0;
                db.beginTransaction();
                try {
                    for (ContentValues val : values){
                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME,null,val);
                        if (_id != -1){
                            totalInserted++;
                        }
                    }
                    // done
                    db.setTransactionSuccessful();
                } finally {
                    // end transaction to keep data consistent
                    db.endTransaction();
                }
                return totalInserted;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        return null;
    }

    static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        
        matcher.addURI(MovieContract.CONTENT_AUTHORITY,
                                                MovieContract.PATH_MOVIES, MOVIES);

        matcher.addURI(MovieContract.CONTENT_AUTHORITY,
                                    MovieContract.PATH_FAVORITE_MOVIES, MOVIES_FAVORITE);

        // used to get either trailers or reviews
        matcher.addURI(MovieContract.CONTENT_AUTHORITY,
                                MovieContract.PATH_MOVIES +"/#/trailers",
                MOVIE_SPECIFIC_TRAILERS);

        matcher.addURI(MovieContract.CONTENT_AUTHORITY,
                MovieContract.PATH_MOVIES +"/#/reviews",
                MOVIE_SPECIFIC_REVIEWS);

        // uri used to get movies specific info.
        matcher.addURI(MovieContract.CONTENT_AUTHORITY,
                MovieContract.PATH_MOVIES +"/#", MOVIE_ITEM_INFO);

        // uri used to get movies specific info.
        matcher.addURI(MovieContract.CONTENT_AUTHORITY,
                MovieContract.PATH_MOVIES +"/highest_rated", MOVIES_HIGHEST_RATED);

        matcher.addURI(MovieContract.CONTENT_AUTHORITY,
                MovieContract.PATH_MOVIES +"/most_popular", MOVIES_MOST_POPULAR);

        return matcher;
    }
}

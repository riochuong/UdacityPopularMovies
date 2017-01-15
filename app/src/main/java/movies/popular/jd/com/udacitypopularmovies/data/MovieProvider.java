package movies.popular.jd.com.udacitypopularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
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
    static final int MOVIES_FAVORITE = 401;
    static final int MOVIE_ITEM_INFO = 402;
    static final int MOVIE_ITEM_FAV_UPDATE = 403;


    private static final SQLiteQueryBuilder sMovieDBQueryBuilder;

    static {
        sMovieDBQueryBuilder = new SQLiteQueryBuilder();
        sMovieDBQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_NAME
        );
    }


    @Override
    public boolean onCreate() {
        //create new db
        mMovieDBHelper = new MovieDBHelper(getContext());
        return true;
    }


    public static final String sFavoriteMovieSelection =
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_FAVORITE +
                    " = ? ";

    public static final String sSpecificMovieSelection =
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID +
                    " = ? ";



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
            case MOVIES_FAVORITE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_ITEM_FAV_UPDATE:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
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
                return bulkInsertToDb(MovieContract.MovieEntry.TABLE_NAME, db, values, uri);
            default:
                return super.bulkInsert(uri, values);
        }
    }


    /**
     * Helper to avoid repeated code for bulk insert.
     * @param tabName
     * @param values
     * @return
     */
    private int bulkInsertToDb(String tabName,
                               SQLiteDatabase db,
                               ContentValues[] values,
                               Uri uri){
        int totalInserted = 0;
        db.beginTransaction();
        try {
            for (ContentValues val : values){
                // ignore conflict to preserved favortie movies
                long _id= db.insertWithOnConflict(
                            tabName,
                            null,
                            val,
                            SQLiteDatabase.CONFLICT_IGNORE);
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
        getContext().getContentResolver().notifyChange(uri,null);
        return totalInserted;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        int match = sUriMatcher.match(uri);
        SQLiteDatabase db = mMovieDBHelper.getWritableDatabase();
        switch(match){
            case MOVIES:
                long _id = db.insertWithOnConflict(
                        MovieContract.MovieEntry.TABLE_NAME,
                        null,
                        contentValues,
                        SQLiteDatabase.CONFLICT_IGNORE);;
                if (_id > 0){
                    return MovieContract.MovieEntry.buildMovieListUri(_id);
                }
                else
                    throw new android.database.SQLException("Failed to insert into "+uri);
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        int match = sUriMatcher.match(uri);
        SQLiteDatabase db = mMovieDBHelper.getWritableDatabase();
        switch(match){
            case MOVIES:
                int rowCount = db.delete(MovieContract.MovieEntry.TABLE_NAME, s , strings);
                getContext().getContentResolver().notifyChange(uri,null);
                return rowCount;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);

        }
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        int match = sUriMatcher.match(uri);
        SQLiteDatabase db = mMovieDBHelper.getWritableDatabase();
        int rowCount;
        switch(match){
            case MOVIES:
                rowCount =
                        db.update(MovieContract.MovieEntry.TABLE_NAME, contentValues, s , strings);
                // notify affected observer
                getContext().getContentResolver().notifyChange(uri,null);
                return rowCount;
            case MOVIE_ITEM_FAV_UPDATE:

                rowCount =
                        db.update(MovieContract.MovieEntry.TABLE_NAME, contentValues, s , strings);
                getContext().getContentResolver().notifyChange(uri,null);
                return rowCount;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
    }



    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortingOrder) {
        int match = sUriMatcher.match(uri);
        SQLiteDatabase db = mMovieDBHelper.getReadableDatabase();
        Cursor returnCursor = null;
        switch(match){
            case MOVIES:
                returnCursor = db.query(
                           MovieContract.MovieEntry.TABLE_NAME,
                           projection,
                           selection,
                           selectionArgs,
                           null,
                           null,
                           sortingOrder);
                break;
            case MOVIES_FAVORITE:
                returnCursor =
                        sMovieDBQueryBuilder.query(
                                db,
                                projection,
                                sFavoriteMovieSelection,
                                new String[]{"1"},
                                null,
                                null,
                                sortingOrder);
                        break;

            case MOVIE_ITEM_INFO:
                String movieId = MovieContract.MovieEntry.getMovieIdFromUri(uri);
                returnCursor =
                        sMovieDBQueryBuilder.query(
                                db,
                                projection,
                                sSpecificMovieSelection,
                                new String[]{movieId},
                                null,
                                null,
                                sortingOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);

        }

        return returnCursor;
    }

    static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        
        matcher.addURI(MovieContract.CONTENT_AUTHORITY,
                                                MovieContract.PATH_MOVIES, MOVIES);

        matcher.addURI(MovieContract.CONTENT_AUTHORITY,
                                    MovieContract.PATH_MOVIES+"/favorites", MOVIES_FAVORITE);

//        // used to get either trailers or reviews
//        matcher.addURI(MovieContract.CONTENT_AUTHORITY,
//                                MovieContract.PATH_MOVIES +"/#/trailers",
//                MOVIE_SPECIFIC_TRAILERS);
//
//        matcher.addURI(MovieContract.CONTENT_AUTHORITY,
//                MovieContract.PATH_MOVIES +"/#/reviews",
//                MOVIE_SPECIFIC_REVIEWS);

        // uri used to get movies specific info.
        matcher.addURI(MovieContract.CONTENT_AUTHORITY,
                MovieContract.PATH_MOVIES +"/#", MOVIE_ITEM_INFO);

        matcher.addURI(MovieContract.CONTENT_AUTHORITY,
                MovieContract.PATH_MOVIES +"/#/fav", MOVIE_ITEM_FAV_UPDATE);

        // uri used to get movies specific info.
//        matcher.addURI(MovieContract.CONTENT_AUTHORITY,
//                MovieContract.PATH_MOVIES +"/highest_rated", MOVIES_HIGHEST_RATED);
//
//        matcher.addURI(MovieContract.CONTENT_AUTHORITY,
//                MovieContract.PATH_MOVIES +"/most_popular", MOVIES_MOST_POPULAR);

        return matcher;
    }
}

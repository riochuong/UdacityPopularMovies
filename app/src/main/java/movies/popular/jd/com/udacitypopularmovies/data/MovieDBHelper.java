package movies.popular.jd.com.udacitypopularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import movies.popular.jd.com.udacitypopularmovies.data.MovieContract.MovieEntry;
/**
 * Created by chuondao on 1/11/17.
 */

public class MovieDBHelper extends SQLiteOpenHelper {


    public static final String DATABASE_NAME = "movies.db";

    public static final int DATABASE_VERSION = 2;

    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }




    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // create all db tables here

        final String SQL_CREATE_MOVIE_LIST_TABLE =
                "CREATE TABLE "+ MovieEntry.TABLE_NAME+ " ( " +
                        MovieEntry._ID + " INTEGER PRIMARY KEY, "+
                        MovieEntry.COLUMN_ADULT + " BOOLEAN NOT NULL, "+
                        MovieEntry.COLUMN_IMAGE + " BLOB, "+
                        MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, "+
                        MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, "+
                        MovieEntry.COLUMN_TITLE + " TEXT NOT NULL,"+
                        MovieEntry.COLUMN_OVERVIEW + " TEXT, "+
                        MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL,"+
                        MovieEntry.COLUMN_RELEASE_DATE + " INTEGER NOT NULL, "+
                        MovieEntry.COLUMN_VOTE_AVG + " REAL NOT NULL, "+
                        MovieEntry.COLUMN_VOTE_COUNT + " INTEGER NOT NULL, "+
                        MovieEntry.COLUMN_VIDEO + " BOOLEAN NOT NULL, "+
                        MovieEntry.COLUMN_FAVORITE + " BOOLEAN NOT NULL"+
                        " );";

//        final String SQL_CREATE_MOVIE_TRAILERS_TABLE =
//                "CREATE TABLE "+ TrailerEntry.TABLE_NAME+ " ( " +
//                        TrailerEntry._ID + " INTEGER PRIMARY KEY, "+
//                        TrailerEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, "+
//                        TrailerEntry.COLUMN_TRAILER_NAME + " TEXT NOT NULL, "+
//                        TrailerEntry.COLUMN_TRAILER_YOUTUBE_LINK + " TEXT NOT NULL "+
//                        " );";
//
//        final String SQL_CREATE_MOVIE_REVIEWS_TABLE =
//                "CREATE TABLE "+ ReviewEntry.TABLE_NAME+ " ( " +
//                        ReviewEntry._ID + " INTEGER PRIMARY KEY, "+
//                        ReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, "+
//                        ReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL, "+
//                        ReviewEntry.COLUMN_CONTENT + " TEXT NOT NULL "+
//                        " );";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_LIST_TABLE);
   //     sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_REVIEWS_TABLE);
       // sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TRAILERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // drop data if DB version change
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+MovieEntry.TABLE_NAME);
    //    sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TrailerEntry.TABLE_NAME);
    //    sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ReviewEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}

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
                        MovieEntry.COLUMN_ADULT + " INTEGER NOT NULL, "+
                        MovieEntry.COLUMN_IMAGE + " BLOB, "+
                        MovieEntry.COLUMN_MOVIE_ID + " INTEGER UNIQUE NOT NULL, "+
                        MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, "+
                        MovieEntry.COLUMN_TITLE + " TEXT NOT NULL,"+
                        MovieEntry.COLUMN_OVERVIEW + " TEXT, "+
                        MovieEntry.COLUMN_REVIEW + " TEXT, "+
                        MovieEntry.COLUMN_TRAILER + " TEXT, "+
                        MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL,"+
                        MovieEntry.COLUMN_RELEASE_DATE + " INTEGER NOT NULL, "+
                        MovieEntry.COLUMN_VOTE_AVG + " REAL NOT NULL, "+
                        MovieEntry.COLUMN_VOTE_COUNT + " INTEGER NOT NULL, "+
                        MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, "+
                        MovieEntry.COLUMN_VIDEO + " INTEGER NOT NULL, "+
                        MovieEntry.COLUMN_FAVORITE + " INTEGER NOT NULL"+
                        " );";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_LIST_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // drop data if DB version change
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}

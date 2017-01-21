package movies.popular.jd.com.udacitypopularmovies.tasks;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import movies.popular.jd.com.udacitypopularmovies.MovieDetailFragment;
import movies.popular.jd.com.udacitypopularmovies.data.MovieContract;

/**
 * Created by chuondao on 1/21/17.
 */

public class UpdateMovieFavAsyncTask extends AsyncTask<String, String ,String> {
    private Context mCtx;
    private Uri mUri;
    private ContentValues mValues;
    private boolean mNotify = true;

    public UpdateMovieFavAsyncTask(Context ctx, Uri uri, ContentValues values) {
        this.mCtx = ctx;
        this.mUri = uri;
        this.mValues = values;
    }

    @Override
    protected String doInBackground(String[] objects) {
        String movieId = MovieContract.MovieEntry.getMovieIdFromUri(mUri);
        mCtx.getContentResolver()
                .update(
                        mUri,
                        mValues,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + "= ?",
                        new String[]{movieId}
                );

        if (mNotify){
            mCtx.getContentResolver()
                    .notifyChange(mUri, null);
            mCtx.getContentResolver()
                    .notifyChange(MovieContract.MovieEntry.CONTENT_URI, null);
        }
        return null;
    }
}

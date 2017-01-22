package movies.popular.jd.com.udacitypopularmovies.tasks;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import movies.popular.jd.com.udacitypopularmovies.data.MovieContract;
import movies.popular.jd.com.udacitypopularmovies.util.MovieQueryJsonParser;
import movies.popular.jd.com.udacitypopularmovies.util.MovieReview;
import movies.popular.jd.com.udacitypopularmovies.util.MovieTrailer;
import movies.popular.jd.com.udacitypopularmovies.util.StorageHelper;

/**
 * AsyncTask Loader used for fetching favorite movie details
 *
 */

public class FetchFavMovieOfflineModeLoaderTask
        extends AsyncTaskLoader<MovieDetailLoaderInfo> {

    String mMoviedId;
    private  static final String TAG = "FetchMovieDetailTask";
    public FetchFavMovieOfflineModeLoaderTask(Context context, String movieId) {
        super(context);
        mMoviedId = movieId;
    }

    @Override
    public MovieDetailLoaderInfo loadInBackground() {

        if (mMoviedId == null){
            return null;
        }

        Uri uri = MovieContract.MovieEntry.buildMovieListUri(Long.parseLong(mMoviedId));

        Cursor cursor =
                getContext().getContentResolver().query(uri,null,null,null,null);
        cursor.moveToFirst();

        // should only get one unique results back
        if (cursor != null){
            String reviewJson = MovieCursorHelper.getMovieReviewFromCursor(cursor);
            String trailerJson = MovieCursorHelper.getMovieTrailerFromCursor(cursor);
            List<MovieTrailer> trailerList = StorageHelper.getMovieTrailerFromJson(trailerJson);
            List<MovieReview> reviewList = StorageHelper.getMovieReviewFromJson(reviewJson);
            return new MovieDetailLoaderInfo(reviewList,trailerList);
        }
        return null;

    }

}

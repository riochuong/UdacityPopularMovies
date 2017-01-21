package movies.popular.jd.com.udacitypopularmovies.tasks;

import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.util.Pair;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import movies.popular.jd.com.udacitypopularmovies.data.MovieContract;
import movies.popular.jd.com.udacitypopularmovies.util.MovieQueryJsonParser;
import movies.popular.jd.com.udacitypopularmovies.util.MovieReview;
import movies.popular.jd.com.udacitypopularmovies.util.MovieTrailer;

/**
 * AsyncTask Loader used for fetching movie details
 *
 */

public class FetchMovieDetailsLoaderTask
        extends AsyncTaskLoader<MovieDetailLoaderInfo> {

    String mMoviedId;
    private  static final String TAG = "FetchMovieDetailTask";
    public FetchMovieDetailsLoaderTask(Context context, String movieId) {
        super(context);
        mMoviedId = movieId;
    }

    @Override
    public MovieDetailLoaderInfo loadInBackground() {

        if (mMoviedId == null){
            return null;
        }

        Uri reviewUri = MovieTaskHelper.buildMovieReviewsRequestUrl(mMoviedId);
        Uri trailerUri = MovieTaskHelper.buildMovieTrailersRequestUrl(mMoviedId);

        try {
            // Obtain raw data from network call
            String reviewRawString = MovieTaskHelper.fetchHttpDataMovie(reviewUri);
            String trailerRawString = MovieTaskHelper.fetchHttpDataMovie(trailerUri);
            Uri specificMovieQuery = MovieContract.MovieEntry.
                    buildMovieListUri(Integer.parseInt(mMoviedId));


            // Parse raw json and construct the result to be used for displaying data
            MovieDetailLoaderInfo result =
                new MovieDetailLoaderInfo(
                  MovieQueryJsonParser.parseMovieReviews(reviewRawString, mMoviedId),
                  MovieQueryJsonParser.parseMovieTrailers(trailerRawString,mMoviedId)
                );
            return result;

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG,"IO error during fetch movie details");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG,"RAW Json data is not well-formed");
        }

        return null;

    }

}

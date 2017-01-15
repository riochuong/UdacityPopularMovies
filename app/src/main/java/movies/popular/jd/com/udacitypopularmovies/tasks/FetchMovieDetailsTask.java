package movies.popular.jd.com.udacitypopularmovies.tasks;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import movies.popular.jd.com.udacitypopularmovies.util.MovieQueryJsonParser;
import movies.popular.jd.com.udacitypopularmovies.util.MovieReview;
import movies.popular.jd.com.udacitypopularmovies.util.MovieTrailer;

/**
 * AsyncTask Loader used for fetching movie details
 *
 */

public class FetchMovieDetailsTask extends AsyncTaskLoader<Pair
                                <List<MovieReview>,List<MovieTrailer>>> {

    String mMoviedId;
    private  static final String TAG = "FetchMovieDetailTask";
    public FetchMovieDetailsTask(Context context, String movieId) {
        super(context);
        mMoviedId = movieId;
    }

    @Override
    public Pair<List<MovieReview>,List<MovieTrailer>> loadInBackground() {
        Uri reviewUri = MovieTaskHelper.buildMovieReviewsRequestUrl(mMoviedId);
        Uri trailerUri = MovieTaskHelper.buildMovieTrailersRequestUrl(mMoviedId);

        try {
            // Obtain raw data from network call
            String reviewRawString = MovieTaskHelper.fetchHttpDataMovie(reviewUri);
            String trailerRawString = MovieTaskHelper.fetchHttpDataMovie(trailerUri);

            // Parse raw json and construct the result to be used for displaying data
            Pair<List<MovieReview>,List<MovieTrailer>> result =
                new Pair(
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

package movies.popular.jd.com.udacitypopularmovies.tasks;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import movies.popular.jd.com.udacitypopularmovies.data.MovieContract;
import movies.popular.jd.com.udacitypopularmovies.util.MovieQueryJsonParser;

import static movies.popular.jd.com.udacitypopularmovies.tasks.MovieTaskHelper.API_KEY;
import static movies.popular.jd.com.udacitypopularmovies.tasks.MovieTaskHelper.POPULAR_CHOICE;
import static movies.popular.jd.com.udacitypopularmovies.tasks.MovieTaskHelper.POPULAR_MOVIE_BASE_QUERY;
import static movies.popular.jd.com.udacitypopularmovies.tasks.MovieTaskHelper.TOP_RATED_CHOICE;
import static movies.popular.jd.com.udacitypopularmovies.tasks.MovieTaskHelper.TOP_RATED_MOVIE_BASE_QUERY;
import static movies.popular.jd.com.udacitypopularmovies.tasks.MovieTaskHelper.fetchHttpDataMovie;

/**
 * Background task for fetching and parsing Movies data !!!
 */


public class FetchMovieListTask extends AsyncTask<String, Void, Void> {

    private static final String TAG = "FETCH_MOVIE_TASK";

    private Context mContext = null;

    public FetchMovieListTask( Context ctx) {
        mContext = ctx;
    }

    @Override
    protected Void doInBackground(String... strings) {

        // make sure we have a choice for query
        if (strings.length <= 0) {
            return null;
        }

        String queryChoice = strings[0];
        String baseQuery = null;
        HttpURLConnection httpConnection = null;

        Uri queryUri = MovieTaskHelper.buildMovieListRequestUrl(queryChoice);

        if (queryChoice == null){
            Log.e(TAG, "Wrong choice for querying data ");
            return null;
        }

        try {
            // need to add API_KEY before doing query
            String res = fetchHttpDataMovie(queryUri);
            if (res != null) {
                Log.d(TAG,"Successfully Fetching Movies data from Movie DB");
                List<ContentValues> resCVList = MovieQueryJsonParser.parseMovieListResult(res);
                // performing insert to DBs
                ContentValues[] cvArr = new ContentValues[resCVList.size()];


                resCVList.toArray(cvArr);
                if (resCVList.size() > 0){
                        mContext.getContentResolver()
                                .bulkInsert(MovieContract.MovieEntry.CONTENT_URI,
                                        cvArr);
                    Log.d(TAG,"Finished Inserting data to DB");
                }
            }

        } catch (MalformedURLException e) {
            Log.e(TAG, "Error Malformed URL");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, "Failed to Open http connection ");
            e.printStackTrace();
        } catch (JSONException e) {
            Log.e(TAG, "Error Parse JSON results ");
            e.printStackTrace();
        }


        return null;
    }


}

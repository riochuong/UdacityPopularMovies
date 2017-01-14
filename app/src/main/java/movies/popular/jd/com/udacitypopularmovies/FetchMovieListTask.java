package movies.popular.jd.com.udacitypopularmovies;

import android.content.ContentValues;
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

import movies.popular.jd.com.udacitypopularmovies.data.MovieQueryJsonParser;

/**
 * Background task for fetching and parsing Movies data !!!
 */


public class FetchMovieListTask extends AsyncTask<String, Void, Void> {

    private static final String TAG = "FETCH_MOIE_TASK";

    private static final String API_KEY = BuildConfig.MOVIE_DB_API_KEY;

    private static final String BASE_QUERY = "http://api.themoviedb.org/3";

    private static final String TOP_RATED_MOVIE_BASE_QUERY = BASE_QUERY + "/movie/top_rated?";
    private static final String POPULAR_MOVIE_BASE_QUERY = BASE_QUERY + "/movie/popular?";

    private static final String POPULAR_CHOICE = "popular";

    private static final String TOP_RATED_CHOICE = "top_rated";


    @Override
    protected Void doInBackground(String... strings) {

        // make sure we have a choice for query
        if (strings.length <= 0) {
            return null;
        }

        String queryChoice = strings[0];
        String baseQuery = null;
        HttpURLConnection httpConnection = null;

        // figuring out which type of movies to be fetched ...
        if (queryChoice.equalsIgnoreCase(POPULAR_CHOICE)) {
            baseQuery = POPULAR_MOVIE_BASE_QUERY;
        } else if (queryChoice.equalsIgnoreCase(TOP_RATED_CHOICE)) {
            baseQuery = TOP_RATED_MOVIE_BASE_QUERY;
        } else {
            Log.e(TAG, "Wrong choice for querying data ");
            return null;
        }

        try {
            // need to add API_KEY before doing query
            Uri queryUri = Uri.parse(baseQuery).buildUpon()
                    .appendQueryParameter("api_key", API_KEY)
                    .build();

            URL url = new URL(queryUri.toString());
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.connect();
            String res = readIncomingData(httpConnection.getInputStream());

            // Get the list of CONTENT_VALUES back now insert
            if (res != null) {

                List<ContentValues> resCVList = MovieQueryJsonParser.parseMovieListResult(res);

                // performing insert to DBs
                if (resCVList.size() > 0){

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

    /**
     * Helper for reading data from input stream
     *
     * @param is
     * @return
     */
    private String readIncomingData(InputStream is) {

        StringBuffer sb = null;
        BufferedReader bfr = null;
        String line = null;

        try {
            if (is != null) {
                sb = new StringBuffer();
                bfr = new BufferedReader(new InputStreamReader(is));
                while ((line = bfr.readLine()) != null) {
                    // adding newline does not affect json format
                    // but help deubgging way easier
                    sb.append(line + "\n");
                }
                return sb.toString();
            }
        } catch (IOException e) {
            Log.e(TAG, "ERROR to read data from Input stream reader");
            e.printStackTrace();
        }

        return null;
    }

}

package movies.popular.jd.com.udacitypopularmovies;

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

import movies.popular.jd.com.udacitypopularmovies.data.MovieQueryJsonParser;

/**
 * Created by chuondao on 1/11/17.
 */

public class FetchMovieListTask extends AsyncTask<String, Void, Void> {

    private static final String TAG = "FETCH_MOIE_TASK";

    private static final String API_KEY = "c7824726e838dc3554c840e9edf2f331";

    private static final String BASE_QUERY = "http://api.themoviedb.org/3";

    private static final String TOP_RATED_MOVIE_BASE_QUERY = BASE_QUERY + "/movie/top_rated?";
    private static final String POPULAR_MOVIE_BASE_QUERY = BASE_QUERY + "/movie/popular?";

    private static final String POPULAR_CHOICE = "popular";

    private static final String TOP_RATED_CHOICE = "top_rated";

    private static final String SPECIFIC_MOVIE_TRAILERS = "trailers";
    private static final String SPECIFIC_MOVIE_REVIEWS = "reviews";

    @Override
    protected Void doInBackground(String... strings) {

        // make sure we have a choice for query
        if (strings.length <= 0) {
            return null;
        }

        String queryChoice = strings[0];
        String baseQuery = null;
        HttpURLConnection httpConnection = null;
        boolean specificMovieQuery = false;


        if (queryChoice.equalsIgnoreCase(POPULAR_CHOICE)) {
            baseQuery = POPULAR_MOVIE_BASE_QUERY;
        } else if (queryChoice.equalsIgnoreCase(TOP_RATED_CHOICE)) {
            baseQuery = TOP_RATED_MOVIE_BASE_QUERY;
        } else if (queryChoice.equalsIgnoreCase(SPECIFIC_MOVIE_TRAILERS)
                        || queryChoice.equalsIgnoreCase(SPECIFIC_MOVIE_REVIEWS)) {

            // check if Id is passed as the second args
            if (strings.length < 2){
                Log.e(TAG,"Need to pass the movie ID as second param");
                return null;
            }
            baseQuery = BASE_QUERY + "/movie/"+strings[1]+"/"+queryChoice.trim().toLowerCase();
            specificMovieQuery = true;
        }
        else{
            Log.e(TAG,"Wrong choice for querying data ");
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
            // for now just to see how the data looks like first for parsing
            if (res != null){
                Log.i(TAG,res);
                if (! specificMovieQuery) {
                    MovieQueryJsonParser.parseMovieListResult(res);
                }
//                else{
//                    MovieQueryJsonParser.parseJsonMoveiSpecific(res,queryChoice.trim().toLowerCase());
//                }
            }

        } catch (MalformedURLException e) {
            Log.e(TAG,"Error Malformed URL");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG,"Failed to Open http connection ");
            e.printStackTrace();
        } catch (JSONException e) {
            Log.e(TAG,"Error Parse JSON results ");
            e.printStackTrace();
        }


        return null;
    }

    /**
     * Helper for reading data from input stream
     * @param is
     * @return
     */
    private String readIncomingData(InputStream is){

        StringBuffer sb = null;
        BufferedReader bfr = null;
        String line = null;

        try {
            if (is != null){
                sb = new StringBuffer();
                bfr = new BufferedReader(new InputStreamReader(is));
                while ((line = bfr.readLine()) != null){
                    // adding newline does not affect json format
                    // but help deubgging way easier
                    sb.append(line+"\n");
                }
                return sb.toString();
            }
        } catch (IOException e) {
            Log.e(TAG,"ERROR to read data from Input stream reader");
            e.printStackTrace();
        }

        return null;
    }

}

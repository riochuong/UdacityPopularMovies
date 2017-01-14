package movies.popular.jd.com.udacitypopularmovies.tasks;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import movies.popular.jd.com.udacitypopularmovies.BuildConfig;

/**
 * Created by chuondao on 1/14/17.
 */

public class MovieTaskHelper {

    private static final String TAG = "MovieTaskHelper";

    public static final String API_KEY = BuildConfig.MOVIE_DB_API_KEY;

    public static final String BASE_QUERY = "http://api.themoviedb.org/3";

    public static final String TOP_RATED_MOVIE_BASE_QUERY = BASE_QUERY + "/movie/top_rated?";
    public static final String POPULAR_MOVIE_BASE_QUERY = BASE_QUERY + "/movie/popular?";

    public static final String POPULAR_CHOICE = "popular";

    public static final String TOP_RATED_CHOICE = "top_rated";

    /**
     * build movie URLS request for specific movie trailers
     *
     * @param movideId
     * @return
     */
    public static Uri buildMovieTrailersRequestUrl(String movideId) {
        String baseQuery = BASE_QUERY + "/" + movideId + "/trailers";
        return Uri.parse(baseQuery).buildUpon()
                .appendQueryParameter("api_key", API_KEY)
                .build();

    }

    /**
     * build movie URLS request for specific movie reviews
     *
     * @param movideId
     * @return
     */
    public static Uri buildMovieReviewsRequestUrl(String movideId) {
        String baseQuery = BASE_QUERY + "/" + movideId + "/reviews";
        return Uri.parse(baseQuery).buildUpon()
                .appendQueryParameter("api_key", API_KEY)
                .build();
    }

    /**
     * build URL to help request list of movie based on type
     * @param queryChoice
     * @return
     */
    public static Uri buildMovieListRequestUrl(String queryChoice){
        // figuring out which type of movies to be fetched ...
        String baseQuery;
        if (queryChoice.equalsIgnoreCase(POPULAR_CHOICE)) {
            baseQuery = POPULAR_MOVIE_BASE_QUERY;
        } else if (queryChoice.equalsIgnoreCase(TOP_RATED_CHOICE)) {
            baseQuery = TOP_RATED_MOVIE_BASE_QUERY;
        } else {

            return null;
        }
        // need to add API_KEY before doing query
        return Uri.parse(baseQuery).buildUpon()
                .appendQueryParameter("api_key", API_KEY)
                .build();
    }


    /**
     * Helper for making HTTP GET request
     * @param queryUri
     * @return
     * @throws IOException
     */
    public static String fetchHttpDataMovie(Uri queryUri) throws IOException {
        URL url = new URL(queryUri.toString());
        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
        httpConnection.setRequestMethod("GET");
        httpConnection.connect();
        return readIncomingData(httpConnection.getInputStream());
    }


    /**
     * Helper for reading data from input stream from HTTP connection
     * @param is
     * @return
     */
    private static String readIncomingData(InputStream is) {

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
        Log.e(TAG,"Failed to fetch data from Input stream reader");
        return null;
    }

}

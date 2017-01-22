package movies.popular.jd.com.udacitypopularmovies.tasks;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import movies.popular.jd.com.udacitypopularmovies.BuildConfig;
import movies.popular.jd.com.udacitypopularmovies.data.MovieContract;

/**
 * Created by chuondao on 1/14/17.
 */

public class MovieTaskHelper {

    private static final String TAG = "MovieTaskHelper";

    public static final String API_KEY = BuildConfig.MOVIE_DB_API_KEY;

    public static final String BASE_QUERY = "http://api.themoviedb.org/3";

    public static final String IMG_BASE_QUERY = "http://image.tmdb.org/t/p/w185";

    public static final String TOP_RATED_MOVIE_BASE_QUERY = BASE_QUERY + "/movie/top_rated?";
    public static final String POPULAR_MOVIE_BASE_QUERY = BASE_QUERY + "/movie/popular?";

    public static final String POPULAR_CHOICE = "popular";

    public static final String TOP_RATED_CHOICE = "top_rated";

    public static final String YOUTUBE_VIDEO_THUMBNAIL =
            "http://img.youtube.com/vi/%s/hqdefault.jpg";

    public static final String YOUTUBE_WATCH_URL_BASE =
            "http://www.youtube.com/watch?v=%s";

    public static final String MY_SHARED_PREFS = "my_shared_prefs";
    public static final String VIEW_CRITERIA = "view_criteria";
    // only use for two panes mode
    public static final String CURRENT_SELECTED_MOVIE = "current_selected_movie";

    public static final int FAVORITE_CRITERIA = 0;
    public static final int  POPULAR_CRITERIA = 1;
    public static final int HIGHEST_RATED_CRITERIA = 2;

    public  static final String TOP_RATED_STR = "top_rated";
    public  static final String  POPULAR_STR = "popular";


    /**
     * build movie URLS request for specific movie trailers
     *
     * @param movideId
     * @return
     */
    public static Uri buildMovieTrailersRequestUrl(String movideId) {
        String baseQuery = BASE_QUERY + "/movie" +"/"+ movideId + "/trailers";
        return Uri.parse(baseQuery).buildUpon()
                .appendQueryParameter("api_key", API_KEY)
                .build();

    }

    /**
     * build youtube intent for launching trailer
     * @param link
     * @return
     */
    public static Intent buildYoutubeIntend(String link){

        if (link == null){
            return null;
        }

        Intent launchYoutube = new Intent();
        launchYoutube.setAction(Intent.ACTION_VIEW);
        launchYoutube.setData(buildYoutubeWatchUrl(link));
        return launchYoutube;
    }

    /**
     * build Url show we can launch youtube app
     * @param link
     * @return
     */
    public static Uri buildYoutubeWatchUrl(String link){
        String baseQuery = String.format(YOUTUBE_WATCH_URL_BASE,link);
        return Uri.parse(baseQuery).buildUpon().build();
    }

    /**
     * Construct the url to request thumbnail image from youtube.
     * @param movieId
     * @return
     */
    public static Uri buildYoutubeThumbnailRequestUrl(String movieId){
        String baseQuery = String.format(YOUTUBE_VIDEO_THUMBNAIL, movieId);
        return Uri.parse(baseQuery).buildUpon().build();
    }

    /**
     * build icon with W185 so we can request image from picasso
     * @param path
     * @return
     */
    public static Uri buildMovieIconUrl(String path){
        String baseQuery = IMG_BASE_QUERY + "/" + path;
        return Uri.parse(baseQuery).buildUpon().appendQueryParameter("api_key",API_KEY)
                .build();
    }

    /**
     * build movie URLS request for specific movie reviews
     *
     * @param movideId
     * @return
     */
    public static Uri buildMovieReviewsRequestUrl(String movideId) {
        String baseQuery = BASE_QUERY + "/movie" +"/" + movideId + "/reviews";
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




    public static Bundle buildBundleForDetailActivity(Cursor cursor){

        Bundle bundle = new Bundle();


        bundle.putString(MovieContract.MovieEntry.COLUMN_TITLE,
                            MovieCursorHelper.getMovieNameFromCursor(cursor));

        bundle.putString(MovieContract.MovieEntry.COLUMN_MOVIE_ID,
                MovieCursorHelper.getMovieIdFromCursor(cursor));

        bundle.putString(MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
                MovieCursorHelper.getMovieReleaseDateFromCursor(cursor));

        bundle.putString(MovieContract.MovieEntry.COLUMN_POPULARITY,
                MovieCursorHelper.getMoviePopluarityFromCursor(cursor));

        bundle.putString(MovieContract.MovieEntry.COLUMN_VOTE_AVG,
                MovieCursorHelper.getMovieRatingFromCursor(cursor));

        bundle.putString(MovieContract.MovieEntry.COLUMN_POSTER_PATH,
                MovieCursorHelper.getMoviePosterPath(cursor));

        bundle.putString(MovieContract.MovieEntry.COLUMN_OVERVIEW,
                MovieCursorHelper.getMovieOverView(cursor));

        return bundle;
    }

}

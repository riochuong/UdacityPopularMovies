package movies.popular.jd.com.udacitypopularmovies.util;

import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import movies.popular.jd.com.udacitypopularmovies.data.MovieContract;

/**
 * Created by chuondao on 1/12/17.
 */

public class MovieQueryJsonParser {

    public static final String RESULTS = "results";

    // movies column match with Content_provider
    public static final String POSTER_PATH = "poster_path";
    public static final String ADULT = "adult";
    public static final String ID = "id";
    public static final String ORIGINAL_TITLE = "original_title";
    public static final String TITLE = "title";
    public static final String VIDEO = "video";
    public static final String VOTE_COUNT = "vote_count";
    public static final String VOTE_AVG = "vote_average";
    public static final String RELEASE_DATE = "release_date";
    public static final String OVERVIEW = "overview";
    public static final String POPULARITY = "popularity";


    public static final String YOUTUBE_TRAILERS = "youtube";
    public static final String SOURCE = "source";


    public static final String TRAILER_NAME = "name";
    public static final String TRAILER_SIZE = "size";
    public static final String TRAILER_SOURCE = "source";
    public static final String TRAILER_TYPE = "type";

    public static final String REVIEW_AUTHOR = "author";
    public static final String REVIEW_CONTENT = "content";
    public static final String REVIEW_ID = "id";

    /**
     * Parse movie list result and return the
     * Content values that match the MovieContract to be
     * inserted in to the DB
     *
     * @param res
     */
    public static List<ContentValues> parseMovieListResult(String res) throws JSONException {

        // values to be saved
        long id;
        String title;
        String origTitle;
        boolean adult;
        boolean video;
        boolean favor = false;
        double voteAvg;
        String posterPath;
        String overview;
        String releaseDate;
        double popularity;
        long voteCount;

        JSONObject rootObject = new JSONObject(res);
        JSONArray resultArray = rootObject.getJSONArray(RESULTS);
        int lenOfResults = resultArray.length();
        List<ContentValues> cVList = new ArrayList<>();

        for (int i = 0; i < lenOfResults; i++) {
            ContentValues newCv = new ContentValues();
            JSONObject result = resultArray.getJSONObject(i);
            id = result.getLong(ID);
            title = result.getString(TITLE);
            origTitle = result.getString(ORIGINAL_TITLE);
            adult = result.getBoolean(ADULT);
            video = result.getBoolean(VIDEO);
            voteAvg = result.getDouble(VOTE_AVG);
            posterPath = result.getString(POSTER_PATH);
            overview = result.getString(OVERVIEW);
            releaseDate = result.getString(RELEASE_DATE);
            popularity = result.getDouble(POPULARITY);
            voteCount = result.getLong(VOTE_COUNT);

            // put data in
            newCv.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, id);
            newCv.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
            newCv.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, origTitle);
            newCv.put(MovieContract.MovieEntry.COLUMN_ADULT, adult);
            newCv.put(MovieContract.MovieEntry.COLUMN_VIDEO, video);
            newCv.put(MovieContract.MovieEntry.COLUMN_VOTE_AVG, voteAvg);
            newCv.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, posterPath);
            newCv.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview);
            newCv.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
            newCv.put(MovieContract.MovieEntry.COLUMN_POPULARITY, popularity);
            newCv.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, voteCount);
            newCv.put(MovieContract.MovieEntry.COLUMN_FAVORITE, favor);
            cVList.add(newCv);
        }

        return cVList;

    }

    /**
     * Parse Json trailers list . Only care about youtube link for now
     * @param json
     * @param opt
     * @return
     */
    public static List<MovieTrailer>
                parseMovieTrailers(String json,String movideId) throws JSONException {
        String source;
        String name;
        String size;
        String type;

        List<MovieTrailer> movieTrailers = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        JSONObject jobject = new JSONObject(json);
        JSONArray jArr = jobject.getJSONArray(YOUTUBE_TRAILERS);


        // put all trailers youtube link to the return list
        for (int i = 0; i < jArr.length(); i++) {
            JSONObject jsonTrailer = jArr.getJSONObject(i);
            name = jsonTrailer.getString(TRAILER_NAME);
            size = jsonTrailer.getString(TRAILER_SIZE);
            source = jsonTrailer.getString(TRAILER_SOURCE);
            type = jsonTrailer.getString(TRAILER_TYPE);
            movieTrailers.add(new MovieTrailer(name,size,source,type, movideId));

        }
        return movieTrailers;

    }


    /**
     * Parse Json trailers list
     * @param json
     * @return
     */
    public static List<MovieReview>
        parseMovieReviews(String json, String movieId) throws JSONException {


        String content;
        String author;
        String id;

        List<MovieReview> movieReviews = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        JSONObject jobject = new JSONObject(json);
        JSONArray jArr = jobject.getJSONArray(YOUTUBE_TRAILERS);


        // put all trailers youtube link to the return list
        for (int i = 0; i < jArr.length(); i++) {
            JSONObject jsonTrailer = jArr.getJSONObject(i);
            author = jsonTrailer.getString(REVIEW_AUTHOR);
            content = jsonTrailer.getString(REVIEW_CONTENT);
            id = jsonTrailer.getString(REVIEW_ID);
            movieReviews.add(new MovieReview(author, content, id, movieId));

        }
        return movieReviews;

    }


}

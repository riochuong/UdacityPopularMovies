package movies.popular.jd.com.udacitypopularmovies.data;

import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

    public static final String SEPERATOR = "___,___";

    public static  final String MOVIE_SPEC_OPT_REVIEWS = "reviews";
    public static  final String MOVIE_SPEC_OPT_TRAILERS = "trailers";

    public  static final String YOUTUBE_TRAILERS = "youtube";
    public  static final String SOURCE = "source";

    /**
     * Parse movie list result and return the
     * Content values that match the MovieContract to be
     * inserted in to the DB
     * @param res
     */
    public static List<ContentValues> parseMovieListResult (String res) throws JSONException {

        // values to be saved
        long id;
        String title;
        String origTitle;
        boolean adult;
        boolean video;
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

        for (int i = 0; i <lenOfResults ; i++) {
            ContentValues newCv = new ContentValues();
            JSONObject result = resultArray.getJSONObject(i);
            id = result.getLong(ID);
            title = result.getString(TITLE);
            origTitle = result.getString(ORIGINAL_TITLE);
            adult = result.getBoolean(ADULT);
            video = result.getBoolean(VIDEO);
            voteAvg = result.getDouble(VOTE_AVG);
            posterPath = result.getString(POSTER_PATH);
            overview  = result.getString(OVERVIEW);
            releaseDate = result.getString(RELEASE_DATE);
            popularity = result.getDouble(POPULARITY);
            voteCount = result.getLong(VOTE_COUNT);

            // put data in
            newCv.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID,ID);
            newCv.put(MovieContract.MovieEntry.COLUMN_TITLE,TITLE);
            newCv.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE,ORIGINAL_TITLE);
            newCv.put(MovieContract.MovieEntry.COLUMN_ADULT,ADULT);
            newCv.put(MovieContract.MovieEntry.COLUMN_VIDEO,VIDEO);
            newCv.put(MovieContract.MovieEntry.COLUMN_VOTE_AVG,VOTE_AVG);
            newCv.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH,POSTER_PATH);
            newCv.put(MovieContract.MovieEntry.COLUMN_OVERVIEW,OVERVIEW);
            newCv.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE,RELEASE_DATE);
            newCv.put(MovieContract.MovieEntry.COLUMN_POPULARITY,POPULARITY);
            newCv.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT,VOTE_COUNT);
            cVList.add(newCv);
        }

        return cVList;

    }

    /**
     *
     * @param json
     * @param opt
     * @return
     */
    public static String parseJsonMoveiSpecific (String json, String opt) throws JSONException {

        if (opt.equalsIgnoreCase(MOVIE_SPEC_OPT_TRAILERS)){
            StringBuffer sb = new StringBuffer();
            JSONObject jobject = new JSONObject(json);
            JSONArray jArr = jobject.getJSONArray(YOUTUBE_TRAILERS);
            for (int i = 0; i < jArr.length(); i++) {
                sb.append(jArr.getJSONObject(i).getString(SOURCE));
                sb.append(SEPERATOR);
            }
            return sb.toString();
        }
        return null;
    }

//    /**
//     *
//     * @param json
//     * @return
//     */
//    public static String parseJsonTrailers (String json){
//
//    }

    /**
     * This helper to help split arr to string so we can store with comma separated
     * in the database.
     * @param strArr
     * @return
     */
    private static String convertArrayToString (String [] strArr){
        StringBuffer sb = null;
        if (strArr != null && strArr.length >0){
            sb = new StringBuffer();
            for (int i = 0; i < strArr.length ; i++) {
                sb.append(strArr[i]);
                sb.append(SEPERATOR);
            }
        }

        return sb.toString();
    }

    /**
     * helper to split the data stored from DB seperated by comma
     * @param str
     * @return
     */
    private static String [] convertStringToArrary(String str){
        return str.split(SEPERATOR);
    }



}

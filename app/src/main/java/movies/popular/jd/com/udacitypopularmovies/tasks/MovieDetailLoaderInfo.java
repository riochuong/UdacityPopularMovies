package movies.popular.jd.com.udacitypopularmovies.tasks;

import java.util.List;

import movies.popular.jd.com.udacitypopularmovies.util.MovieReview;
import movies.popular.jd.com.udacitypopularmovies.util.MovieTrailer;

/**
 * Created by chuondao on 1/15/17.
 */

public class MovieDetailLoaderInfo {
    private List<MovieReview> mReviewList;
    private List<MovieTrailer> mTrailerList;

    public MovieDetailLoaderInfo(List<MovieReview> mReviewList, List<MovieTrailer> mTrailerList) {
        this.mReviewList = mReviewList;
        this.mTrailerList = mTrailerList;

    }

    public List<MovieReview> getmReviewList() {
        return mReviewList;
    }

    public List<MovieTrailer> getmTrailerList() {
        return mTrailerList;
    }


}

package movies.popular.jd.com.udacitypopularmovies.util;

/**
 * This class represent an entity of movie reviews
 *
 */

public class MovieTrailer {


    private String mName;
    private  String mSize;
    private  String mSource;
    private  String mType;
    private  String mMovieId;

    public MovieTrailer(String mName, String mSize, String mSource, String mType, String mId) {
        this.mName = mName;
        this.mSize = mSize;
        this.mSource = mSource;
        this.mType = mType;
        this.mMovieId = mId;
    }


    public String getName() {
        return mName;
    }

    public String getSize() {
        return mSize;
    }

    public String getSource() {
        return mSource;
    }

    public String getType() {
        return mType;
    }

    public String getMovieId() {
        return mMovieId;
    }


}

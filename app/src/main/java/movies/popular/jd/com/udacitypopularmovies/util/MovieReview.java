package movies.popular.jd.com.udacitypopularmovies.util;

/**
 * This class respresnt a movie specific reviews
 */

public class MovieReview {

    private String movieId;
    private String author;
    private String content;
    private String id;

    public MovieReview(String author, String content, String id, String movieId) {
        this.author = author;
        this.content = content;
        this.id = id;
        this.movieId = movieId;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getId() {
        return id;
    }

    public String getMovieId() {
        return movieId;
    }
}

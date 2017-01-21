package movies.popular.jd.com.udacitypopularmovies.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import movies.popular.jd.com.udacitypopularmovies.R;
import movies.popular.jd.com.udacitypopularmovies.data.MovieContract;
import movies.popular.jd.com.udacitypopularmovies.tasks.MovieTaskHelper;
import movies.popular.jd.com.udacitypopularmovies.util.MovieReview;
import movies.popular.jd.com.udacitypopularmovies.util.MovieTrailer;

/**
 * Created by chuondao on 1/19/17.
 */

public class MovieDetailsRecylerAdapter extends RecyclerView.Adapter {

    private List<MovieReview> mMovieReviewList;
    private static final  String TAG = "MOVIE_RECYCLER_ADAPTER";
    private List<MovieTrailer> mMovieTrailerList;
    private String mOverview;
    private Context mContext;
    private int mReviewHeaderPosition = -1;
    private static final int MOVIE_INFO_POSITION = 0;
    private static final int MOVIE_OVERVIEW_POSITION = 1;
    private static final int MOVIE_TRAILER_HEADER_POSITION = 2;
    private static final int NUMBER_OF_STATIC_VIEWS = 4;
    private Bundle mMovieInfo;

    public MovieDetailsRecylerAdapter(Context ctx,
                                      List<MovieReview> movieReviewList,
                                      List<MovieTrailer> movieTrailerList,
                                      Bundle info) {

        this.mMovieTrailerList = movieTrailerList;
        this.mMovieReviewList = movieReviewList;
        this.mContext = ctx;
        mMovieInfo = info;
        // to the next available spot
        mReviewHeaderPosition = MOVIE_TRAILER_HEADER_POSITION +
                ((movieTrailerList == null) ? 1 : (movieTrailerList.size() + 1));


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;

        if (viewType < 0 || viewType > getItemCount()){
            Log.e(TAG, "Error inflate view here ");
            return null;
        }

        switch (viewType) {
            case MOVIE_INFO_POSITION:
                v = LayoutInflater.from(mContext)
                        .inflate(R.layout.movie_detail_header_layout, parent, false);
                return new MovieHeaderInfoHolder(v);
            case MOVIE_OVERVIEW_POSITION:
                v = LayoutInflater.from(mContext)
                        .inflate(R.layout.movie_overview_layout, parent, false);
                return new MovieOverviewHolder(v);
            case MOVIE_TRAILER_HEADER_POSITION:
                v = LayoutInflater.from(mContext)
                        .inflate(R.layout.movie_trailer_header, parent, false);
                return new EmptyViewHolder(v);
            default:
                // in here we have to do some logic to inflate the correct
                // view which
                // REVIEW HEADER
                if (viewType == mReviewHeaderPosition) {
                    v = LayoutInflater.from(mContext)
                            .inflate(R.layout.movie_review_header, parent, false);
                    return new EmptyViewHolder(v);
                }

                // inflate review
                if (viewType > mReviewHeaderPosition
                        && mMovieReviewList != null){
                    v = LayoutInflater.from(mContext).
                            inflate(R.layout.review_item_layout,parent,false);
                    return new MovieReviewViewHolder(v);
                }

                // inflate movie trailers
                v = LayoutInflater.from(mContext)
                        .inflate(R.layout.movie_trailer_item_layout, parent, false);
                // need to know trailer index so we can get the trailer link
                // in order to set the onClickListener
                int trailIndex = viewType - (MOVIE_TRAILER_HEADER_POSITION + 1);
                return new MovieTrailerHolder(v, mMovieTrailerList.get(trailIndex).getSource());
        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (position) {
            case MOVIE_INFO_POSITION:
                setMovieHeaderDataToView((MovieHeaderInfoHolder)holder);
                break;
            case MOVIE_OVERVIEW_POSITION:
                ((MovieOverviewHolder)holder).mContent.setText(
                        mMovieInfo.getString(MovieContract.MovieEntry.COLUMN_OVERVIEW));
                break;
            case MOVIE_TRAILER_HEADER_POSITION:
                break;
            default:
                // in here we have to do some logic to inflate the correct
                // view which
                // REVIEW HEADER
                if (position == mReviewHeaderPosition) {
                   return;
                }

                // inflate review
                if (position > mReviewHeaderPosition){
                    // first index should be right after the header
                    int revInd = position - (mReviewHeaderPosition + 1);
                    if (mMovieReviewList != null &&
                            revInd < mMovieReviewList.size()){
                        MovieReview mvRev = mMovieReviewList.get(revInd);
                        ((MovieReviewViewHolder)holder).mAuthor.setText(mvRev.getAuthor());
                        ((MovieReviewViewHolder)holder).mContent.setText(mvRev.getContent());
                    }
                }
                else {
                    // inflate movie trailers
                    int trailInd = position - (MOVIE_TRAILER_HEADER_POSITION + 1);
                    if (mMovieTrailerList != null
                            && (trailInd < mMovieTrailerList.size()))
                    {
                        ((MovieTrailerHolder) holder).mTrailerTitle
                                .setText(mMovieTrailerList.get(trailInd).getName());
                    }
                }
        }
    }


    /**
     * should only be called after view inflatted
     */
    private void setMovieHeaderDataToView (MovieHeaderInfoHolder vh){

        // do a check in case this is the first launch
        if (mMovieInfo != null){
            vh.mMovieName.setText(mMovieInfo.getString(MovieContract.MovieEntry.COLUMN_TITLE));
            vh.mDateRelease.setText(mMovieInfo.
                    getString(MovieContract.MovieEntry.COLUMN_RELEASE_DATE));
            vh.mMovieRating.setText(mMovieInfo.getString(MovieContract.MovieEntry.COLUMN_VOTE_AVG));
            Uri imageRequestUri = MovieTaskHelper
                    .buildMovieIconUrl(
                            mMovieInfo.getString(MovieContract.MovieEntry.COLUMN_POSTER_PATH));

            Picasso.with(mContext).load(imageRequestUri).into(vh.mMovieImage);
        }

    }

    @Override
    public int getItemCount() {
        // item count equals all views + number of trailers + number of reviews
        int totalItem = (mMovieReviewList != null) ? mMovieReviewList.size() : 0;
        totalItem += (mMovieTrailerList != null) ? mMovieTrailerList.size() : 0;
        return (totalItem + NUMBER_OF_STATIC_VIEWS);
    }


    @Override
    public int getItemViewType(int position) {

        return position;
    }

    /**
     * View holders  for each type of view
     */
    private class MovieReviewViewHolder extends RecyclerView.ViewHolder {
        TextView mAuthor;
        TextView mContent;

        public MovieReviewViewHolder(View itemView) {
            super(itemView);
            mAuthor = (TextView) itemView.findViewById(R.id.review_author);
            mContent = (TextView) itemView.findViewById(R.id.review_content);
        }
    }

    /**
     * View holders  for movie overview
     */
    private class MovieOverviewHolder extends RecyclerView.ViewHolder {
        TextView mContent;

        public MovieOverviewHolder(View itemView) {
            super(itemView);
            mContent = (TextView) itemView.findViewById(R.id.movie_overview_content);
        }
    }


    /**
     * View holders  for movie trailer
     */
    private class MovieTrailerHolder extends RecyclerView.ViewHolder {
        TextView mTrailerTitle;

        public MovieTrailerHolder(View itemView, final String source) {
            super(itemView);
            mTrailerTitle = (TextView) itemView.findViewById(R.id.trailer_title);
            // set on click listener to play youtube videos.
            itemView.setClickable(true);
            itemView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                Intent intent = MovieTaskHelper.buildYoutubeIntend(source);
                                mContext.startActivity(intent);
                        }
                    }
            );
        }
    }

    /*
     * View holders  for each type of view
     */
    class MovieHeaderInfoHolder extends RecyclerView.ViewHolder {
        ImageView mMovieImage;
        TextView mMovieName;
        TextView mDateRelease;
        TextView mMovieRating;

        public MovieHeaderInfoHolder(View itemView) {
            super(itemView);
            //ButterKnife.bind(itemView);
            mMovieImage = (ImageView) itemView.findViewById(R.id.movie_poster_img);
            mMovieName = (TextView) itemView.findViewById(R.id.movie_header_name);
            mDateRelease = (TextView) itemView.findViewById(R.id.date_release);
            mMovieRating = (TextView) itemView.findViewById(R.id.movie_rating);
        }
    }

    private class EmptyViewHolder extends RecyclerView.ViewHolder {

        public EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }
}

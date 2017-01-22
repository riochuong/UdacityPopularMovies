package movies.popular.jd.com.udacitypopularmovies.ui;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObservable;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;

import movies.popular.jd.com.udacitypopularmovies.MainActivity;
import movies.popular.jd.com.udacitypopularmovies.R;
import movies.popular.jd.com.udacitypopularmovies.data.MovieContract;
import movies.popular.jd.com.udacitypopularmovies.tasks.MovieCursorHelper;
import movies.popular.jd.com.udacitypopularmovies.tasks.MovieTaskHelper;
import movies.popular.jd.com.udacitypopularmovies.util.SharedPreferenceHelper;

/**
 * movie list Recycler adapter
 * borrow the implementation idea from the gist below:
 * https://gist.github.com/skyfishjy/443b7448f59be978bc59
 */

public class MovieCursorRecyclerAdapter extends
        RecyclerView.Adapter<MovieCursorRecyclerAdapter.MovieItemViewHolder> {

    private Cursor mCursor;
    private boolean mDataValid;
    private Context mContext;
    private int mSortCriteria;
    // important to have the data oberver incase the
    // cursor data is changed outside of the app.
    private DataChangeObserver mDataChangeObserver;

    // to avoid keep querying db for fav. movies display
    // and to keep consistent data ..

    public MovieCursorRecyclerAdapter(Context ctx, Cursor cursor) {
        mContext = ctx;
        mCursor = cursor;
        mDataChangeObserver = new DataChangeObserver();
        // register data change observer for invalid data notification
        if (cursor != null) {
            cursor.registerDataSetObserver(mDataChangeObserver);
        }
        mSortCriteria = SharedPreferenceHelper.getViewCriteriaFromPref(mContext);

    }


    @Override
    public MovieItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View movieItemView =
                LayoutInflater.from(mContext).inflate(R.layout.movie_list_item_layout, parent, false);
        View movieDetailFragment = parent.findViewById(R.id.movie_detail_fragment);
       // movieItemView.setLayoutParams(getRecylcerViewLayoutParams(movieDetailFragment));
        return new MovieItemViewHolder(movieItemView);

    }

    @Override
    public void onBindViewHolder(MovieItemViewHolder holder, int position) {
        if (!mDataValid)
            throw new IllegalStateException("Cannot bind invalid data to view ");
        // move cursor to the specified position and do sth
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException
                    ("Could not move cursor to the specifed position: " + position);
        }
        onBindViewHolder(holder, mCursor, position);
    }


    public void onBindViewHolder(MovieItemViewHolder holder, Cursor cursor, int position) {
        // bind data
        String movieId = cursor.getString(
            cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID)
        );
        // set item view on Click listener
        holder.itemView.setOnClickListener(new MovieDetailOnClickListener(
                MovieTaskHelper.buildBundleForDetailActivity(cursor)));


        holder.mMovieName.setText(
                MovieCursorHelper.getMovieNameFromCursor(cursor));
        holder.mRating.setText(
                MovieCursorHelper.getMovieRatingFromCursor(cursor));

        // set correct favorite listener when user click on the star icon
        int isFav = MovieCursorHelper.getMovieFavorField(mCursor);
        holder.setFavBtn(isFav > 0);

        //holder.mFavStar.setOnClickListener(new OnFavSelectListener(movieId, isFav, holder));
        // get poster path to prepare for loading image
        String posterPath = MovieCursorHelper.getMoviePosterPath(cursor);

        // use picasso to load image to image view
        Picasso.with(mContext).
                load(MovieTaskHelper.buildMovieIconUrl(posterPath))
                .into(holder.mImgView);
    }




    /**
     * each movie view will have its own listener
     * so it can launch the movie detail view when needed
     */
    private class MovieDetailOnClickListener implements View.OnClickListener{

        Bundle mInfoBundle;
        int position;

        public MovieDetailOnClickListener(Bundle mInfoBundle) {

            this.mInfoBundle = mInfoBundle;
        }

        @Override
        public void onClick(View view) {
            ((MainActivity)mContext).startMovieDetailView(mInfoBundle);
        }
    }


    /**
     * swap new cursor in and return the old cursor
     *
     * @param newCursor
     */
    public void swapCursor(Cursor newCursor) {

        if (newCursor == mCursor) {
            return;
        }

        if (mCursor != null) {
            mCursor.unregisterDataSetObserver(mDataChangeObserver);
            mCursor.close();
        }

        if (newCursor != null) {
            mCursor = newCursor;
            mCursor.setNotificationUri(mContext.getContentResolver(),
                    MovieContract.MovieEntry.CONTENT_URI);
            mCursor.registerDataSetObserver(mDataChangeObserver);
            mDataValid = true;
            // initialize fav movies hashmap here
            notifyDataSetChanged();
        } else {
            mDataValid = false;
            mCursor = null;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        if (mCursor != null && mDataValid) {
            return mCursor.getCount();
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        if (mDataValid && mCursor != null) {
            mCursor.moveToPosition(position);
            // movieID should be unique for each movie
            int movieIdColIdx = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
            return Long.parseLong(mCursor.getString(movieIdColIdx));
        }
        return 0;
    }

    /**
     * inner class for quick reference of view
     */
    public static class MovieItemViewHolder extends RecyclerView.ViewHolder {

        ImageView mImgView;
        TextView mMovieName;
        TextView mRating;
        ImageButton mFavStar;

        public MovieItemViewHolder(View itemView) {
            super(itemView);
            mImgView = (ImageView) itemView.findViewById(R.id.movie_image);
            mMovieName = (TextView) itemView.findViewById(R.id.movie_name);
            mRating = (TextView) itemView.findViewById(R.id.movie_rating);
            mFavStar = (ImageButton) itemView.findViewById(R.id.fav_star);
        }

        /**
         * help to change button img based on fav or not
         * @param isFav
         */
        public void setFavBtn(boolean isFav){
            if (isFav){
                mFavStar.setImageResource(R.drawable.heart_fav);
                mFavStar.setTag(R.drawable.heart_fav);
            }
            else{
                mFavStar.setImageResource(R.drawable.heart_non_fav);
                mFavStar.setTag(R.drawable.heart_non_fav);
            }
        }

        public boolean isFavSelected (){
            return (mFavStar.getTag() != null) && ((int)mFavStar.getTag() == R.drawable.heart_fav);
        }
    }


    /**
     * implement data set observer to notify when data set is not valid
     */
    private class DataChangeObserver extends DataSetObserver {

        @Override
        public void onChanged() {
            super.onChanged();
            mDataValid = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            mDataValid = false;
            notifyDataSetChanged();
        }
    }


}

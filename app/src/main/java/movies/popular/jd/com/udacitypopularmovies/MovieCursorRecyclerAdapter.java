package movies.popular.jd.com.udacitypopularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import movies.popular.jd.com.udacitypopularmovies.data.MovieContract;
import movies.popular.jd.com.udacitypopularmovies.tasks.MovieTaskHelper;

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
    // important to have the data oberver incase the
    // cursor data is changed outside of the app.
    private DataChangeObserver mDataChangeObserver;

    public MovieCursorRecyclerAdapter(Context ctx, Cursor cursor) {
        mContext = ctx;
        mCursor = cursor;
        mDataChangeObserver = new DataChangeObserver();
        // register data change observer for invalid data notification
        if (cursor != null) {
            cursor.registerDataSetObserver(mDataChangeObserver);
        }
    }

    @Override
    public MovieItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View movieItemView =
                LayoutInflater.from(mContext).inflate(R.layout.list_item_layout, parent, false);

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
        onBindViewHolder(holder, mCursor);
    }


    public void onBindViewHolder(MovieItemViewHolder holder, Cursor cursor) {
        // bind data
        String movieId = cursor.getString(
            cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID)
        );
        holder.mMovieName.setText(
                cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)));
        holder.mPopular.setText(
                cursor.getDouble(
                        cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POPULARITY)) + "");
        holder.mRating.setText(
                cursor.getDouble(
                        cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)) + "");

        // set correct favorite listener when user click on the star icon
        boolean isFav = cursor.getInt(
                cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_FAVORITE)) > 0;
        holder.mFavStar.setChecked(isFav);
        holder.mFavStar.setOnClickListener(new OnFavSelectListener(movieId,isFav));

        String posterPath = cursor.getString(
            cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH)
        );

        // use picasso to load image to image view
        Picasso.with(mContext).
                load(MovieTaskHelper.buildMovieIconUrl(posterPath))
                .into(holder.mImgView);
    }

    /**
     * helper to implement change to data set when
     */
    private class OnFavSelectListener implements View.OnClickListener {
        private String mMovideId;
        private boolean mChecked;
        public OnFavSelectListener(String movieId, boolean checked) {
            mMovideId = movieId;
            mChecked = checked;
        }

        @Override
        public void onClick(View view) {
            boolean isChecked = ((CheckBox)view).isChecked();
            if (isChecked != mChecked){
                ContentValues cv = new ContentValues();
                cv.put(MovieContract.MovieEntry.COLUMN_FAVORITE, (isChecked ? 1 : 0));
                MovieCursorRecyclerAdapter.this.mContext.getContentResolver()
                        .update(
                                MovieContract.MovieEntry.buildMovieFavUpdateUri(mMovideId),
                                cv,
                                MovieContract.MovieEntry.COLUMN_MOVIE_ID + "= ?",
                                new String[]{mMovideId}
                        );
                mChecked = isChecked;
            }

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
            mCursor.registerDataSetObserver(mDataChangeObserver);
            mDataValid = true;
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
        TextView mPopular;
        CheckBox mFavStar;

        public MovieItemViewHolder(View itemView) {
            super(itemView);
            mImgView = (ImageView) itemView.findViewById(R.id.movie_image);
            mMovieName = (TextView) itemView.findViewById(R.id.movie_name);
            mRating = (TextView) itemView.findViewById(R.id.movie_rating);
            mPopular = (TextView) itemView.findViewById(R.id.movie_popular);
            mFavStar = (CheckBox) itemView.findViewById(R.id.fav_star);
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

package movies.popular.jd.com.udacitypopularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import movies.popular.jd.com.udacitypopularmovies.data.MovieContract;
import movies.popular.jd.com.udacitypopularmovies.tasks.FetchFavMovieOfflineModeLoaderTask;
import movies.popular.jd.com.udacitypopularmovies.tasks.FetchMovieDetailsOnlineModeLoaderTask;
import movies.popular.jd.com.udacitypopularmovies.tasks.MovieCursorHelper;
import movies.popular.jd.com.udacitypopularmovies.tasks.MovieDetailLoaderInfo;
import movies.popular.jd.com.udacitypopularmovies.tasks.MovieTaskHelper;
import movies.popular.jd.com.udacitypopularmovies.tasks.UpdateMovieFavAsyncTask;
import movies.popular.jd.com.udacitypopularmovies.ui.MovieDetailsRecylerAdapter;
import movies.popular.jd.com.udacitypopularmovies.util.MovieTrailer;
import movies.popular.jd.com.udacitypopularmovies.util.NetworkHelper;
import movies.popular.jd.com.udacitypopularmovies.util.StorageHelper;


/**
 * Display movie details in new fragment or separate new activity
 */
public class MovieDetailFragment extends Fragment {

    @BindView(R.id.all_views_recycler_view)
    RecyclerView mHomeView;
    @BindView(R.id.trailer_img) ImageView mTrailerImg;
    @BindView(R.id.play_trailer_button)
    ImageView mPlayTrailerBtn;
    @BindView(R.id.fav_fab)
    FloatingActionButton mFavBtn;
    @BindView(R.id.detail_view_main_display) View mMainDisplay;
    @BindView(R.id.movie_detail_empty_view) View mEmptyTextView;
    private static final int MOVIE_DETAIL_LOADER = 1;
    private static final int MOVIE_DETAIL_FAV_LOADER = 2;
    private static final String TAG = "MovieDetailFragment";
    LoaderManager mLoaderManager;
    Pair<String,String> mReviewTrailerGson = null;
    public MovieDetailFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_movie_detail_upgrade, container, false);
        ButterKnife.bind(this, v);

        LinearLayoutManager allViewLayout = new LinearLayoutManager(
                getContext(),
                LinearLayoutManager.VERTICAL, false);

        mHomeView.setLayoutManager(allViewLayout);
        mTrailerImg.setDrawingCacheEnabled(true);
        mLoaderManager = getLoaderManager();

        // if we are in offline mode then only fetch from db if movie is favorite movies.....
        if (NetworkHelper.isConnectToInternet(getContext())
            || isBundleSaidMovieFav())
        {
            initLoader();
        }
        else {
            // set empty display
            setEmptyDisplay();
        }
        return v;
    }

    /**
     * set empty view in the case that
     * network is not connected and movie is not favorite
     */
    private void setEmptyDisplay(){
        if (mMainDisplay != null){
           mMainDisplay.setVisibility(View.INVISIBLE);
        }
        if (mEmptyTextView != null){
            mEmptyTextView.setVisibility(View.VISIBLE);
        }

    }

    /**
     * force init loader
     */
    private void initLoader() {
        if (getMovieIdFromAgrs() != null) {

            mLoaderManager
                    .restartLoader(MOVIE_DETAIL_LOADER, null,
                            new MovieDetailsLoaderCallBacks())
                    .forceLoad();


            mLoaderManager
                    .restartLoader(MOVIE_DETAIL_FAV_LOADER, null,
                            new MovieFavCursorLoaderCallBacks())
                    .forceLoad();
        }
    }


    /**
     * initialize state for favorite float button
     */
    private void initializeFavButton(boolean ismFavInit) {

        int imgId =
                ismFavInit ? R.drawable.heart_fav : R.drawable.heart_non_fav;

        mFavBtn.setImageResource(imgId);
        mFavBtn.setTag(imgId);

        // set on Click listener for Fav button
        mFavBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final ContentValues cv = new ContentValues();
                        final String movieId = getMovieIdFromAgrs();
                        int res = 0;
                        // check to toggle the button and change the database
                        if (isFavSelected()) {
                            mFavBtn.setImageResource(R.drawable.heart_non_fav);
                            mFavBtn.setTag(R.drawable.heart_non_fav);
                        } else {
                            mFavBtn.setImageResource(R.drawable.heart_fav);
                            mFavBtn.setTag(R.drawable.heart_fav);
                            res = 1;
                        }

                        Uri uri = MovieContract.MovieEntry
                                .buildMovieFavUpdateUri(getMovieIdFromAgrs());
                        // this mean fav selected
                        if (res > 0) {
                            Log.d(TAG, "Store images locally");
                            storeDisplayAndTrailerImgs(movieId);
                        }
                        // TODO: GSON to store reviews and comments
                        if (mReviewTrailerGson != null){
                            cv.put(MovieContract.MovieEntry.COLUMN_TRAILER,
                                    mReviewTrailerGson.first);
                            cv.put(MovieContract.MovieEntry.COLUMN_REVIEW,
                                    mReviewTrailerGson.second);
                        }


                        // should we put this one on a thread
                        cv.put(MovieContract.MovieEntry.COLUMN_FAVORITE, res);

                        // call update task to update the db in background
                        new UpdateMovieFavAsyncTask(
                                getContext(), uri, cv
                        ).execute();

                    }
                }
        );
    }

    /**
     * store movie img and trailer images to internal storage
     *
     * @param movieId
     */
    private void storeDisplayAndTrailerImgs(String movieId) {
        // let's store display first
        String dispImgPath = StorageHelper.getPathToStoreFavImg(movieId, getContext(),
                StorageHelper.DISP_IMG);
        if (dispImgPath != null) {
            ImageView imgView =
                    (ImageView) getView().findViewById(R.id.movie_poster_img);
            if (imgView != null) {
                Bitmap drawable = imgView.getDrawingCache(true);
                StorageHelper.storeBitmapToInternal(dispImgPath, drawable);
            }
        }

        // now store trailer img
        String trailerImgPath = StorageHelper.
                getPathToStoreFavImg(movieId, getContext(), StorageHelper.TRAILER_IMG);
        // if path is null means the file exists and wwe dont have to do anything
        if (trailerImgPath != null) {
            ImageView imgView = (ImageView) getView().findViewById(R.id.trailer_img);
            if (imgView != null) {
                Bitmap drawable = imgView.getDrawingCache(true);
                StorageHelper.storeBitmapToInternal(trailerImgPath, drawable);
            }
        }
    }


//    /**
//     * need synchronized method to avoid crash
//     */
//    private   void notifyDatasetChanged(){
//
//    }

    /**
     * /**
     * set correct image source
     */
    private boolean isFavSelected() {

        int imgId = (int) mFavBtn.getTag();

        if (imgId == R.drawable.heart_fav) {
            return true;
        }

        return false;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    /**
     * helper to retreive movie id
     *
     * @return
     */
    private String getMovieIdFromAgrs() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            return bundle.getString(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
        }
        return null;
    }


    /**
     * helper to retreive movie id
     *
     * @return
     */
    private boolean isBundleSaidMovieFav() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            boolean val = bundle.getBoolean(MovieContract.MovieEntry.COLUMN_FAVORITE);
            return val;
        }
        return false;
    }


    /**
     *
     */
    private class MovieDetailsLoaderCallBacks
            implements LoaderManager.LoaderCallbacks<MovieDetailLoaderInfo> {


        @Override
        public Loader<MovieDetailLoaderInfo> onCreateLoader(int id, Bundle args) {
            // if movie passed in is favorite and no network connection
            // we might have to load everything from the db !
            if (isBundleSaidMovieFav() &&
                    (! NetworkHelper.isConnectToInternet(getContext()))){
                return new FetchFavMovieOfflineModeLoaderTask(getContext(), getMovieIdFromAgrs());
            }
            // esle we fetch movie specific data from network
            return new FetchMovieDetailsOnlineModeLoaderTask(getContext(), getMovieIdFromAgrs());
        }

        @Override
        public void onLoadFinished(Loader<MovieDetailLoaderInfo> loader, MovieDetailLoaderInfo data) {

            // now we can bind the data
            if (data != null) {
                mHomeView.setAdapter(
                        new MovieDetailsRecylerAdapter(getContext(),
                                data.getmReviewList(),
                                data.getmTrailerList(), MovieDetailFragment.this.getArguments()));

                // serialize data just in case this one is selected as favorite
                mReviewTrailerGson = StorageHelper.serializeMovieInfo(data);

                // official trailer most of the time is at the first position
                MovieTrailer officialTrailer = (data.getmTrailerList() != null
                                                    && data.getmTrailerList().size()  > 0 ) ?
                        data.getmTrailerList().get(0) : null;
                setMovieTrailerImg(officialTrailer);
            }

        }

        @Override
        public void onLoaderReset(Loader<MovieDetailLoaderInfo> loader) {
            mHomeView.setAdapter(null);
        }
    }

    /**
     * set the thumbnail image for movie trailer
     */
    private void setMovieTrailerImg(final MovieTrailer officialTrailer) {
        // use picasso to load image to view
        if (officialTrailer != null) {

            // if we have network then fetch from online
            if (NetworkHelper.isConnectToInternet(getContext())){
                Picasso.with(getContext()).
                        load(MovieTaskHelper
                                .buildYoutubeThumbnailRequestUrl(officialTrailer.getSource()))
                        .into(mTrailerImg);

            } else if (isBundleSaidMovieFav()){

                // else load fav movie img from internal storage
                File trailerFile = StorageHelper.getPathToLoadFavImg(
                        getMovieIdFromAgrs(),getContext(),StorageHelper.TRAILER_IMG
                );
                Picasso.with(getContext()).
                        load(trailerFile)
                        .into(mTrailerImg);
            } else{
                // do nothing here
                return ;
            }


            // set only click listener to launch youtube
            mPlayTrailerBtn.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // build and launch trailer with youtube.
                            Intent launchYoutube = MovieTaskHelper
                                    .buildYoutubeIntend(officialTrailer.getSource());
                            getContext().startActivity(launchYoutube);
                        }
                    }
            );
        }
    }

    private class MovieFavCursorLoaderCallBacks implements LoaderManager.LoaderCallbacks<Cursor> {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Uri uri =
                    MovieContract.MovieEntry.buildMovieListUri(
                            Long.parseLong(getMovieIdFromAgrs()));


            return new CursorLoader(
                    getContext(),
                    uri,
                    new String[]{MovieContract.MovieEntry.COLUMN_FAVORITE},
                    null,
                    null,
                    null
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            cursor.moveToFirst();
            // register to get notify when data changed
            cursor.setNotificationUri(
                    getContext().getContentResolver(),
                    MovieContract.MovieEntry.buildMovieListUri(
                            Long.parseLong(getMovieIdFromAgrs())
                    )
            );
            boolean isFav = (MovieCursorHelper.getMovieFavorField(cursor) > 0);
            initializeFavButton(isFav);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }
}

package movies.popular.jd.com.udacitypopularmovies;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import movies.popular.jd.com.udacitypopularmovies.data.MovieContract;
import movies.popular.jd.com.udacitypopularmovies.tasks.FetchMovieListTask;
import movies.popular.jd.com.udacitypopularmovies.tasks.MovieTaskHelper;
import movies.popular.jd.com.udacitypopularmovies.ui.MovieCursorRecyclerAdapter;
import movies.popular.jd.com.udacitypopularmovies.util.NetworkHelper;
import movies.popular.jd.com.udacitypopularmovies.util.SharedPreferenceHelper;

import static android.content.ContentValues.TAG;
import static movies.popular.jd.com.udacitypopularmovies.tasks.MovieTaskHelper.FAVORITE_CRITERIA;
import static movies.popular.jd.com.udacitypopularmovies.tasks.MovieTaskHelper.HIGHEST_RATED_CRITERIA;
import static movies.popular.jd.com.udacitypopularmovies.tasks.MovieTaskHelper.POPULAR_CRITERIA;
import static movies.popular.jd.com.udacitypopularmovies.tasks.MovieTaskHelper.VIEW_CRITERIA;


/**
 *
 */
public class MovieListFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int MOVIE_LIST_FRAGMENT_LOADER = 0;
    GridLayoutManager mLayoutManager = null;
    MovieCursorRecyclerAdapter mAdapter = null;
    Cursor mCursor;


    // TODO: Rename and change types and number of parameters
    public static MovieListFragment newInstance(String param1, String param2) {
        MovieListFragment fragment = new MovieListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);

        // calculate number of columns should be fitted
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // accomodate deprecated API

                            if (Build.VERSION.SDK_INT < 16) {
                                rootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                            } else {
                                rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            }

                            int col = getCollumnSpanSize(rootView);
                            setupRecyclerView(rootView, col);
                             initLoader();



                    }
                });
        return rootView;
    }

    /**
     * force loader to reload data
     */
    public void onSelectionChange() {
        Bundle bundle = new Bundle();
        // get the selection from shared prefs
        bundle.putInt(VIEW_CRITERIA,
                SharedPreferenceHelper.getViewCriteriaFromPref(getContext()));
        // force data reload here ...if fails ....set empty view instead
        forceReloadData();

    }

    private void initLoader() {
        Bundle bundle = new Bundle();
        int choice = SharedPreferenceHelper.getViewCriteriaFromPref(getContext());
        bundle.putInt(VIEW_CRITERIA, choice);
        getLoaderManager().initLoader(MOVIE_LIST_FRAGMENT_LOADER,bundle,this);
    }


    /**
     * Helper used to setup recycler view
     *
     * @param rootLayout
     */
    private void setupRecyclerView(View rootLayout, int colSpan) {

        RecyclerView recView = (RecyclerView) rootLayout.findViewById(R.id.movie_list_recycler_view);
        mAdapter = new MovieCursorRecyclerAdapter(this.getContext(), mCursor);
        mLayoutManager = new GridLayoutManager(getContext(), colSpan);
        recView.setLayoutManager(mLayoutManager);

        recView.setHasFixedSize(true);
        recView.setAdapter(mAdapter);
    }

    private int getCollumnSpanSize(View rootView) {
        // doing some math here to calculate span
        WindowManager wm = (WindowManager)
                getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        double density = metrics.density;
        double widthDp = rootView.getWidth() / density;

        double cellWidth =
                getContext().getResources().getDimension(R.dimen.card_view_width) / density;

        return (int) Math.round(widthDp / cellWidth);

    }


    /**
     * this called from MovieListActivity to change the
     * display movies on the main activity
     *
     * @return :
     * false : if and only if network is not available
     */
    private void forceReloadData() {
        int choice = SharedPreferenceHelper.getViewCriteriaFromPref(getContext());
        Bundle bundle = new Bundle();
        bundle.putInt(VIEW_CRITERIA, choice);
        getLoaderManager().restartLoader(MOVIE_LIST_FRAGMENT_LOADER, bundle, this);

    }

    /**
     * force refetch data and update the DB
     */
    private void foreReFetchDataFromNetwork(){
            int choice = SharedPreferenceHelper.getViewCriteriaFromPref(getContext());
            Log.e(TAG, "Network connected refetch from Internet");
            String task = (choice == HIGHEST_RATED_CRITERIA) ?
                    MovieTaskHelper.TOP_RATED_STR : MovieTaskHelper.POPULAR_STR;
            FetchMovieListTask fetchTask = new FetchMovieListTask(getContext());
            fetchTask.execute(task);
    }




    //************ LOADER CALL BACKS ******************************
    @Override
    public Loader onCreateLoader(int id, Bundle args) {

        int choice = args.getInt(VIEW_CRITERIA);
        String sortOrder = " DESC";
        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
        String selection = null;
        String[] selectionArsg = null;
        // check if we need to classify out others data
        switch (choice) {
            case FAVORITE_CRITERIA:
                sortOrder = MovieContract.MovieEntry.COLUMN_POPULARITY + sortOrder;
                selection = MovieContract.MovieEntry.COLUMN_FAVORITE + "=?";
                selectionArsg = new String[]{"1"};
                break;
            case POPULAR_CRITERIA:
                sortOrder = MovieContract.MovieEntry.COLUMN_POPULARITY + sortOrder;
                break;
            case HIGHEST_RATED_CRITERIA:
                sortOrder = MovieContract.MovieEntry.COLUMN_VOTE_AVG + sortOrder;
                break;
        }

        return new CursorLoader(
                getContext(),
                uri,
                null,
                selection,
                selectionArsg,
                sortOrder);

    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        if (data != null) {
            if (data.getCount() > 0){
                mCursor = data;
                mAdapter.swapCursor(mCursor);
                disableEmptyView();
            } else if (NetworkHelper.isConnectToInternet(getContext())){
                // try to force refetch data
                foreReFetchDataFromNetwork();
            } else {
                // reach here mean nothing we can do ..must display empty view.
                setEmptyView(getString(R.string.no_data_available));
            }

        } else {
            // hope we are not getting here
           // some thing is really wrong here ...might need to re-install the apps
            setEmptyView(getString(R.string.severe_app_execption_with_db));
        }

    }

    private void setEmptyView(String message) {
        // if failed to reload data ... we need to show the empty display
        View rootView = MovieListFragment.this.getView();
        RecyclerView mRecyclerView = (RecyclerView) rootView.
                findViewById(R.id.movie_list_recycler_view);
        TextView emptyView = (TextView) rootView.findViewById(R.id.movie_list_empty_view);
        emptyView.setText(message);
        if (mRecyclerView != null) {
            mRecyclerView.setVisibility(View.INVISIBLE);
        }
        if (emptyView != null) {
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * set empty view if network is not available
     */
    private void disableEmptyView() {
        // if failed to reload data ... we need to show the empty display
        View rootView = MovieListFragment.this.getView();
        RecyclerView mRecyclerView = (RecyclerView) rootView.
                findViewById(R.id.movie_list_recycler_view);
        TextView emptyView = (TextView) rootView.findViewById(R.id.movie_list_empty_view);
        if (mRecyclerView != null
                && mRecyclerView.getVisibility() == View.INVISIBLE) {
            mRecyclerView.setVisibility(View.VISIBLE);
        }
        if (emptyView != null) {
            emptyView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        // set cursor to null when data becomes invalid
        mAdapter.swapCursor(null);
    }


}

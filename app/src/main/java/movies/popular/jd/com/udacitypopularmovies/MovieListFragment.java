package movies.popular.jd.com.udacitypopularmovies;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.TextView;

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
        bundle.putInt(VIEW_CRITERIA,
                SharedPreferenceHelper.getViewCriteriaFromPref(getContext()));
        if (!forceReloadData(SharedPreferenceHelper.getViewCriteriaFromPref(getContext()))) {
            setEmptyView();
        } else {
            disableEmptyView();
        }
    }

    private void initLoader() {
        Bundle bundle = new Bundle();
        int choice = SharedPreferenceHelper.getViewCriteriaFromPref(getContext());
        bundle.putInt(VIEW_CRITERIA, choice);
        switch(choice){
            case FAVORITE_CRITERIA:
                getLoaderManager().initLoader(MOVIE_LIST_FRAGMENT_LOADER,bundle,this);
                disableEmptyView();
                break;
            default:
                // if not connected then dont init loader
                if (NetworkHelper.isConnectToInternet(getContext())){
                    getLoaderManager().initLoader(MOVIE_LIST_FRAGMENT_LOADER,bundle,this);
                    disableEmptyView();
                }
                else{
                    setEmptyView();
                }
        }

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
     * this called from MainActivity to change the
     * display movies on the main activity
     *
     * @return :
     * false : if and only if network is not available
     */
    private boolean forceReloadData(int choice) {
        Bundle bundle = new Bundle();
        bundle.putInt(VIEW_CRITERIA, choice);

        switch (choice) {
            case FAVORITE_CRITERIA:
                bundle.putInt(VIEW_CRITERIA, FAVORITE_CRITERIA);
                getLoaderManager().restartLoader(MOVIE_LIST_FRAGMENT_LOADER, bundle, this);
                return true;
            case HIGHEST_RATED_CRITERIA:
            case POPULAR_CRITERIA:
                if (!NetworkHelper.isConnectToInternet(getContext())) {
                    Log.e(TAG, "Not network connection");
                    return false;
                }

                FetchMovieListTask fetchTask = new FetchMovieListTask(getContext());
                String task = (choice == HIGHEST_RATED_CRITERIA) ?
                        MovieTaskHelper.TOP_RATED_STR : MovieTaskHelper.POPULAR_STR;
                fetchTask.execute(task);
                bundle.putInt(VIEW_CRITERIA, choice);
                getLoaderManager().restartLoader(MOVIE_LIST_FRAGMENT_LOADER, bundle, this);
                return true;
            default:
                Log.e(TAG, "Should not get Here !!!");
                return false;
        }
    }


    //************ LOADER CALL BACKS ******************************
    @Override
    public Loader onCreateLoader(int id, Bundle args) {

        int choice = args.getInt(VIEW_CRITERIA);
        String sortOrder = " DESC";
        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
        String selection = null;
        String[] selectionArsg = null;
        boolean isConnected = NetworkHelper.isConnectToInternet(getContext());
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
            mCursor = data;
            mAdapter.swapCursor(mCursor);
        } else {
            // need to fetch from network as database does not have data
            if (!forceReloadData(SharedPreferenceHelper.getViewCriteriaFromPref(getContext()))) {
                setEmptyView();
            } else {
                disableEmptyView();
            }
        }

    }

    private void setEmptyView() {
        // if failed to reload data ... we need to show the empty display
        View rootView = MovieListFragment.this.getView();
        RecyclerView mRecyclerView = (RecyclerView) rootView.
                findViewById(R.id.movie_list_recycler_view);
        TextView emptyView = (TextView) rootView.findViewById(R.id.movie_list_empty_view);
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

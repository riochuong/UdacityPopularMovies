package movies.popular.jd.com.udacitypopularmovies;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import movies.popular.jd.com.udacitypopularmovies.data.MovieContract;
import movies.popular.jd.com.udacitypopularmovies.tasks.FetchMovieListTask;


/**
 *
 */
public class MovieListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int MOVIE_LIST_FRAGMENT_LOADER = 0;
    private static final int GRID_SPAN = 2;
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
        View rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);

        // setup Recycler view
        setupRecyclerView(rootView);

        return rootView;
    }

    /**
     * Helper used to setup recycler view 
     * @param rootLayout
     */
    private void setupRecyclerView(View rootLayout){
        RecyclerView recView = (RecyclerView) rootLayout.findViewById(R.id.recyclerview);
        mLayoutManager = new GridLayoutManager(getContext(),GRID_SPAN);
        mAdapter = new MovieCursorRecyclerAdapter(this.getContext(), mCursor);
        recView.setLayoutManager(mLayoutManager);
        recView.setAdapter(mAdapter);
    }

    /**
     * this called from MainActivity to change the
     * display movies on the main activity
     *
     */
    public void onSelectionChange(String choice){
        Bundle bundle = new Bundle();
        switch(choice){
            case "favor":
                break;
            case "top_rated":
                FetchMovieListTask topRatedTask = new FetchMovieListTask(getContext());
                topRatedTask.execute("top_rated");
                bundle.putString("choice","top_rated");
                getLoaderManager().restartLoader(MOVIE_LIST_FRAGMENT_LOADER,bundle,this);
                break;
            case "popular":
                FetchMovieListTask popukarTask = new FetchMovieListTask(getContext());
                popukarTask.execute("popular");
                bundle.putString("choice","popular");
                getLoaderManager().restartLoader(MOVIE_LIST_FRAGMENT_LOADER,bundle,this);
                break;

        }

    }


    //************ LOADER CALL BACKS ******************************
    @Override
    public Loader onCreateLoader(int id, Bundle args) {

        String choice = args.getString("choice");
        String sortOrder = " DESC";
        if (choice.equalsIgnoreCase("top_rated")){
            sortOrder = MovieContract.MovieEntry.COLUMN_VOTE_AVG + sortOrder;
        }
        else{
            sortOrder = MovieContract.MovieEntry.COLUMN_POPULARITY + sortOrder;
        }

        return new CursorLoader(
                getContext(),
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                sortOrder);

    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
            mCursor = data;
            mAdapter.swapCursor(mCursor);
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

}

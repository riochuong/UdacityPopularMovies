package movies.popular.jd.com.udacitypopularmovies;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import movies.popular.jd.com.udacitypopularmovies.data.MovieContract;
import movies.popular.jd.com.udacitypopularmovies.tasks.FetchMovieDetailsLoaderTask;
import movies.popular.jd.com.udacitypopularmovies.tasks.MovieDetailLoaderInfo;
import movies.popular.jd.com.udacitypopularmovies.tasks.MovieTaskHelper;
import movies.popular.jd.com.udacitypopularmovies.ui.MovieReviewRecylerAdapter;


/**
 *
 * Display movie details in new fragment or separate new activity
 *
 */
public class MovieDetailFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<MovieDetailLoaderInfo> {

    @BindView(R.id.all_views_recycler_view) RecyclerView mHomeView;

    private static final int MOVIE_DETAIL_LOADER = 1;

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
        View v =  inflater.inflate(R.layout.fragment_movie_detail_upgrade, container, false);
        ButterKnife.bind(this, v);

        LinearLayoutManager allViewLayout = new LinearLayoutManager(
                                                    getContext(),
                                                   LinearLayoutManager.VERTICAL,false);

        mHomeView.setLayoutManager(allViewLayout);
        getLoaderManager().restartLoader(MOVIE_DETAIL_LOADER,null,this).forceLoad();
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_DETAIL_LOADER,null, this).forceLoad();
        super.onActivityCreated(savedInstanceState);
    }



    /**
     * helper to retreive movie id
     * @return
     */
    private String getMovieIdFromAgrs(){
        Bundle bundle = this.getArguments();
        if (bundle != null){
            return bundle.getString(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
        }
        return null;
    }


    @Override
    public Loader<MovieDetailLoaderInfo> onCreateLoader(int id, Bundle args) {

        return new FetchMovieDetailsLoaderTask(getContext(),getMovieIdFromAgrs());


    }

    @Override
    public void onLoadFinished(Loader<MovieDetailLoaderInfo> loader, MovieDetailLoaderInfo data) {

        // now we can bind the data
        if (data != null)
        {
           mHomeView.setAdapter(
                   new MovieReviewRecylerAdapter(getContext(),
                           data.getmReviewList(),
                           data.getmTrailerList(),this.getArguments()));
        }

    }

    @Override
    public void onLoaderReset(Loader<MovieDetailLoaderInfo> loader) {

    }
}

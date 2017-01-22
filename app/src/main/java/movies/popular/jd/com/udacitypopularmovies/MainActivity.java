package movies.popular.jd.com.udacitypopularmovies;

import android.app.Application;
import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import movies.popular.jd.com.udacitypopularmovies.tasks.FetchMovieListTask;
import movies.popular.jd.com.udacitypopularmovies.util.SharedPreferenceHelper;

import static movies.popular.jd.com.udacitypopularmovies.tasks.MovieTaskHelper.FAVORITE_CRITERIA;
import static movies.popular.jd.com.udacitypopularmovies.tasks.MovieTaskHelper.HIGHEST_RATED_CRITERIA;
import static movies.popular.jd.com.udacitypopularmovies.tasks.MovieTaskHelper.POPULAR_CRITERIA;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String MOVIE_DETAIL_FRAGMENT_TAG = "MVDETAIL_TAG";
    private boolean mTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (isInTwoPane()){
            Log.d(TAG,"Activity is in two pane mode");
            mTwoPane = true;
            // TODO: create the second pane fragment only if it's not an orientation change
            if (savedInstanceState == null){
               startMovieDetailView(null);
            }

        }

        //MovieListFragment mvListFragment = new MovieListFragment();
    }

    public boolean isInTwoPane (){
        return (this.findViewById(R.id.movie_detail_container) != null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }




    public void startMovieDetailView (Bundle mvInfoBundle){

        if (mvInfoBundle != null) {
            if (mTwoPane) {
                // let's replace the old fragment
                MovieDetailFragment mvFragment = new MovieDetailFragment();
                mvFragment.setArguments(mvInfoBundle);
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.movie_detail_container, mvFragment, MOVIE_DETAIL_FRAGMENT_TAG).commit();
            } else {
                // otherwise start detail activity with data passed
                Intent startDetailAct = new Intent(this, MovieDetailActivity.class);
                startDetailAct.putExtras(mvInfoBundle);
                startActivity(startDetailAct);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        MovieListFragment mvFrag = (MovieListFragment)
                        getSupportFragmentManager().findFragmentById(R.id.movie_list_fragment);
        switch (id){
            case R.id.favor:
                // get favorites fromd Database.
                Log.d(TAG,"Query favorite movies rom DB");
                mvFrag.onSelectionChange(FAVORITE_CRITERIA);
                SharedPreferenceHelper.
                        storeViewCriteriaToPref(this,FAVORITE_CRITERIA);
                break;
            case R.id.popular:
                // refetch popular movies
                Log.d(TAG,"Refetch popular movies");
                mvFrag.onSelectionChange(POPULAR_CRITERIA);
                SharedPreferenceHelper.
                        storeViewCriteriaToPref(this,POPULAR_CRITERIA);
                break;
            case R.id.top_rated:
                // refetch top rated movie
                Log.d(TAG,"Refetch top rated movies");
                mvFrag.onSelectionChange(HIGHEST_RATED_CRITERIA);
                SharedPreferenceHelper.
                        storeViewCriteriaToPref(this,HIGHEST_RATED_CRITERIA);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}

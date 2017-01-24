package movies.popular.jd.com.udacitypopularmovies;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import movies.popular.jd.com.udacitypopularmovies.util.SharedPreferenceHelper;

import static movies.popular.jd.com.udacitypopularmovies.tasks.MovieTaskHelper.FAVORITE_CRITERIA;
import static movies.popular.jd.com.udacitypopularmovies.tasks.MovieTaskHelper.HIGHEST_RATED_CRITERIA;
import static movies.popular.jd.com.udacitypopularmovies.tasks.MovieTaskHelper.POPULAR_CRITERIA;

public class MovieListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = MovieListActivity.class.getSimpleName();
    private static final String MOVIE_DETAIL_FRAGMENT_TAG = "MVDETAIL_TAG";
    private boolean mTwoPane = false;

    private static int MENU_ITEM_FAV = 2;
    private static int MENU_ITEM_POP = 0;
    private static int MENU_ITEM_TOP_RATED = 1;

    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.naviation_view) NavigationView mNavigationView;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.spinner) ProgressBar mSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (isInTwoPane()){
            Log.d(TAG,"Activity is in two pane mode");
            mTwoPane = true;
            if (savedInstanceState == null){
               startMovieDetailView(null);
            }

        }
        mSpinner.setVisibility(View.VISIBLE);
        setSupportActionBar(mToolbar);
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        mSpinner.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // adding action bar toggle listener
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close
        );
        mDrawerLayout.addDrawerListener(drawerToggle);

        drawerToggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);

        // set initial hightl
        setInitialHighlightedItemForNavView();
    }

    /**
     * set initialize highlighted item for nav view
     */
    private void setInitialHighlightedItemForNavView(){
        int choice = SharedPreferenceHelper.getViewCriteriaFromPref(this);
        switch(choice){
            case FAVORITE_CRITERIA:
                mNavigationView.getMenu().getItem(MENU_ITEM_FAV).setChecked(true);
                mToolbar.setTitle(R.string.favorite_menu_item);
                break;
            case POPULAR_CRITERIA:
                mNavigationView.getMenu().getItem(MENU_ITEM_POP).setChecked(true);
                mToolbar.setTitle(R.string.popular_menu_item);
                break;
            case HIGHEST_RATED_CRITERIA:
                mNavigationView.getMenu().getItem(MENU_ITEM_TOP_RATED).setChecked(true);
                mToolbar.setTitle(R.string.top_rated_menu_item);
                break;
            default:
                break;
        }
    }


    public boolean isInTwoPane (){
        return (this.findViewById(R.id.movie_detail_container) != null);
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        MovieListFragment mvFrag = (MovieListFragment)
                getSupportFragmentManager().findFragmentById(R.id.movie_list_fragment);
        switch (item.getItemId()){
            case R.id.favorite_item:
                // get favorites fromd Database.
                Log.d(TAG,"Query favorite movies rom DB");
                SharedPreferenceHelper.
                        storeViewCriteriaToPref(this,FAVORITE_CRITERIA);
                mvFrag.onSelectionChange();
                mToolbar.setTitle(R.string.favorite_menu_item);
                break;
            case R.id.popular_item:
                // refetch popular movies
                Log.d(TAG,"Refetch popular movies");
                SharedPreferenceHelper.
                        storeViewCriteriaToPref(this,POPULAR_CRITERIA);
                mvFrag.onSelectionChange();
                mToolbar.setTitle(R.string.popular_menu_item);
                break;
            case R.id.top_rated_item:
                // refetch top rated movie
                Log.d(TAG,"Refetch top rated movies");
                SharedPreferenceHelper.
                        storeViewCriteriaToPref(this,HIGHEST_RATED_CRITERIA);
                mvFrag.onSelectionChange();
                mToolbar.setTitle(R.string.top_rated_menu_item);
                break;
        }

        // animating closing after click
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}

package movies.popular.jd.com.udacitypopularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;

public class MovieDetailActivity extends AppCompatActivity {

    private static final String MOVIE_DETAIL_FRAG = "MOVIE_DETAIL_FRAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        if (savedInstanceState == null){
            // now inflate
            MovieDetailFragment newFrag = new MovieDetailFragment();
            // get data passed from Mainactivity to initialize details view
            newFrag.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().replace(
                    R.id.movie_detail_container,newFrag,MOVIE_DETAIL_FRAG
            ).commit();
        }
    }
}

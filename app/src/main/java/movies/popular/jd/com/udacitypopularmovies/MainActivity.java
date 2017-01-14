package movies.popular.jd.com.udacitypopularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import movies.popular.jd.com.udacitypopularmovies.tasks.FetchMovieListTask;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FetchMovieListTask fetchTask = new FetchMovieListTask(this);
        fetchTask.execute("trailers","297761");
    }
}

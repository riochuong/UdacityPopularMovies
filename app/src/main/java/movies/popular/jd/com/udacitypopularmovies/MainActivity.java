package movies.popular.jd.com.udacitypopularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FetchMovieListTask fetchTask = new FetchMovieListTask();
        fetchTask.execute("reviews","297761");
    }
}

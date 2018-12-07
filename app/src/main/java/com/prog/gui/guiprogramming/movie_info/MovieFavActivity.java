package com.prog.gui.guiprogramming.movie_info;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toolbar;

import com.prog.gui.guiprogramming.R;

import java.util.List;

public class MovieFavActivity extends Activity {

    ListView lvFavMovies;
    //Custom list adapter
    MovieFavoritesAdapter listAdapter;
    //Custom data set - model class
    List<Movie> mRowData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_activitiy_fav);
        initViews();
    }

    void initViews(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
        lvFavMovies = findViewById(R.id.lvFavMovies);
        getMoviesFromDB(); // this function retrieves favorite movies from database
        lvFavMovies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                /* startActivityForResult to detect whether
                 * user has removed movie from favorites or not
                 * result will be handled in onActivityResult
                 * */
                startActivityForResult(
                        new Intent(MovieFavActivity.this, MovieDetailActivity.class)
                                //Passing MOVIE_ID to filter particular movie from database
                                .putExtra(MovieAppHelper.INTENT_PASS_MOVIE_ID,
                                        mRowData.get(i).getImdbID())
                                //TYPE is passed because we are using same activity to
                                // showing movie details from IMDb database(online) and also for
                                // showing movie details from SQLite database (offline)
                                .putExtra(MovieAppHelper.INTENT_PASS_TYPE,
                                        MovieAppHelper.INTENT_PASS_TYPE_LOCAL),

                        MovieAppHelper.RESULT_MOVIE_DELETED);
            }
        });
    }

    /**
     * Getting list of movies and set to ListView
     * */
    private void getMoviesFromDB() {
        MovieDBManager movieDbManager = new MovieDBManager(MovieFavActivity.this);
        movieDbManager.open(); //require to open a database connection
        mRowData = movieDbManager.getFavoritesList(); //Getting favorite movie list
        listAdapter = new MovieFavoritesAdapter(mRowData, MovieFavActivity.this);
        lvFavMovies.setAdapter(listAdapter);
        //closing connection after usage
        movieDbManager.close();
    }

    /**
     * Handling startActivityForResult() method's result
     * */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //If this condition is true, that means one row is deleted
        // from database and we need to update our ListView
        if (resultCode == MovieAppHelper.RESULT_MOVIE_DELETED) {
            //Calling again to refresh ListView
            getMoviesFromDB();
        }
    }
}

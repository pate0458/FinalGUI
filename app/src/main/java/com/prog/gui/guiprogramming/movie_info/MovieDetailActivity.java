package com.prog.gui.guiprogramming.movie_info;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.prog.gui.guiprogramming.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * MovieDetailActivity shows all the detailed information about particular movie
 * */
public class MovieDetailActivity extends Activity {

    ImageView imgMoviePoster;
    TextView tvMovieTitle, tvShowType, tvMovieYear, tvMovieGenre, tvMovieIMDBRating,
            tvMovieLanguage, tvMoviePlot, tvMovieRuntime, tvMovieCountry;
    RelativeLayout relPosterBG;
    Button btnAddToFav;

    String Title, Year, Poster, Type, imdbID, Runtime, Genre,
            Plot, Language, Country, imdbRating;
    int movieRuntime;
    String IMDbID, TYPE;
    Intent intent;
    boolean isExists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_activity_movie_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
        intent = getIntent();
        TYPE = intent.getStringExtra(MovieAppHelper.INTENT_PASS_TYPE);
        IMDbID = intent.getStringExtra(MovieAppHelper.INTENT_PASS_MOVIE_ID);
        relPosterBG = findViewById(R.id.relPosterBG);
        imgMoviePoster = findViewById(R.id.imgMoviePoster);
        tvMovieTitle = findViewById(R.id.tvMovieTitle);
        tvShowType = findViewById(R.id.tvShowType);
        tvMovieYear = findViewById(R.id.tvMovieYear);
        tvMovieGenre = findViewById(R.id.tvMovieGenre);
        tvMovieIMDBRating = findViewById(R.id.tvMovieIMDBRating);
        tvMovieLanguage = findViewById(R.id.tvMovieLanguage);
        tvMoviePlot = findViewById(R.id.tvMoviePlot);
        tvMovieRuntime = findViewById(R.id.tvMovieRuntime);
        tvMovieCountry = findViewById(R.id.tvMovieCountry);
        btnAddToFav = findViewById(R.id.btnAddToFav);

        //If TYPE = SERVER then get details from API
        // otherwise get details from local database
        if (TYPE.equals(MovieAppHelper.INTENT_PASS_TYPE_SERVER)) {
            new MovieDetailAsync(IMDbID).execute();
        } else if (TYPE.equals(MovieAppHelper.INTENT_PASS_TYPE_LOCAL)) {
            getMovieDetailsFromDB(IMDbID);
        }

        MovieDBManager movieDbManager = new MovieDBManager(MovieDetailActivity.this);
        movieDbManager.open();
        isExists = movieDbManager.isExists(IMDbID);
        if(isExists){
            btnAddToFav.setText(getResources().getString(R.string.movie_remove_from_favorites));
        }else{
            btnAddToFav.setText(getResources().getString(R.string.movie_add_to_string_favorites));
        }
        movieDbManager.close();
        btnAddToFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MovieDBManager movieDbManager = new MovieDBManager(MovieDetailActivity.this);
                movieDbManager.open();
                String message = "";
                if(isExists){
                    if(movieDbManager.removeFavorite(imdbID)){
                        message = getResources().getString(R.string.movie_msg_removed);
                        setResult(MovieAppHelper.RESULT_MOVIE_DELETED);
                    }else{
                        message = getResources().getString(R.string.movie_msg_not_removed);
                    }
                }else{
                    String fileName = Poster.substring(Poster.lastIndexOf('/') + 1, Poster.length());
                    if (movieDbManager.addMovie(imdbID, Title, fileName,
                            Year, Plot, Type, Language, imdbRating, Genre, movieRuntime, Country)
                            != -1) {
                        message = getResources().getString(R.string.movie_msg_added);
                    }else{
                        message = getResources().getString(R.string.movie_msg_not_added);
                    }
                }
                Snackbar snackbar = Snackbar.make(relPosterBG, message, Snackbar.LENGTH_SHORT);
                snackbar.show();
                isExists = movieDbManager.isExists(IMDbID);
                if(isExists){
                    btnAddToFav.setText(getResources().getString(R.string.movie_remove_from_favorites));
                }else{
                    btnAddToFav.setText(getResources().getString(R.string.movie_add_to_string_favorites));
                }
                movieDbManager.close();
            }
        });
    }

    class MovieDetailAsync extends AsyncTask<Void, Integer, String> {

        String mURL;
        ProgressDialog dialog = new ProgressDialog(MovieDetailActivity.this);

        public MovieDetailAsync(String mURL) {
            this.mURL = "http://www.omdbapi.com/?apikey=715834d3&i="+mURL;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String message = getResources().getString(R.string.movie_please_wait);
            dialog.setMessage(message);
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            InputStream inputStream = null;
            URL url = null;
            HttpURLConnection connection = null;
            String inputLine;
            try {
                url = new URL(mURL);
                connection = (HttpURLConnection) url.openConnection();
                inputStream = (InputStream) connection.getContent();
                //Create a new InputStreamReader
                InputStreamReader streamReader = new
                        InputStreamReader(inputStream);
                //Create a new buffered reader and String Builder
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                //Check if the line we are reading is not null
                while((inputLine = reader.readLine()) != null){
                    stringBuilder.append(inputLine);
                }
                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();
                //Set our result equal to our stringBuilder
                return stringBuilder.toString();

            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            //dismissing a dialog
            dialog.dismiss();
            try {
                //JSON parsing
                JSONObject jsonObject = new JSONObject(response);
                String Response = jsonObject.getString("Response");
                if (Response.equals("True")) {
                    Title = jsonObject.getString("Title");
                    Year = jsonObject.getString("Year");
                    imdbID = jsonObject.getString("imdbID");
                    Runtime = jsonObject.getString("Runtime");
                    movieRuntime = Integer.parseInt(Runtime.split(" ")[0]);
                    Genre = jsonObject.getString("Genre");
                    Plot = jsonObject.getString("Plot");
                    Language = jsonObject.getString("Language");
                    Country = jsonObject.getString("Country");
                    imdbRating = jsonObject.getString("imdbRating");
                    Poster = jsonObject.getString("Poster");
                    Type = jsonObject.getString("Type");
                    new MovieImageLoader(Poster, imgMoviePoster).execute();
                    tvMovieTitle.setText(Title);
                    tvMovieYear.setText(Year);
                    tvMovieGenre.setText(Genre);
                    tvMoviePlot.setText(Plot);
                    tvMovieLanguage.setText(Language);
                    tvMovieCountry.setText(Country);
                    tvMovieIMDBRating.setText(imdbRating);
                    tvMovieRuntime.setText(Runtime);
                    tvShowType.setText(Type);
                } else {
                    MovieAppHelper.showToast(MovieDetailActivity.this, jsonObject.getString("Error"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * to get favorite movie detail from database
     * @param IMDbID to filter movies
     * */
    void getMovieDetailsFromDB(String IMDbID) {
        MovieDBManager movieDbManager = new MovieDBManager(MovieDetailActivity.this);
        movieDbManager.open();
        Movie movie = movieDbManager.getMovieDetails(IMDbID);
        imdbID = movie.getImdbID();
        Title = movie.getTitle();
        Year = movie.getYear();
        Genre = movie.getGenre();
        Plot = movie.getPlot();
        Language = movie.getLanguage();
        Country = movie.getCountry();
        imdbRating = movie.getRating();
        movieRuntime = movie.getRuntime();
        Type = movie.getType();
        Poster = movie.getPoster();
        Runtime = movieRuntime + " min";

        tvMovieTitle.setText(Title);
        tvMovieYear.setText(Year);
        tvMovieGenre.setText(Genre);
        tvMoviePlot.setText(Plot);
        tvMovieLanguage.setText(Language);
        tvMovieCountry.setText(Country);
        tvMovieIMDBRating.setText(imdbRating);
        tvMovieRuntime.setText(Runtime);
        tvShowType.setText(Type);
        File storagePath = new File(Environment.getExternalStorageDirectory().getPath() +
                File.separator + "MOVIE_INFO_APP" + File.separator + "MOVIE_IMAGES" + File.separator + Poster);
        //Check if poster file is exists or not
        if (storagePath.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(storagePath.getAbsolutePath());
            imgMoviePoster.setImageBitmap(bitmap);
        }
        movieDbManager.close();
    }

    /**
     * Binding menu to activity
     * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * Handling click event of menu items
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home: //Back button on action bar
                onBackPressed();
                break;
            case R.id.menuHelp:
                showHelpDialog();
                break;
            case R.id.menuStat:
                startActivity(new Intent(MovieDetailActivity.this, MovieStatisticsActivity.class));
                break;
        }
        return true;
    }

    /**
     * Showing a help dialog to user
     * */
    void showHelpDialog() {
        final Dialog dialog = new Dialog(MovieDetailActivity.this);
        dialog.setTitle(getResources().getString(R.string.movie_help));
        //setting layout to dialog
        dialog.setContentView(R.layout.movie_dialog_detail_help);
        //initializing widgets of custom dialog layout
        Button btnOk = dialog.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        //showing dialog with MATCH_PARENT width.
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);

        dialog.setCancelable(true);
        dialog.show();
    }
}

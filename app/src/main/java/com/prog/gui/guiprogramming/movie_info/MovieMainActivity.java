package com.prog.gui.guiprogramming.movie_info;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toolbar;

import com.prog.gui.guiprogramming.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * MovieMainActivity contains two fragments, one for searching movies
 * and other one for showing favorite movies list
 */
public class MovieMainActivity extends Activity {

    Button btnFav;
    SearchView svMovieInput;
    ListView lvMovies;

    MovieListAdapter listAdapter;
    List<Movie> mRowData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_activity_main);
        initViews();
        checkPermissions();
    }

    void checkPermissions() {
        if (ContextCompat.checkSelfPermission(MovieMainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MovieMainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                showPermissionAlertDialog();
            } else {
                // No explanation needed to user; request the permission
                reqPermission();
            }
        }
        // Permission has already been granted
    }

    void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
        btnFav = findViewById(R.id.btnFav);
        svMovieInput = findViewById(R.id.svMovieInput);
        lvMovies = findViewById(R.id.lvMovies);

        mRowData = new ArrayList<>();
        listAdapter = new MovieListAdapter(mRowData, MovieMainActivity.this);
        lvMovies.setAdapter(listAdapter);

        svMovieInput.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //Called when user hits Search Icon on keyboard
            @Override
            public boolean onQueryTextSubmit(String query) {
                new SearchMoviesAsync(query.replaceAll(" ", "%20"))
                        .execute();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        lvMovies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(new Intent(MovieMainActivity.this, MovieDetailActivity.class)
                        .putExtra(MovieAppHelper.INTENT_PASS_MOVIE_ID, mRowData.get(i).getImdbID())
                        .putExtra(MovieAppHelper.INTENT_PASS_TYPE, MovieAppHelper.INTENT_PASS_TYPE_SERVER));
            }
        });

        btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MovieMainActivity.this, MovieFavActivity.class));
            }
        });
    }

    /**
     * Binding menu to activity
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * Handling click event of menu items
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menuStat:
                startActivity(new Intent(MovieMainActivity.this, MovieStatisticsActivity.class));
                break;
            case R.id.menuHelp:
                showHelpDialog();
                break;
        }
        return true;
    }


    /**
     * Showing a help dialog to user
     */
    void showHelpDialog() {
        final Dialog dialog = new Dialog(MovieMainActivity.this);
        dialog.setTitle(getResources().getString(R.string.movie_help));
        //setting layout to dialog
        dialog.setContentView(R.layout.movie_dialog_home_help);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Got permissions
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(MovieMainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //Show Information about why you need the permission
                    showPermissionAlertDialog();
                } else {
                    MovieAppHelper.showToast(this, "Won't be able to continue without permission, "
                            + "Go to Settings -> Applications -> Movie information -> Permission and allow permission to continue !");
                    finish();
                }
            }
        }
    }

    /**
     * Request permission dialog, system provided
     */
    public void reqPermission() {
        ActivityCompat.requestPermissions(MovieMainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
    }

    /**
     * Showing "why do we need this permission" dialog to user
     */
    void showPermissionAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MovieMainActivity.this);
        builder.setTitle("Permission request");
        builder.setMessage("This app needs WRITE_EXTERNAL_STORAGE permission to store your " +
                "favorite movies' details offline. Allow to continue!");
        builder.setCancelable(false);
        builder.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                reqPermission();
            }
        });
        builder.setNegativeButton("Deny", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });
        builder.show();
    }

    class SearchMoviesAsync extends AsyncTask<Void, Integer, String> {

        String mURL;
        ProgressDialog dialog = new ProgressDialog(MovieMainActivity.this);

        public SearchMoviesAsync(String mURL) {
            this.mURL = "http://www.omdbapi.com/?apikey=715834d3&s="+mURL;
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
                mRowData.clear();
                if (Response.equals("True")) {
                    JSONArray jsonArray = jsonObject.getJSONArray("Search");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject data = jsonArray.getJSONObject(i);
                        String Title = data.getString("Title");
                        String Year = data.getString("Year");
                        String imdbID = data.getString("imdbID");
                        String Type = data.getString("Type");
                        String Poster = data.getString("Poster");
                        //Running async task to saving posters to local storage
                        new MovieImage(Poster).execute();
                        Movie movie = new Movie();
                        movie.setImdbID(imdbID);
                        movie.setPoster(Poster);
                        movie.setYear(Year);
                        movie.setType(Type);
                        movie.setTitle(Title);
                        mRowData.add(movie);
                    }
                } else {
                    MovieAppHelper.showToast(MovieMainActivity.this, jsonObject.getString("Error"));
                }
                listAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

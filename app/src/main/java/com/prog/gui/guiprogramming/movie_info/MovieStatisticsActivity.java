package com.prog.gui.guiprogramming.movie_info;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.prog.gui.guiprogramming.R;


public class MovieStatisticsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_activity_statistics);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
        TextView tvMovieCount = findViewById(R.id.tvMovieCount);
        TextView tvShortRuntime = findViewById(R.id.tvShortRuntime);
        TextView tvAvgRuntime = findViewById(R.id.tvAvgRuntime);
        TextView tvLongRuntime = findViewById(R.id.tvLongRuntime);
        //Establishing database connection
        MovieDBManager db = new MovieDBManager(MovieStatisticsActivity.this);
        db.open();
        //getting no. of records from database
        int count = db.getCount();
        //if more than 0 records are stored then view stats
        if (count > 0) {
            String c = String.valueOf(count);
            tvMovieCount.setText(c);
            String unit = getResources().getString(R.string.movie_min);
            String shortestRunTime = db.getShortestRunTimeMovie() + " " + unit;
            String avgRunTime = db.getAvgRunTimeMovie() + " " + unit;
            String longestRunTime = db.getLongestRunTimeMovie() + " " + unit;
            tvShortRuntime.setText(shortestRunTime);
            tvAvgRuntime.setText(avgRunTime);
            tvLongRuntime.setText(longestRunTime);
        } else {
            //show message otherwise
            Toast.makeText(this, getResources().getString(R.string.movie_none_saved_msg), Toast.LENGTH_SHORT).show();
        }
        db.close();
    }
}

package com.prog.gui.guiprogramming;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toolbar;

import com.prog.gui.guiprogramming.cbc_news_reader.CBCMainActivity;
import com.prog.gui.guiprogramming.food_nut_db.FoodMainActivity;
import com.prog.gui.guiprogramming.movie_info.MovieMainActivity;

/**
 * This works as Navigation screen
 * */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
    }

    /**
     * Initializing all the UI widgets and handling click events
     * */
    private void initUI(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
        Button btnFoodApp = findViewById(R.id.btnFoodApp);
        Button btnCBCNewsApp = findViewById(R.id.btnCBCNewsApp);
        Button btnMovieApp = findViewById(R.id.btnMovieApp);

        btnFoodApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FoodMainActivity.class);
                startActivity(intent);
            }
        });

        btnCBCNewsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CBCMainActivity.class);
                startActivity(intent);
            }
        });

        btnMovieApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MovieMainActivity.class);
                startActivity(intent);
            }
        });
    }
}

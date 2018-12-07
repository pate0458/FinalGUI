package com.prog.gui.guiprogramming.food_nut_db;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.prog.gui.guiprogramming.R;

public class FoodStatsActivity extends Activity {

    TextView tvTotalItems, tvSumCalories, tvMinCalories, tvAvgCalories, tvMaxCalories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_activity_stats);
        initViews();
    }

    /**
     * <p>Initializing all the layout widgets</p>
     * */
    void initViews(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
        tvTotalItems = findViewById(R.id.tvTotalItems);
        tvSumCalories = findViewById(R.id.tvSumCalories);
        tvMinCalories = findViewById(R.id.tvMinCalories);
        tvAvgCalories = findViewById(R.id.tvAvgCalories);
        tvMaxCalories = findViewById(R.id.tvMaxCalories);
        setAllData();
    }

    /**
     * <p>Setting statistics to TextViews</p>
     * */
    void setAllData() {
        //Establishing database connection
        FoodDB db = new FoodDB(FoodStatsActivity.this);
        db.open();
        //getting no. of records from database
        int count = db.getCount();
        //if more than 0 records are stored then view stats
        if (count > 0) {
            String c = String.valueOf(count);
            tvTotalItems.setText(c);
            String sum = String.valueOf(db.getTotalCalories());
            String min = String.valueOf(db.getMinCalories());
            String avg = String.valueOf(db.getAvgCalories());
            String max = String.valueOf(db.getMaxCalories());
            tvSumCalories.setText(sum);
            tvMinCalories.setText(min);
            tvAvgCalories.setText(avg);
            tvMaxCalories.setText(max);
        } else {
            //show message otherwise
            String message = getResources().getString(R.string.food_none_saved_msg);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
        db.close();
    }
}

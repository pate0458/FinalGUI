package com.prog.gui.guiprogramming.cbc_news_reader;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.prog.gui.guiprogramming.R;

/**
 * CBCStatsActivity shows the statistics for news article description
 */
public class CBCStatsActivity extends Activity {

    TextView tvTotalNewsCount, tvMaxWordCount, tvAvgWordCount, tvMinWordCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cbc_activity_stats);
        initViews();
    }

    /**
     * <p>Initializing all the layout widgets</p>
     * */
    void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
        tvTotalNewsCount = findViewById(R.id.tvTotalNewsCount);
        tvMaxWordCount = findViewById(R.id.tvMaxWordCount);
        tvAvgWordCount = findViewById(R.id.tvAvgWordCount);
        tvMinWordCount = findViewById(R.id.tvMinWordCount);
        setAllData();
    }

    /**
     * <p>Setting statistics to TextViews</p>
     * */
    void setAllData() {
        //Establishing database connection
        CBCDB db = new CBCDB(CBCStatsActivity.this);
        db.open();
        //getting no. of records from database
        int count = db.getCount();
        //if more than 0 records are stored then view stats
        if (count > 0) {
            String c = String.valueOf(count);
            tvTotalNewsCount.setText(c);
            String min = db.getMinWords() + " words";
            String avg = db.getAvgWords() + " words";
            String max = db.getMaxWords() + " words";
            tvMinWordCount.setText(min);
            tvAvgWordCount.setText(avg);
            tvMaxWordCount.setText(max);
        } else {
            //show message otherwise
            String message = getResources().getString(R.string.cbc_none_saved_msg);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
        db.close();
    }
}

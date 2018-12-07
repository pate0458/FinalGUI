package com.prog.gui.guiprogramming.food_nut_db;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.prog.gui.guiprogramming.R;

import java.util.ArrayList;

public class FoodFavActivity extends Activity {

    ListView listFavFoods;
    FoodFavoritesAdapter adapter;
    ArrayList<FoodModel> mRowData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_activity_fav);
        initViews();
    }

    void initViews(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
        listFavFoods = findViewById(R.id.listFavFoods);
        getFavorites();
        listFavFoods.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //startActivity can also be used, but on this screen, it has to be detected whether
                // the clicked food item is deleted or not, if deleted then list has to be updated
                //that's why startActivityForResult is used
                startActivityForResult(new Intent(FoodFavActivity.this, FoodDetailActivity.class)
                                .putExtra("food", mRowData.get(i)),
                        1);
            }
        });
    }

    void getFavorites() {
        //establishing database connection
        FoodDB dbManager = new FoodDB(FoodFavActivity.this);
        dbManager.open();
        //getting the list of food from the database
        mRowData = dbManager.getFavoritesList();
        adapter = new FoodFavoritesAdapter(FoodFavActivity.this, mRowData);
        listFavFoods.setAdapter(adapter);
        //closing the database connection
        dbManager.close();
    }

    //Detecting whether the article is deleted or not, if deleted then updating the list
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            getFavorites();
        }
    }
}

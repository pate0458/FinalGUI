package com.prog.gui.guiprogramming.cbc_news_reader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toolbar;

import com.prog.gui.guiprogramming.R;

import java.util.ArrayList;

public class CBCFavActivity extends Activity {

    ArrayList<CBCNewsModel> mRowData;
    ListView listViewFavouriteArticles;
    CBCNewsFeedAdapter favouriteArticlesAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cbc_activity_fav);
        initViews();
    }

    void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
        listViewFavouriteArticles = findViewById(R.id.lvFavArticles);
        //getting list from the database
        getArticlesFromDB();
        listViewFavouriteArticles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //startActivity can also be used, but on this screen, it has to be detected whether
                // the clicked article is deleted or not, if deleted then list has to be updated
                //that's why startActivityForResult is used
                startActivityForResult(
                        new Intent(CBCFavActivity.this, CBCNewsDetailActivity.class)
                                .putExtra("article", mRowData.get(i)),
                        1);
            }
        });
    }

    private void getArticlesFromDB() {
        //establishing database connection
        CBCDB db = new CBCDB(CBCFavActivity.this);
        db.open();
        //getting the list of articles from the database
        mRowData = db.getAllArticles();
        favouriteArticlesAdapter = new CBCNewsFeedAdapter(CBCFavActivity.this, mRowData);
        listViewFavouriteArticles.setAdapter(favouriteArticlesAdapter);
        //closing the database connection
        db.close();
    }

    //Detecting whether the article is deleted or not, if deleted then updating the list
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            getArticlesFromDB();
        }
    }
}

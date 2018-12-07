package com.prog.gui.guiprogramming.cbc_news_reader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;


/**
 *
 */
public class CBCMainActivity extends Activity {

    Button btnFav;
    ListView listFeeds;
    CBCNewsFeedAdapter listAdapter;
    ArrayList<CBCNewsModel> mRowData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cbc_activity_main);

        //Initializing all the layout widgets
        initViews();
    }

    /**
     * <p>Initializing all the layout widgets</p>
     * */
    void initViews(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
        btnFav = findViewById(R.id.btnFav);
        listFeeds = findViewById(R.id.listFeeds);

        mRowData = new ArrayList<>();
        listAdapter = new CBCNewsFeedAdapter(CBCMainActivity.this, mRowData);
        listFeeds.setAdapter(listAdapter);

        btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CBCMainActivity.this, CBCFavActivity.class));
            }
        });

        listFeeds.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(new Intent(CBCMainActivity.this, CBCNewsDetailActivity.class)
                        .putExtra("article", mRowData.get(i)));
            }
        });


        //Executing async task
        new GetFeedsAsync().execute();
    }

    /**
     * <p>Inflating menu</p>
     * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * <p>handling menuItem clicks</p>
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menuStat:
                startActivity(new Intent(CBCMainActivity.this, CBCStatsActivity.class));
                break;
            case R.id.menuHelp:
                showHelpDialog();
                break;
        }
        return true;
    }


    /**
     * Showing custom layout HELP dialog
     */
    void showHelpDialog() {
        final Dialog dialog = new Dialog(CBCMainActivity.this);
        dialog.setTitle(getResources().getString(R.string.cbc_help));
        //setting layout to dialog
        dialog.setContentView(R.layout.cbc_dialog_home_help);
        //initializing widgets of custom dialog layout
        Button btnOk = dialog.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        //setting height of a dialog
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);

        dialog.setCancelable(true);
        dialog.show();
    }

    //Async task to get news feeds
    @SuppressLint("StaticFieldLeak")
    public class GetFeedsAsync extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog = new ProgressDialog(CBCMainActivity.this);
        //RSS-feeds URL
        String mURL = "https://www.cbc.ca/cmlink/rss-world";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Showing progress dialog
            String message = getResources().getString(R.string.cbc_please_wait);
            dialog.setMessage(message);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL(mURL);
                XmlPullParserFactory parserFactory = XmlPullParserFactory.newInstance();
                parserFactory.setNamespaceAware(false);
                XmlPullParser parser = parserFactory.newPullParser();
                parser.setInput(url.openConnection().getInputStream(), "UTF_8");
                boolean isInItemTag = false;
                int eventType = parser.getEventType();
                //clearing a list if it has old data
                mRowData.clear();
                CBCNewsModel newsModel = null;
                //XML parsing
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if (parser.getName().equals("item")) {
                            isInItemTag = true;
                        } else if (parser.getName().equals("title")) {
                            if (isInItemTag) {
                                newsModel = new CBCNewsModel();
                                String title = parser.nextText();
                                newsModel.setTitle(title);
                            }
                        } else if (parser.getName().equals("link")) {
                            if (isInItemTag) {
                                String link = parser.nextText();
                                if (newsModel != null)
                                    newsModel.setLink(link);
                            }
                        } else if (parser.getName().equals("description")) {
                            if (isInItemTag) {
                                String d = parser.nextText();
                                String description = d.substring(d.indexOf("<p>"), d.lastIndexOf("</p>"));
                                description = description.replace("<p>", "");
                                if (newsModel != null)
                                    newsModel.setDescription(description);
                            }
                        }else if (parser.getName().equals("pubDate")) {
                            if (isInItemTag) {
                                String pubDate = parser.nextText();
                                if (newsModel != null)
                                    newsModel.setPubDate(pubDate);
                            }
                        }else if (parser.getName().equals("author")) {
                            if (isInItemTag) {
                                String author = parser.nextText();
                                if (newsModel != null)
                                    newsModel.setAuthor(author);
                            }
                        }else if (parser.getName().equals("category")) {
                            if (isInItemTag) {
                                String category = parser.nextText();
                                if (newsModel != null)
                                    newsModel.setCategory(category);
                            }
                        }
                    } else if (eventType == XmlPullParser.END_TAG && parser.getName().equals("item")) {
                        isInItemTag = false;
                        mRowData.add(newsModel);
                    }
                    eventType = parser.next();
                }
            } catch (IOException | XmlPullParserException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //dismissing a dialog
            dialog.dismiss();
            //updating a ListView through adapter
            listAdapter.notifyDataSetChanged();
        }
    }
}

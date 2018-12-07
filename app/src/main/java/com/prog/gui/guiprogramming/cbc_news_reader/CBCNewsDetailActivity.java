package com.prog.gui.guiprogramming.cbc_news_reader;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.prog.gui.guiprogramming.R;

/**
 * <p>Shows detailed info for a news article</p>
 */
public class CBCNewsDetailActivity extends Activity {

    String articleLink = "";
    String title;
    Button btnSave, btnViewMore;
    CBCDB db;
    CBCNewsModel data;
    LinearLayout linearParent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cbc_activity_detail);
        //Initializing all the layout widgets
        initViews();
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message;
                //if data already exists, then delete otherwise insert data
                if (hasArticle(db)) {
                    if (db.removeArticle(title)) {
                        setResult(1);
                        message = getResources().getString(R.string.cbc_msg_removed);
                    } else {
                        message = getResources().getString(R.string.cbc_msg_not_removed);
                    }
                } else {
                    if (db.saveArticle(data) != -1) {
                        message = getResources().getString(R.string.cbc_msg_added);
                    } else {
                        message = getResources().getString(R.string.cbc_msg_not_added);
                    }
                }
                hasArticle(db);
                Snackbar snackbar = Snackbar.make(linearParent, message, Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        });

        btnViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open article link into a browser
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(articleLink));
                startActivity(i);
            }
        });
    }

    /**
     * <p>Initializing all the layout widgets</p>
     * */
    void initViews(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
        TextView tvNewsTitle = findViewById(R.id.tvNewsTitle);
        TextView tvNewsDesc = findViewById(R.id.tvNewsDesc);
        TextView tvAuthName = findViewById(R.id.tvAuthName);
        TextView tvNewsPubDate = findViewById(R.id.tvNewsPubDate);
        TextView tvNewsCategory = findViewById(R.id.tvNewsCategory);
        btnViewMore = findViewById(R.id.btnViewMore);
        btnSave = findViewById(R.id.btnSave);
        linearParent = findViewById(R.id.linearParent);
        data = (CBCNewsModel) getIntent().getSerializableExtra("article");
        title = data.getTitle();
        String desc = data.getDescription();
        String pubDate = data.getPubDate();
        String author = data.getAuthor();
        String category = data.getCategory();
        articleLink = data.getLink();
        tvNewsTitle.setText(title);
        tvNewsDesc.setText(desc);
        tvNewsPubDate.setText(pubDate);
        tvAuthName.setText(author);
        tvNewsCategory.setText(category);
        db = new CBCDB(CBCNewsDetailActivity.this);
        db.open();
        hasArticle(db);
    }

    /**
     * <p>Check if article has already been saved</p>
     */
    boolean hasArticle(CBCDB manager) {
        boolean isExists = manager.hasArticle(title);
        if (isExists) {
            String removeText = getResources().getString(R.string.cbc_remove_article);
            btnSave.setText(removeText);
        } else {
            String saveText = getResources().getString(R.string.cbc_save_article);
            btnSave.setText(saveText);
        }
        return isExists;
    }

    @Override
    protected void onStop() {
        super.onStop();
        //closing database connection
        db.close();
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
                startActivity(new Intent(CBCNewsDetailActivity.this, CBCStatsActivity.class));
                break;
            case R.id.menuHelp:
                showHelpDialog();
                break;
        }
        return true;
    }


    /**
     * <p>Showing custom layout HELP dialog</p>
     */
    void showHelpDialog() {
        final Dialog dialog = new Dialog(CBCNewsDetailActivity.this);
        dialog.setTitle(getResources().getString(R.string.cbc_help));
        //setting layout to dialog
        dialog.setContentView(R.layout.cbc_dialog_detail_help);
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
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);

        dialog.setCancelable(true);
        dialog.show();
    }
}
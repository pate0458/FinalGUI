package com.prog.gui.guiprogramming.food_nut_db;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.prog.gui.guiprogramming.R;

public class FoodDetailActivity extends Activity {

    LinearLayout linearDetails;
    Button btnAddToFav;
    String foodID, title, categoryLabel, category, tag;
    double energy;
    boolean isExists;
    String message = "";

    EditText edtTag;
    Dialog dialog;
    FoodDB dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_activity_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
        Intent intent = getIntent();
        FoodModel foodModel = (FoodModel) intent.getSerializableExtra("food");
        linearDetails = findViewById(R.id.linearDetails);
        TextView tvRowFoodTitle = findViewById(R.id.tvRowFoodTitle);
        TextView tvRowFoodCategory = findViewById(R.id.tvRowFoodCategory);
        TextView tvFoodEnergy = findViewById(R.id.tvFoodEnergy);
        TextView tvFoodTag = findViewById(R.id.tvFoodTag);
        btnAddToFav = findViewById(R.id.btnAddToFav);
        foodID = foodModel.getFoodId();
        title = foodModel.getLabel();
        categoryLabel = foodModel.getCategoryLabel();
        category = foodModel.getCategory();
        String categoryText = foodModel.getCategory() + " (" + categoryLabel + ")";
        energy = foodModel.getEnergy();
        tag = foodModel.getTag();
        String energyString = energy + "kcal";
        tvRowFoodTitle.setText(title);
        tvRowFoodCategory.setText(categoryText);
        tvFoodEnergy.setText(energyString);
        if (tag != null && tag.trim().length() != 0) {
            String tagToSet = "tag: " + tag;
            tvFoodTag.setText(tagToSet);
        } else {
            tvFoodTag.setVisibility(View.GONE);
        }

        dbManager = new FoodDB(FoodDetailActivity.this);
        dbManager.open();
        updateButtonText(dbManager);
        dbManager.close();

        btnAddToFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbManager.open();
                if (isExists) {
                    //FoodModel exists, remove it
                    if (dbManager.removeFavorite(foodID)) {
                        message = getResources().getString(R.string.food_msg_removed);
                        setResult(1);
                    } else {
                        message = getResources().getString(R.string.food_msg_not_removed);
                    }
                    showSnackBar(message);
                    updateButtonText(dbManager);
                    dbManager.close();
                } else {
                    //FoodModel not exists, insert it
                    dialog = new Dialog(FoodDetailActivity.this);
                    //We do not need title for custom dialog
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.food_dialog_tag);
                    edtTag = dialog.findViewById(R.id.edtTag);
                    edtTag.setText(title);
                    Button btnCancel = dialog.findViewById(R.id.btnCancel);
                    Button btnSet = dialog.findViewById(R.id.btnSet);
                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.cancel();
                        }
                    });
                    btnSet.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String text = edtTag.getText().toString().trim();
                            if (text.length() != 0) {
                                if (dbManager.addToFav(foodID, title, energy,
                                        category, categoryLabel, text)
                                        != -1) {
                                    message = getResources().getString(R.string.food_msg_added);
                                } else {
                                    message = getResources().getString(R.string.food_msg_not_added);
                                }
                            } else {
                                Toast.makeText(FoodDetailActivity.this, "Enter TAG to set!", Toast.LENGTH_SHORT).show();
                            }
                            dialog.cancel();
                            showSnackBar(message);
                            updateButtonText(dbManager);
                            dbManager.close();
                        }
                    });
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
        });
    }

    void updateButtonText(FoodDB dbManager) {
        isExists = dbManager.hasFood(foodID);
        if (isExists) {
            btnAddToFav.setText(getResources().getString(R.string.food_remove_from_favorites));
        } else {
            btnAddToFav.setText(getResources().getString(R.string.food_add_to_string_favorites));
        }
    }

    void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(linearDetails, message, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

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
                startActivity(new Intent(FoodDetailActivity.this, FoodStatsActivity.class));
                break;
            case R.id.menuHelp:
                showHelpDialog();
                break;
            //handling onclick of actionBar back button
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }


    /**
     * Showing a help dialog to user
     */
    void showHelpDialog() {
        final Dialog dialog = new Dialog(FoodDetailActivity.this);
        dialog.setTitle(getResources().getString(R.string.food_help));
        //setting layout to dialog
        dialog.setContentView(R.layout.food_dialog_detail_help);
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

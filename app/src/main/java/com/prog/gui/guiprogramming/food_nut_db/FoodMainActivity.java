package com.prog.gui.guiprogramming.food_nut_db;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
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

public class FoodMainActivity extends Activity {

    Button btnFav;

    ArrayAdapter<String> adapter;
    ArrayList<FoodModel> mRowData;
    ArrayList<String> results;
    ListView lvResults;
    SearchView svMovieInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_activity_main);

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

        btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FoodMainActivity.this, FoodFavActivity.class));
            }
        });

        svMovieInput = findViewById(R.id.searchFood);
        lvResults = findViewById(R.id.lvResults);
        mRowData = new ArrayList<>();
        results = new ArrayList<>();
        adapter = new ArrayAdapter<>(FoodMainActivity.this, android.R.layout.simple_list_item_1, results);
        lvResults.setAdapter(adapter);
        svMovieInput.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //Called when user hits Search Icon on keyboard
            @Override
            public boolean onQueryTextSubmit(String query) {
                String url = "https://api.edamam.com/api/food-database/parser" +
                        "?app_id=4b5a1e88&app_key=94958fe9dfc9cedc8276f77eaab7f5e2" +
                        "&ingr=" + (query.replaceAll(" ", "%20"));
                new FoodSearchAsync(url).execute();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        lvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(FoodMainActivity.this, FoodDetailActivity.class)
                        //passing FoodModel class object as Serializable
                        .putExtra("food", mRowData.get(position)));
            }
        });
    }

    /**
     * Binding menu to activity
     * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * Handling click event of menu items
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menuStat:
                startActivity(new Intent(FoodMainActivity.this, FoodStatsActivity.class));
                break;
            case R.id.menuHelp:
                showHelpDialog();
                break;
        }
        return true;
    }


    /**
     * Showing a help dialog to user
     * */
    void showHelpDialog() {
        final Dialog dialog = new Dialog(FoodMainActivity.this);
        //We do not need title for custom dialog
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //setting layout to dialog
        dialog.setContentView(R.layout.food_dialog_home_help);
//        initializing widgets of custom dialog layout
        Button btnOk = dialog.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

//        showing dialog with MATCH_PARENT width.
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);

        dialog.setCancelable(true);
        dialog.show();
    }

    class FoodSearchAsync extends AsyncTask<Void, Integer, String> {

        String mURL;
        ProgressDialog dialog = new ProgressDialog(FoodMainActivity.this);

        public FoodSearchAsync(String mURL) {
            this.mURL = mURL;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String message = getResources().getString(R.string.food_app_please_wait);
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
                JSONObject jsonObject = new JSONObject(response);
                mRowData.clear();
                results.clear();
                JSONArray parsed = jsonObject.getJSONArray("parsed");
                if (parsed.length() > 0) {
                    for (int i = 0; i < parsed.length(); i++) {
                        JSONObject food = parsed.getJSONObject(i).getJSONObject("food");
                        String foodId = food.getString("foodId");
                        String label = food.getString("label");
                        String category = food.getString("category");
                        String categoryLabel = food.getString("categoryLabel");
                        JSONObject nutrients = food.getJSONObject("nutrients");
                        double energy = 0;
                        //safe checking for nutrients
                        if (nutrients.has("ENERC_KCAL"))
                            energy = nutrients.getDouble("ENERC_KCAL");
                        results.add(label);
                        FoodModel data = new FoodModel();
                        data.setFoodId(foodId);
                        data.setLabel(label);
                        data.setCategory(category);
                        data.setCategoryLabel(categoryLabel);
                        data.setEnergy(energy);
                        mRowData.add(data);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(FoodMainActivity.this, "Item not found!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}


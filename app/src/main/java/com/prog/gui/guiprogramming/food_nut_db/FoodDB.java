package com.prog.gui.guiprogramming.food_nut_db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;


//To communicate with SQLite, here common instance of this class is useful
class FoodDB {

    //instance of a subclass
    private DBHelper dbHelper;
    //context from which the connection is establishing
    private Context context;
    //instance of SQLite database
    private SQLiteDatabase database;

    /**
     * <p>Constructor to initialize context value</p>
     * */
    FoodDB(Context context) {
        this.context = context;
    }

    /**
     * <p>Opens sqlite database connection</p>
     * */
    void open() throws SQLException {
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    /**
     * <p>Closes sqlite database connection</p>
     * */
    void close() {
        dbHelper.close();
    }

    /**
     *
     * <p>Inserts a row into FOOD table</p>
     *
     * @param foodID        unique ID of a food
     * @param foodLabel     label of a food
     * @param energy    calories of a food - unit: (kcal), provided as ENERC_KCAL in the API
     * @param category      category of a food, provided as category in the API
     * @param categoryLabel category-label of a food, provided as categoryLabel in the API
     * @param tag           entered by user
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    long addToFav(String foodID, String foodLabel,
                         double energy,
                         String category, String categoryLabel, String tag) {
        //Map all the parameters with it's appropriate column name
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.FOOD_ID, foodID);
        cv.put(DBHelper.FOOD_LABEL, foodLabel);
        cv.put(DBHelper.FOOD_ENERGY, energy);
        cv.put(DBHelper.FOOD_CATEGORY, category);
        cv.put(DBHelper.FOOD_CATEGORY_LABEL, categoryLabel);
        cv.put(DBHelper.FOOD_TAG, tag);
        return database.insert(DBHelper.TABLE_FOOD, null, cv);
    }

    /**
     * <p>can be used to retrieve all the saved food from the database</p>
     * @return ArrayList<FoodModel> a list of FoodModel class
     *          FoodModel class is used to hold each row value
     * */
    ArrayList<FoodModel> getFavoritesList() {
        ArrayList<FoodModel> mRowData = new ArrayList<>();

        //Column names to retrieve data
        String[] columns = new String[]{DBHelper.FOOD_ID, DBHelper.FOOD_LABEL,
                DBHelper.FOOD_CATEGORY, DBHelper.FOOD_CATEGORY_LABEL,
                DBHelper.FOOD_ENERGY, DBHelper.FOOD_TAG};
        //Getting results
        Cursor cursor = database.query(DBHelper.TABLE_FOOD, columns, null,
                null, null, null, null);

        //if cursor got single entry
        if (cursor.moveToFirst()) {
            do {
                //Cursor has data, fetching it
                String foodID = cursor.getString(cursor.getColumnIndex(DBHelper.FOOD_ID));
                String foodLabel = cursor.getString(cursor.getColumnIndex(DBHelper.FOOD_LABEL));
                String foodCategory = cursor.getString(cursor.getColumnIndex(DBHelper.FOOD_CATEGORY));
                String foodCategoryLabel = cursor.getString(cursor.getColumnIndex(DBHelper.FOOD_CATEGORY_LABEL));
                double foodEnergy = cursor.getDouble(cursor.getColumnIndex(DBHelper.FOOD_ENERGY));
                String foodTag = cursor.getString(cursor.getColumnIndex(DBHelper.FOOD_TAG));

                //Set all the data into a single instance of FoodModel
                FoodModel foodModel = new FoodModel();
                foodModel.setFoodId(foodID);
                foodModel.setLabel(foodLabel);
                foodModel.setCategory(foodCategory);
                foodModel.setCategoryLabel(foodCategoryLabel);
                foodModel.setTag(foodTag);
                foodModel.setEnergy(foodEnergy);

                //collect all the instances to an ArrayList
                mRowData.add(foodModel);
            } while (cursor.moveToNext()); //while cursor can move to next record
        }
        cursor.close();
        return mRowData;
    }

    /**
     * <p>to check the database table has particular ID food or not</p>
     * @param foodID ID of food to check record
     * @return true if associated title is exists in FOOD table, returns false otherwise
     * */
    boolean hasFood(String foodID) {
        String[] columns = new String[]{DBHelper.FOOD_ID};
        Cursor cursor = database.query(DBHelper.TABLE_FOOD, columns, DBHelper.FOOD_ID + " = ?",
                new String[]{foodID}, null, null, null);
        boolean isExists = cursor.moveToFirst();
        cursor.close();
        return isExists;
    }

    /**
     * <p>Deletes a row from CBC_NEWS table</p>
     * @param foodID ID of food to check record
     * @return true if row deleted successfully, else it will return false
     * */
    boolean removeFavorite(String foodID) {
        return database.delete(DBHelper.TABLE_FOOD, DBHelper.FOOD_ID + "=?", new String[]{foodID}) > 0;
    }

    /**
     * @return no. of records stored in database, returns 0 if no data is stored in DB
     */
    int getCount() {
        int count = 0;
        Cursor cursor = database.rawQuery("SELECT count(*) FROM " + DBHelper.TABLE_FOOD, null);
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    /**
     * @return SUM of calories
     */
    double getTotalCalories() {
        double total = 0;
        Cursor cursor = database.rawQuery("SELECT sum(" + DBHelper.FOOD_ENERGY + ") FROM " + DBHelper.TABLE_FOOD, null);
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }
        cursor.close();
        return total;
    }

    /**
     * @return Minimum calorie value from FOOD table
     */
    double getMinCalories() {
        double min = 0;
        Cursor cursor = database.rawQuery("SELECT min(" + DBHelper.FOOD_ENERGY + ") FROM " + DBHelper.TABLE_FOOD, null);
        if (cursor.moveToFirst()) {
            min = cursor.getDouble(0);
        }
        cursor.close();
        return min;
    }

    /**
     * @return Average calorie value from FOOD table
     */
    double getAvgCalories() {
        double avg = 0;
        Cursor cursor = database.rawQuery("SELECT avg(" + DBHelper.FOOD_ENERGY + ") FROM " + DBHelper.TABLE_FOOD, null);
        if (cursor.moveToFirst()) {
            avg = cursor.getDouble(0);
        }
        cursor.close();
        return avg;
    }

    /**
     * @return Maximum calorie value from FOOD table
     */
    double getMaxCalories() {
        double max = 0;
        Cursor cursor = database.rawQuery("SELECT max(" + DBHelper.FOOD_ENERGY + ") FROM " + DBHelper.TABLE_FOOD, null);
        if (cursor.moveToFirst()) {
            max = cursor.getDouble(0);
        }
        cursor.close();
        return max;
    }

    //Database open helper class
    class DBHelper extends SQLiteOpenHelper {

        static final String DATABASE_NAME = "FOOD_DB";
        static final String TABLE_FOOD = "FOOD";
        static final String FOOD_ID = "foodId";
        static final String FOOD_LABEL = "foodLabel";
        static final String FOOD_ENERGY = "foodEnergy";
        static final String FOOD_CATEGORY = "foodCategory";
        static final String FOOD_CATEGORY_LABEL = "foodCategoryLabel";
        static final String FOOD_TAG = "foodTAG";

        DBHelper(Context context) {
            super(context, DATABASE_NAME, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(
                    "CREATE TABLE " + TABLE_FOOD + " (" + FOOD_ID + " text primary key, " +
                            FOOD_LABEL + " text," +
                            FOOD_ENERGY + " real," +
                            FOOD_CATEGORY + " real," +
                            FOOD_CATEGORY_LABEL + " real," +
                            FOOD_TAG + " text"
                            + ")"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i1) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOOD);
            onCreate(db);
        }
    }
}

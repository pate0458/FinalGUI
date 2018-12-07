package com.prog.gui.guiprogramming.cbc_news_reader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

//To communicate with SQLite, here common instance of this class is useful
class CBCDB {

    //instance of a subclass
    private DBHelper dbHelper;
    //context from which the connection is establishing
    private Context context;
    //instance of SQLite database
    private SQLiteDatabase database;

    /**
     * <p>Constructor to initialize context value</p>
     * */
    CBCDB(Context context) {
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
     * <p>can be used to retrieve all the articles from the database</p>
     * @return ArrayList<CBCNewsModel> a list of CBCNewsModel class
     *          CBCNewsModel class is used to hold each row value
     * */
    ArrayList<CBCNewsModel> getAllArticles() {
        ArrayList<CBCNewsModel> data = new ArrayList<>();

        //Column names to retrieve data
        String columns[] = {DBHelper.CBC_NEWS_TITLE, DBHelper.CBC_NEWS_DESC,
                DBHelper.CBC_NEWS_LINK, DBHelper.CBC_NEWS_AUTHOR,
                DBHelper.CBC_NEWS_CATEGORY, DBHelper.CBC_NEWS_PUB_DATE};
        //Getting results
        Cursor cursor = database.query(DBHelper.CBC_TABLE_NEWS, columns, null,
                null, null, null, null);

        //if cursor got single entry
        if (cursor.moveToFirst()) {
            do {
                //Cursor has data, fetching it
                String title = cursor.getString(cursor.getColumnIndex(DBHelper.CBC_NEWS_TITLE));
                String desc = cursor.getString(cursor.getColumnIndex(DBHelper.CBC_NEWS_DESC));
                String link = cursor.getString(cursor.getColumnIndex(DBHelper.CBC_NEWS_LINK));
                String author = cursor.getString(cursor.getColumnIndex(DBHelper.CBC_NEWS_AUTHOR));
                String pubDate = cursor.getString(cursor.getColumnIndex(DBHelper.CBC_NEWS_PUB_DATE));
                String category = cursor.getString(cursor.getColumnIndex(DBHelper.CBC_NEWS_CATEGORY));

                //Set all the data into a single instance of CBCNewsModel
                CBCNewsModel model = new CBCNewsModel();
                model.setTitle(title);
                model.setDescription(desc);
                model.setLink(link);
                model.setAuthor(author);
                model.setPubDate(pubDate);
                model.setCategory(category);

                //collect all the instances to an ArrayList
                data.add(model);
            } while (cursor.moveToNext()); //while cursor can move to next record
        }
        //closing cursor
        cursor.close();
        return data;
    }

    /**
     * <p>Inserts a row into CBC_NEWS table</p>
     * @param data collection of all the information into a single instance
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     * */
    long saveArticle(CBCNewsModel data) {
        ContentValues cv = new ContentValues();

        //reading all the records individually and mapping with column names
        cv.put(DBHelper.CBC_NEWS_TITLE, data.getTitle());
        cv.put(DBHelper.CBC_NEWS_DESC, data.getDescription());
        cv.put(DBHelper.CBC_NEWS_CATEGORY, data.getCategory());
        cv.put(DBHelper.CBC_NEWS_LINK, data.getLink());
        cv.put(DBHelper.CBC_NEWS_PUB_DATE, data.getPubDate());
        cv.put(DBHelper.CBC_NEWS_AUTHOR, data.getAuthor());

        //inserting a row into a database
        return database.insert(DBHelper.CBC_TABLE_NEWS, null, cv);
    }

    /**
     * <p>Deletes a row from CBC_NEWS table</p>
     * @param title title of an article to be removed
     * @return true if row deleted successfully, else it will return false
     * */
    boolean removeArticle(String title) {
        return database.delete(DBHelper.CBC_TABLE_NEWS, DBHelper.CBC_NEWS_TITLE + "=?"
                , new String[]{title}) > 0;
    }

    /**
     * <p>to check the database table has particular titled article or not</p>
     * @param title Title of an article to check record
     * @return true if associated title is exists in CBC_NEWS table, returns false otherwise
     * */
    boolean hasArticle(String title) {
        String[] columns = new String[]{DBHelper.CBC_NEWS_TITLE};
        Cursor cursor = database.query(DBHelper.CBC_TABLE_NEWS, columns,
                DBHelper.CBC_NEWS_TITLE + " = ?",
                new String[]{title}, null, null, null);
        boolean isExists = cursor.moveToFirst();
        cursor.close();
        return isExists;
    }

    /**
     * <p>Getting number of row inserted into a CBC_NEWS table,
     * If no records are inserted into table then, it will return 0</p>
     * @return no of records in CBC_NEWS table
     * */
    int getCount() {
        int count = 0;
        Cursor cursor = database.rawQuery("SELECT count(*) FROM " + DBHelper.CBC_TABLE_NEWS,
                null);
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    /**
     * <p>To get minimum words for an article from CBC_NEWS table</p>
     * @return min word count of an article among all the stored articles
     * */
    int getMinWords() {
        int minWords = 0;
        Cursor cursor = database.rawQuery("SELECT min(length(" + DBHelper.CBC_NEWS_DESC + ") - " +
                        "length(replace(" + DBHelper.CBC_NEWS_DESC + ", ' ', '')) + 1) FROM "
                        + DBHelper.CBC_TABLE_NEWS,
                null);
        if (cursor.moveToFirst()) {
            minWords = cursor.getInt(0);
        }
        cursor.close();
        return minWords;
    }

    /**
     * <p>To get maximum words for an article from CBC_NEWS table</p>
     * @return max word count of an article among all the stored articles
     * */
    int getMaxWords() {
        int maxWords = 0;
        Cursor cursor = database.rawQuery("SELECT max(length(" + DBHelper.CBC_NEWS_DESC + ") - " +
                        "length(replace(" + DBHelper.CBC_NEWS_DESC + ", ' ', '')) + 1) FROM "
                        + DBHelper.CBC_TABLE_NEWS,
                null);
        if (cursor.moveToFirst()) {
            maxWords = cursor.getInt(0);
        }
        cursor.close();
        return maxWords;
    }

    /**
     * <p>To get average words of articles from CBC_NEWS table</p>
     * @return avg word count of an article among all the stored articles
     * */
    double getAvgWords() {
        double maxWords = 0;
        Cursor cursor = database.rawQuery("SELECT avg(length(" + DBHelper.CBC_NEWS_DESC + ") - " +
                        "length(replace(" + DBHelper.CBC_NEWS_DESC + ", ' ', '')) + 1) FROM "
                        + DBHelper.CBC_TABLE_NEWS,
                null);
        if (cursor.moveToFirst()) {
            maxWords = cursor.getDouble(0);
        }
        cursor.close();
        return maxWords;
    }

    //Database open helper class
    class DBHelper extends SQLiteOpenHelper {

        static final String CBC_DATABASE_NAME = "NEWS_DB";
        static final String CBC_TABLE_NEWS = "CBC_NEWS";
        static final String CBC_NEWS_TITLE = "cbc_newsTitle";
        static final String CBC_NEWS_DESC = "cbc_newsDesc";
        static final String CBC_NEWS_LINK = "cbc_newsLink";
        static final String CBC_NEWS_PUB_DATE = "cbc_newsPubDate";
        static final String CBC_NEWS_CATEGORY = "cbc_newsCategory";
        static final String CBC_NEWS_AUTHOR = "cbc_newsAuthor";

        DBHelper(Context context) {
            super(context, CBC_DATABASE_NAME, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //Creating table on first start of an app
            String SQL = "CREATE TABLE " + CBC_TABLE_NEWS + " (" + CBC_NEWS_TITLE + " text primary key, " +
                    CBC_NEWS_DESC + " text," +
                    CBC_NEWS_LINK + " text," +
                    CBC_NEWS_PUB_DATE + " text," +
                    CBC_NEWS_CATEGORY + " text," +
                    CBC_NEWS_AUTHOR + " text"
                    + ")";
            db.execSQL(SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i1) {
            db.execSQL("DROP TABLE IF EXISTS " + CBC_TABLE_NEWS);
            onCreate(db);
        }
    }
}

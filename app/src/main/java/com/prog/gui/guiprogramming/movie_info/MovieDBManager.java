package com.prog.gui.guiprogramming.movie_info;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
public class MovieDBManager {
    private DBHelper dbHelper;
    private Context context;

    private SQLiteDatabase database;

    /**
     * @param context - context of a current activity,
     *                used to establish SQLite database connection
     */
    public MovieDBManager(Context context) {
        this.context = context;
    }

    /**
     * This method helps you to establish connection SQLite database, and
     * to store/update current data
     */
    public void open() throws SQLException {
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    /**
     * This method closes the connection to SQLite database
     */
    public void close() {
        dbHelper.close();
    }

    /**
     * <p>addMovie() adds the information about a particular movie.
     * SQLiteDatabase has to be opened as writable database to perform
     * this operation </p>
     *
     * @param imdBID  a unique ID of a movie
     * @param title   name of a movie
     * @param poster  a poster of a movie, pass only a name of a 'poster' file with extension
     * @param year    year of a movie
     * @param plot    plot of a movie
     * @param type    typeof a movie - (Movie or a TV show)
     * @param lang    language of a movie (in case of multiple languages,
     *                has to passed by concatenating with a ',' (comma) to a single string
     *                i.e. Languages are English and Spanish then, it has to be passed like
     *                Lang = "English, Spanish" )
     * @param rating  IMDb rating of a movie
     * @param genre   genre of a movie
     * @param country country of a movie
     * @param runtime IMDb rating of a movie
     * @return row ID if insertion success, otherwise -1
     * @see MovieDBManager#open()
     * @see SQLiteDatabase#insert(String, String, ContentValues)
     */
    public long addMovie(String imdBID, String title, String poster,
                         String year, String plot, String type,
                         String lang, String rating, String genre,
                         int runtime, String country) {
        //Map all the parameters with it's appropriate column name
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.MOVIE_ID, imdBID);
        cv.put(DBHelper.MOVIE_TITLE, title);
        cv.put(DBHelper.MOVIE_POSTER, poster);
        cv.put(DBHelper.MOVIE_YEAR, year);
        cv.put(DBHelper.MOVIE_PLOT, plot);
        cv.put(DBHelper.MOVIE_TYPE, type);
        cv.put(DBHelper.MOVIE_LANG, lang);
        cv.put(DBHelper.MOVIE_RATING, rating);
        cv.put(DBHelper.MOVIE_GENRE, genre);
        cv.put(DBHelper.MOVIE_RUNTIME, runtime);
        cv.put(DBHelper.MOVIE_COUNTRY, country);
        return database.insert(DBHelper.TABLE_MOVIE, null, cv);
    }

    /**
     * <p>getFavoritesList() retrieves short information about a movie from SQLiteDatabase.
     * SQLiteDatabase has to be opened as readable/writable database to perform.</p>
     *
     * @return ArrayList, retrieves all the movies' short description, added to favorites.
     * @see MovieDBManager#open()
     */
    public ArrayList<Movie> getFavoritesList() {
        //column names, to retrieve information
        String[] columns = new String[]{DBHelper.MOVIE_ID, DBHelper.MOVIE_TITLE,
                DBHelper.MOVIE_POSTER, DBHelper.MOVIE_YEAR, DBHelper.MOVIE_TYPE};

        Cursor cursor = database.query(DBHelper.TABLE_MOVIE, columns, null,
                null, null, null, null);

        //To store multiple Movie class objects
        ArrayList<Movie> mRowData = new ArrayList<>();
        //check if cursor has the data and it can read it.
        if (cursor.moveToFirst()) {
            do {
                String movieID = cursor.getString(cursor.getColumnIndex(DBHelper.MOVIE_ID));
                String movieTitle = cursor.getString(cursor.getColumnIndex(DBHelper.MOVIE_TITLE));
                String movieType = cursor.getString(cursor.getColumnIndex(DBHelper.MOVIE_TYPE));
                String movieYear = cursor.getString(cursor.getColumnIndex(DBHelper.MOVIE_YEAR));
                String moviePoster = cursor.getString(cursor.getColumnIndex(DBHelper.MOVIE_POSTER));
                //Storing each row details into Movie class's object
                Movie movie = new Movie();
                movie.setImdbID(movieID);
                movie.setTitle(movieTitle);
                movie.setType(movieType);
                movie.setYear(movieYear);
                movie.setPoster(moviePoster);
                //Add the Movie class object to an ArrayList
                mRowData.add(movie);
            } while (cursor.moveToNext()); //move current cursor to next row
        }
        cursor.close();
        return mRowData;
    }

    /**
     * <p>getMovieDetails() retrieves detailed information about a particular movie from SQLiteDatabase.
     * SQLiteDatabase has to be opened as readable/writable database to perform.</p>
     *
     * @param imdBID a unique ID of a movie
     * @return Movie class object
     * @see MovieDBManager#open()
     */
    public Movie getMovieDetails(String imdBID) {
        //column names, to retrieve information
        String[] columns = new String[]{DBHelper.MOVIE_ID, DBHelper.MOVIE_TITLE,
                DBHelper.MOVIE_POSTER, DBHelper.MOVIE_YEAR, DBHelper.MOVIE_TYPE,
                DBHelper.MOVIE_GENRE, DBHelper.MOVIE_RATING, DBHelper.MOVIE_PLOT,
                DBHelper.MOVIE_RUNTIME, DBHelper.MOVIE_COUNTRY};

        Cursor cursor = database.query(DBHelper.TABLE_MOVIE, columns, DBHelper.MOVIE_ID + " = ?",
                new String[]{imdBID}, null, null, null);
        Movie movie = new Movie();
        if (cursor.moveToFirst()) {
            do {
                //Reading a cursor
                String movieID = cursor.getString(cursor.getColumnIndex(DBHelper.MOVIE_ID));
                String movieTitle = cursor.getString(cursor.getColumnIndex(DBHelper.MOVIE_TITLE));
                String movieType = cursor.getString(cursor.getColumnIndex(DBHelper.MOVIE_TYPE));
                String movieYear = cursor.getString(cursor.getColumnIndex(DBHelper.MOVIE_YEAR));
                String moviePoster = cursor.getString(cursor.getColumnIndex(DBHelper.MOVIE_POSTER));
                String genre = cursor.getString(cursor.getColumnIndex(DBHelper.MOVIE_GENRE));
                String rating = cursor.getString(cursor.getColumnIndex(DBHelper.MOVIE_RATING));
                String plot = cursor.getString(cursor.getColumnIndex(DBHelper.MOVIE_PLOT));
                int runtime = cursor.getInt(cursor.getColumnIndex(DBHelper.MOVIE_RUNTIME));
                String country = cursor.getString(cursor.getColumnIndex(DBHelper.MOVIE_COUNTRY));
                //Storing row details into Movie class's object
                movie.setImdbID(movieID);
                movie.setTitle(movieTitle);
                movie.setType(movieType);
                movie.setYear(movieYear);
                movie.setPoster(moviePoster);
                movie.setGenre(genre);
                movie.setRating(rating);
                movie.setPlot(plot);
                movie.setCountry(country);
                movie.setRuntime(runtime);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return movie;
    }

    /**
     * <p>isExists() checks whether a particular movie exists or not.
     * SQLiteDatabase has to be opened as readable/writable database to perform.
     * It has to be called to display a particular button to user
     * i.e.  if movie exists in database, show "Remove from favorites",
     * otherwise show "Add to favorites"
     * </p>
     *
     * @param imdBID a unique ID of a movie
     * @return true if movie exists, otherwise false
     * @see MovieDBManager#open()
     */
    public boolean isExists(String imdBID) {
        String[] columns = new String[]{DBHelper.MOVIE_ID};
        Cursor cursor = database.query(DBHelper.TABLE_MOVIE, columns, DBHelper.MOVIE_ID + " = ?",
                new String[]{imdBID}, null, null, null);
        boolean isExists = cursor.moveToFirst();
        cursor.close();
        return isExists;
    }

    /**
     * <p>removeFavorite() deletes a row from the database.
     * SQLiteDatabase has to be opened as writable database to perform
     * this operation </p>
     *
     * @param imdBID a unique ID of a movie, to be deleted
     * @return true if movie deleted, otherwise false
     */
    public boolean removeFavorite(String imdBID) {
        return database.delete(DBHelper.TABLE_MOVIE, DBHelper.MOVIE_ID + "=?", new String[]{imdBID}) > 0;
    }

    /**
     * @return no. of records stored in database, returns 0 if no data is stored in DB
     * */
    public int getCount() {
        int count = 0;
        Cursor cursor = database.rawQuery("SELECT count(*) FROM " + DBHelper.TABLE_MOVIE, null);
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    /**
     * <p>getShortestRunTimeMovie used to retrieve shortest runtime from the saved movies</p>
     * @return minimum runtime among all the stored movies
     * */
    public float getShortestRunTimeMovie() {
        float runTime = 0.0f;
        String sql = "SELECT min(" + DBHelper.MOVIE_RUNTIME + ") FROM " + DBHelper.TABLE_MOVIE;
        Cursor cursor = database.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            runTime = cursor.getFloat(0);
        }
        cursor.close();
        return runTime;
    }

    /**
     * <p>getAvgRunTimeMovie used to retrieve shortest runtime from the saved movies</p>
     * @return average runtime of all the stored movies
     * */
    public float getAvgRunTimeMovie() {
        float runTime = 0.0f;
        String sql = "SELECT avg(" + DBHelper.MOVIE_RUNTIME + ") FROM " + DBHelper.TABLE_MOVIE;
        Cursor cursor = database.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            runTime = cursor.getFloat(0);
        }
        cursor.close();
        return runTime;
    }

    /**
     * <p>getLongestRunTimeMovie used to retrieve shortest runtime from the saved movies</p>
     * @return longest runtime among all the stored movies
     * */
    public float getLongestRunTimeMovie() {
        float runTime = 0.0f;
        String sql = "SELECT max(" + DBHelper.MOVIE_RUNTIME + ") FROM " + DBHelper.TABLE_MOVIE;
        Cursor cursor = database.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            runTime = cursor.getFloat(0);
        }
        cursor.close();
        return runTime;
    }

    class DBHelper extends SQLiteOpenHelper {

        //Database name
        static final String DATABASE_NAME = "MOVIE_DB";
        //table name, which will be created later
        static final String TABLE_MOVIE = "MOVIE";

        //Declaration of column names of MOVIE table
        static final String MOVIE_ID = "imdbID";
        static final String MOVIE_TITLE = "Title";
        static final String MOVIE_POSTER = "Poster";
        static final String MOVIE_YEAR = "Year";
        static final String MOVIE_TYPE = "Type";
        static final String MOVIE_PLOT = "Plot";
        static final String MOVIE_LANG = "Language";
        static final String MOVIE_RATING = "Rating";
        static final String MOVIE_GENRE = "Genre";
        static final String MOVIE_RUNTIME = "Runtime";
        static final String MOVIE_COUNTRY = "Country";
        //Declaration of column names of MOVIE table ends here

        /**
         * SQLiteDatabase open helper class constructor
         *
         * @param context required to handle SQLite database connection
         */
        DBHelper(Context context) {
            super(context, DATABASE_NAME, null, 1);
        }

        /**
         * onCreate() will be called once
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(
                    "CREATE TABLE " + TABLE_MOVIE + " (" + MOVIE_ID + " text primary key, " +
                            MOVIE_TITLE + " text," +
                            MOVIE_POSTER + " text," +
                            MOVIE_YEAR + " text," +
                            MOVIE_PLOT + " text," +
                            MOVIE_TYPE + " text," +
                            MOVIE_LANG + " text," +
                            MOVIE_RATING + " text," +
                            MOVIE_GENRE + " text," +
                            MOVIE_RUNTIME + " integer," +
                            MOVIE_COUNTRY + " text"
                            + ")"
            );
        }

        /**
         * onUpgrade() will be called if we increment
         * the current database version number
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i1) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOVIE);
            onCreate(db);
        }
    }
}

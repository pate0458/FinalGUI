package com.prog.gui.guiprogramming.movie_info;

import android.app.Activity;
import android.widget.Toast;

/**
 * MovieAppHelper contains constants and helper methods which can be useful anywhere in module
 * */
public class MovieAppHelper {

    public static void showToast(Activity activity, String message){
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
    }

    public static final String INTENT_PASS_MOVIE_ID = "MOVIE_ID";
    public static final String INTENT_PASS_TYPE = "TYPE";
    public static final String INTENT_PASS_TYPE_LOCAL = "LOCAL";
    public static final String INTENT_PASS_TYPE_SERVER = "SERVER";

    public static final int RESULT_MOVIE_DELETED = 3;

}

package com.imaginetventures.masterkey.utils.SharePreferrence;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Sanjay on 3/24/16.
 */
public class Session {
    private String TAG = "Session";
    private static final String PREF_NAME = "MasterKey";
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String USER_ID = "User_id";
    private static final String EMAIL_ID = "Email_id";
    private static final String BRANCH_ID = "Branch_id";
    private static final String USER_TYPE = "User_type";
    private static final String FIRST_NAME = "First_name";
    private static final String LAST_NAME = "Last_name";


    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;

    public Session(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createSession(JSONObject logincheck) {
        try {
            editor.putBoolean(IS_LOGIN, true);
            editor.putString(USER_ID, logincheck.getString(USER_ID));
            editor.putString(EMAIL_ID, logincheck.getString(EMAIL_ID));
            editor.putString(BRANCH_ID, logincheck.getString(BRANCH_ID));
            editor.putString(USER_TYPE, logincheck.getString(USER_TYPE));
            editor.putString(FIRST_NAME, logincheck.getString(FIRST_NAME));
            editor.putString(LAST_NAME, logincheck.getString(LAST_NAME));
            editor.commit();
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public String getEmailId() {
        return pref.getString(EMAIL_ID, "");
    }

    public String getUserId() {
        return pref.getString(USER_ID, "");
    }

    public String getName() {
        return pref.getString(FIRST_NAME, "").trim() + " " + pref.getString(LAST_NAME, "").trim();
    }

    public String getBranchId() {
        return pref.getString(BRANCH_ID, "");
    }

    public boolean isLogin() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    public void clearSession() {
        editor.clear();
        editor.commit();
    }
}

package ru.loftschool.loftblogmoneytracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.activeandroid.ActiveAndroid;

public class MoneyTrackerApplication extends com.activeandroid.app.Application {

    private final static String TOKEN_KEY = "token_key";
    public final static String DEFAULT_TOKEN_KEY = "1";


    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
    }

    public static void setToken(Context context, String token) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(TOKEN_KEY, token);
        editor.commit();
    }

    public static String getToken(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        return pref.getString(TOKEN_KEY, DEFAULT_TOKEN_KEY);
    }
}

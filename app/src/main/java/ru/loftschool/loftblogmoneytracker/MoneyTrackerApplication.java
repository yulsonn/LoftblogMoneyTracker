package ru.loftschool.loftblogmoneytracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.activeandroid.ActiveAndroid;

import ru.loftschool.loftblogmoneytracker.utils.TokenKeyStorage;

public class MoneyTrackerApplication extends com.activeandroid.app.Application implements TokenKeyStorage{

    public final static String USER_NAME = "user_name";
    public final static String DEFAULT_USER_NAME = "User";

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

    public static void setGoogleToken(Context context, String googleToken) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(TOKEN_GOOGLE_KEY, googleToken);
        editor.commit();
    }

    public static String getGoogleToken(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(TOKEN_GOOGLE_KEY, DEFAULT_TOKEN_GOOGLE_KEY);
    }

    public static void setUserName(Context context, String userName) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(USER_NAME, userName);
        editor.commit();
    }

    public static String getUserName(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(USER_NAME, DEFAULT_USER_NAME);
    }
}

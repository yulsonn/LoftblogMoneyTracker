package ru.loftschool.loftblogmoneytracker;

import com.activeandroid.ActiveAndroid;

public class MoneyTrackerApplication extends com.activeandroid.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
    }
}

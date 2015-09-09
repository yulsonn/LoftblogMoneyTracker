package ru.loftschool.loftblogmoneytracker;

import android.app.Application;

import com.raizlabs.android.dbflow.config.FlowManager;

public class MoneyTrackerApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        FlowManager.init(this);
    }
}

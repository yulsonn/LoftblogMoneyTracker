package ru.loftschool.loftblogmoneytracker.database;

import com.raizlabs.android.dbflow.annotation.Database;

@Database(name = AppDataBase.NAME, version = AppDataBase.VERSION, foreignKeysSupported = true)
public class AppDataBase {

    public static final String NAME = "money_tracker_db";
    public static  final int VERSION = 1;

}

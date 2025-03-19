package com.adithya.aaafexpensemanager.util;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;

/** @noinspection FieldCanBeLocal*/
public class DatabaseHelper extends SQLiteOpenHelper {
    private final Application context;
    private final File databaseFile;
    private final DataHelperSharedPrefs dataHelperSharedPrefs;
    public DatabaseHelper(Context context) {
        super(context, AppConstants.DATABASE_NAME, null, AppConstants.DATABASE_VERSION);
        this.context = (Application) context.getApplicationContext();
        this.databaseFile = context.getDatabasePath(AppConstants.DATABASE_NAME);
        this.dataHelperSharedPrefs = new DataHelperSharedPrefs(context);
    }
    public File getDatabaseFile() {
        return databaseFile;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        DBHelperActions.createActions(db);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("Database Helper","Started onUpgrade");
        onUpgradeOrDowngrade(db, oldVersion, newVersion);
    }
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("Database Helper","Started onDowngrade");
        onUpgradeOrDowngrade(db, oldVersion, newVersion);
    }
    private void onUpgradeOrDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        int currentDataBaseVersion = dataHelperSharedPrefs.getCurrentDataBaseVersion(oldVersion);
        if(currentDataBaseVersion< newVersion){
            dataHelperSharedPrefs.setCurrentDataBaseVersion(newVersion);
            DBHelperActions.dropActions(db);
            DBHelperActions.createActions(db);
        }
    }
}
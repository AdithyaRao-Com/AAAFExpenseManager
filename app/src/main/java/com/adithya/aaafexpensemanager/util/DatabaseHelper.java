package com.adithya.aaafexpensemanager.util;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;

/**
 * @noinspection FieldCanBeLocal
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private final Application context;
    private final File databaseFile;
    private final DBHelperSharedPrefs DBHelperSharedPrefs;
    public DatabaseHelper(Context context) {
        super(context, AppConstants.DATABASE_NAME, null, AppConstants.DATABASE_VERSION);
        this.context = (Application) context.getApplicationContext();
        this.databaseFile = context.getDatabasePath(AppConstants.DATABASE_NAME);
        this.DBHelperSharedPrefs = new DBHelperSharedPrefs(context);
    }

    public File getDatabaseFile() {
        return databaseFile;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        DBHelperActions.createActionsV1(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("Database Helper", "Started onUpgrade");
        DBHelperSharedPrefs.setCurrentDataBaseVersion(newVersion);
        if(oldVersion<2){
            DBHelperActions.dropCreateActionsV2(db);
        }
        if(oldVersion<3){
            DBHelperActions.dropCreateActionsV3(db);
        }
        if(oldVersion<4){
            DBHelperActions.dropCreateActionsV4(db);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("Database Helper", "Started onDowngrade");
        onUpgrade(db, oldVersion, newVersion);
    }
}
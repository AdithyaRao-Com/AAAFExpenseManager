package com.adithya.aaafexpensemanager.util;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;

public class DatabaseHelper extends SQLiteOpenHelper {
    private final Application context;
    private final File databaseFile;

    // Constructor for the live database
    public DatabaseHelper(Context context) {
        super(context, AppConstants.DATABASE_NAME, null, AppConstants.DATABASE_VERSION);
        this.context = (Application) context.getApplicationContext();
        this.databaseFile = context.getDatabasePath(AppConstants.DATABASE_NAME);
    }

    public DatabaseHelper(Context context, File file) {
        super(context, file.getAbsolutePath(), null, AppConstants.DATABASE_VERSION);
        this.context = (Application) context.getApplicationContext();
        this.databaseFile = file;
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
        // Handle database upgrades if needed.  Example:
//        if ((newVersion > oldVersion)||(newVersion==1)) {
//            Log.d("Database Helper","Dropping tables");
//            // Drop the view first if it exists
//            DBHelperActions.dropActions(db);
//            DBHelperActions.createActions(db);
//            if(AppConstants.IS_DEV_MODE){
//                SetupTestData.updateIsSetupData(
//                        (Application) context.getApplicationContext(),
//                        true);
//            }
//        }
    }


}
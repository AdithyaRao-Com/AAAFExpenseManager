package com.adithya.aaafexpensemanager.settings;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.adithya.aaafexpensemanager.util.DatabaseHelper;

import java.io.File;

public class SettingsRepository {
    private final SQLiteDatabase db;
    private final File databaseFile;
    public SettingsRepository(Application application){
        //noinspection resource
        DatabaseHelper dbHelper = new DatabaseHelper(application);
        this.databaseFile = dbHelper.getDatabaseFile();
        db = dbHelper.getWritableDatabase();
    }

    public File getDatabaseFile() {
        return databaseFile;
    }

    public String getSetting(String settingName){
        String query = "SELECT setting_value FROM setting_pairs WHERE setting_name = ?";
        String[] args = {settingName};
        try (Cursor cursor = db.rawQuery(query, args)) {
            if (cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndexOrThrow("setting_value"));
            }
        }
        return null;
    }
    public boolean setSetting(String settingName, String settingValue){
        String[] deleteArgs = {settingName};
        boolean status = false;
        try{
            db.beginTransaction();
            db.delete("setting_pairs", "setting_name = ?", deleteArgs);
            ContentValues values = new ContentValues();
            values.put("setting_name", settingName);
            values.put("setting_value", settingValue);
            db.insert("setting_pairs",null,values);
            db.setTransactionSuccessful();
            status=true;
        }
        catch (Exception e){
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
        finally {
            db.endTransaction();
        }
        return status;
    }
}

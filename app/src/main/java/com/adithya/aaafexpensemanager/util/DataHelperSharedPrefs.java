package com.adithya.aaafexpensemanager.util;

import android.content.Context;
import android.content.SharedPreferences;

public class DataHelperSharedPrefs {
    private static final String PREFS_NAME = "data_helper_prefs";
    private static final String current_data_base_version = "current_data_base_version";
    private final SharedPreferences sharedPreferences;

    public DataHelperSharedPrefs(Context context) {
        super();
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public int getCurrentDataBaseVersion(int defValue) {
        return this.sharedPreferences.getInt(current_data_base_version, defValue);
    }

    public void setCurrentDataBaseVersion(int value) {
        this.sharedPreferences.edit().putInt(current_data_base_version, value).apply();
    }

    public void clearSharedPreferences() {
        sharedPreferences.edit().clear().apply();
    }
}

package com.adithya.aaafexpensemanager.settings.autoBackup;

import android.content.Context;
import android.content.SharedPreferences;

public class AutoBackUpSharedPrefs {
    private static final String PREFS_NAME = "auto_backup_prefs";
    private static final String KEY_IS_AUTO_BACKUP_ENABLED = "is_auto_backup_enabled";
    private static final String KEY_AUTO_BACKUP_DIRECTORY = "auto_backup_directory";
    private final SharedPreferences sharedPreferences;

    public AutoBackUpSharedPrefs(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public boolean getKeyIsAutoBackupEnabled() {
        return this.sharedPreferences.getBoolean(KEY_IS_AUTO_BACKUP_ENABLED, false);
    }

    public void setKeyIsAutoBackupEnabled(boolean value) {
        this.sharedPreferences.edit().putBoolean(KEY_IS_AUTO_BACKUP_ENABLED, value).apply();
    }

    public String getAutoBackupDirectory() {
        return this.sharedPreferences.getString(KEY_AUTO_BACKUP_DIRECTORY, null);
    }

    public void setAutoBackupDirectory(String value) {
        this.sharedPreferences.edit().putString(KEY_AUTO_BACKUP_DIRECTORY, value).apply();
    }

    public void clearSharedPreferences() {
        sharedPreferences.edit().clear().apply();
    }
}

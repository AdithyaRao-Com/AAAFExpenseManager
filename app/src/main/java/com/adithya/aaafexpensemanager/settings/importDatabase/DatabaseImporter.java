package com.adithya.aaafexpensemanager.settings.importDatabase;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/** @noinspection SameParameterValue*/
public class DatabaseImporter {

    private static final String TAG = "DatabaseImporter";

    /** @noinspection ResultOfMethodCallIgnored*/
    public boolean importDatabase(Context context, Uri zipUri, File databaseFile) {
        try {
            File tempZipFile = new File(context.getCacheDir(), "temp_import.zip");

            try (InputStream is = context.getContentResolver().openInputStream(zipUri);
                 FileOutputStream fos = new FileOutputStream(tempZipFile)) {
                byte[] buffer = new byte[1024];
                int length;
                while (true) {
                    assert is != null;
                    if (!((length = is.read(buffer)) > 0)) break;
                    fos.write(buffer, 0, length);
                }
            }
            unzipDatabase(tempZipFile, databaseFile, "SQLite");
            tempZipFile.delete();
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Database import failed: " + e.getMessage());
            return false;
        }
    }

    /** @noinspection resource*/
    private void unzipDatabase(File zipFile, File databaseFile, String password) throws ZipException {
        ZipFile zip = new ZipFile(zipFile, password.toCharArray());
        zip.extractFile(databaseFile.getName(), databaseFile.getParent());
    }
}
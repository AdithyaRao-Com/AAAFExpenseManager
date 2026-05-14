package com.adithya.aaafexpensemanagerv2.settings.importExportDatabase;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.EncryptionMethod;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @noinspection SameParameterValue
 */
public class DatabaseExporter {

    private static final String TAG = "DatabaseExporter";

    public void exportDatabase(Context context, File databaseFile, Uri exportUri) {
        if (databaseFile == null || !databaseFile.exists()) {
            Log.e(TAG, "Database file not found");
            return;
        }

        File zipFile = new File(context.getCacheDir(), "temp.zip");
        try {
            // Delete temp file if it already exists to avoid Zip4j trying to append to a non-zip file
            if (zipFile.exists()) {
                //noinspection ResultOfMethodCallIgnored
                zipFile.delete();
            }

            zipDatabase(databaseFile, zipFile, "SQLite");

            try (FileInputStream fis = new FileInputStream(zipFile);
                 OutputStream os = context.getContentResolver().openOutputStream(exportUri)) {
                if (os == null) {
                    Log.e(TAG, "Could not open output stream for exportUri");
                    return;
                }
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
            }

            Log.d(TAG, "Database exported successfully");
        } catch (ZipException e) {
            Log.e(TAG, "Zip error: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "Database export failed: " + e.getMessage());
        } finally {
            if (zipFile.exists()) {
                //noinspection ResultOfMethodCallIgnored
                zipFile.delete();
            }
        }
    }

    private void zipDatabase(File databaseFile, File zipFile, String password) throws IOException {
        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setEncryptFiles(true);
        zipParameters.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD);

        try (ZipFile zip = new ZipFile(zipFile, password.toCharArray())) {
            zip.addFile(databaseFile, zipParameters);
        }
    }
}
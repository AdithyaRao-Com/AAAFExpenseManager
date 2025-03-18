package com.adithya.aaafexpensemanager.dailyBackup;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import androidx.documentfile.provider.DocumentFile;

import com.adithya.aaafexpensemanager.settings.SettingsRepository;
import com.adithya.aaafexpensemanager.settings.autoBackup.AutoBackUpSharedPrefs;
import com.adithya.aaafexpensemanager.settings.exportDatabase.DatabaseExporter;
import com.adithya.aaafexpensemanager.util.UriUtils;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/** @noinspection CallToPrintStackTrace*/
    public class DailyDataSaver {
    private static final String TAG = "DailyDataSaver";
    private static final String WORK_TAG = "aaaf_expense_manager_backup";
    // TODO - Only scheduling issue is pending to be tested completely.
    public static void scheduleDailySave(Context context) {
        Constraints constraints = new Constraints.Builder()
                .build();

        PeriodicWorkRequest saveRequest = new PeriodicWorkRequest.Builder(
                DataSaveWorker.class, 1, TimeUnit.HOURS) // For debugging
                .setConstraints(constraints)
                .build();
        WorkManager.getInstance(context).cancelUniqueWork(WORK_TAG);
        // Enqueue the new work request
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_TAG,
                ExistingPeriodicWorkPolicy.KEEP,
                saveRequest
        );
    }

    /** @noinspection CallToPrintStackTrace*/
    public static class DataSaveWorker extends Worker {
        public DataSaveWorker(Context context, WorkerParameters workerParams) {
            super(context, workerParams);
        }

        @NonNull
        @Override
        public Result doWork() {
            Log.d(TAG, "DataSaveWorker: doWork started");
            if (!saveDataToFile(getApplicationContext())) {
                return Result.failure();
            }
            Log.d(TAG, "DataSaveWorker: doWork finished");
            return Result.success();
        }

        private boolean saveDataToFile(Context context) {
            Executor executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> getDataToSave(context));
            return true;
        }

        private void getDataToSave(Context context) {
            SettingsRepository settingsRepository = new SettingsRepository((Application)
                    context.getApplicationContext());
            File file = settingsRepository.getDatabaseFile();
            DatabaseExporter databaseExporter = new DatabaseExporter();
            AutoBackUpSharedPrefs autoBackUpSharedPrefs = new AutoBackUpSharedPrefs(context);
            if (autoBackUpSharedPrefs.getKeyIsAutoBackupEnabled()) {
                String directory = autoBackUpSharedPrefs.getAutoBackupDirectory();
                Uri uriDirectory = Uri.parse(directory);
                String localDateString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                Log.d(TAG, "localDateString" + localDateString);
                String fileName = "AAAF_Expense_Manager_Backup_AUTO_" +
                        localDateString +
                        ".zip";

                try {
                    // Convert tree URI to document URI
                    Uri documentUri = UriUtils.treeUriToDocumentUri(uriDirectory);

                    if (documentUri != null) {

                        // Create a DocumentFile for the directory
                        DocumentFile dirDocFile = DocumentFile.fromTreeUri(context, documentUri);

                        if (dirDocFile != null && dirDocFile.exists() && dirDocFile.isDirectory()) {
                            // Create the new file
                            DocumentFile newFile = dirDocFile.createFile("application/zip", fileName);

                            if (newFile != null) {
                                // Export the database using the new file's URI
                                databaseExporter.exportDatabase(context, file, newFile.getUri());
                            } else {
                                Log.e(TAG, "Failed to create file: " + fileName);
                            }
                        } else {
                            Log.e(TAG, "Directory not found or invalid: " + uriDirectory);
                        }
                    } else {
                        Log.e(TAG, "Invalid tree uri: " + uriDirectory);
                    }

                } catch (Exception e) {
                    Log.e(TAG, "Database export failed: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    public static void cancelDailySave(Context context) {
        try {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_TAG);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error cancelling daily save: " + e.getMessage());
        }
    }
}
package com.adithya.aaafexpensemanager.dailyBackup;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import androidx.documentfile.provider.DocumentFile;

import com.adithya.aaafexpensemanager.settings.SettingsRepository;
import com.adithya.aaafexpensemanager.settings.autoBackup.AutoBackUpSharedPrefs;
import com.adithya.aaafexpensemanager.settings.autoBackup.AutoBackupSchedule;
import com.adithya.aaafexpensemanager.settings.exportDatabase.DatabaseExporter;
import com.adithya.aaafexpensemanager.util.UriUtils;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalUnit;
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
        long delay = new AutoBackupSchedule(Duration.ofHours(1)).calculateDelay();
        OneTimeWorkRequest saveRequest = new OneTimeWorkRequest.Builder(
                DataSaveWorker.class)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .build();
        WorkManager.getInstance(context).enqueueUniqueWork(
                WORK_TAG,
                ExistingWorkPolicy.REPLACE,
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
            scheduleDailySave(getApplicationContext());
            return Result.success();
        }

        private boolean saveDataToFile(Context context) {
            Executor executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> getDataToSave(context));
            return true;
        }

        private void getDataToSave(Context context) {
            autoBackupDatabaseEveryDay(context);
        }

        private void autoBackupDatabaseEveryDay(Context context) {
            try{
                SettingsRepository settingsRepository = new SettingsRepository((Application)
                        context.getApplicationContext());
                File file = settingsRepository.getDatabaseFile();
                DatabaseExporter databaseExporter = new DatabaseExporter();
                AutoBackUpSharedPrefs autoBackUpSharedPrefs = new AutoBackUpSharedPrefs(context);
                if (autoBackUpSharedPrefs.getKeyIsAutoBackupEnabled()) {
                    String directory = autoBackUpSharedPrefs.getAutoBackupDirectory();
                    Uri uriDirectory = Uri.parse(directory);
                    String localDateString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                    String fileName = "AAAF_Expense_Manager_Backup_AUTO_" +
                            localDateString +
                            ".zip";
                    Uri documentUri = UriUtils.treeUriToDocumentUri(uriDirectory);
                    DocumentFile dirDocFile = UriUtils.getValidDirectory(context, documentUri);
                    if (!(dirDocFile != null && dirDocFile.exists() && dirDocFile.isDirectory())) {
                        throw new RuntimeException("Error while getting directory. Directory URI is invalid" +
                                " or directory does not exist" +
                                " or URI is not a directory");
                    }
                    DocumentFile newFile = dirDocFile.createFile("application/zip", fileName);
                    if (newFile == null) {
                        throw new RuntimeException("Failed to create file: " + fileName);
                    }
                    databaseExporter.exportDatabase(context, file, newFile.getUri());
                }
            }
            catch (Exception e) {
                Log.e(TAG, "Database export failed: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
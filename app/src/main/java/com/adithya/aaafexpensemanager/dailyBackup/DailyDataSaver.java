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

import com.adithya.aaafexpensemanager.settings.SettingsRepository;
import com.adithya.aaafexpensemanager.settings.autoBackup.AutoBackUpSharedPrefs;
import com.adithya.aaafexpensemanager.settings.exportDatabase.DatabaseExporter;
import com.adithya.aaafexpensemanager.util.UriUtils;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/** @noinspection unused*/
public class DailyDataSaver {
// TODO - test this change on DailyDataSaver
    private static final String TAG = "DailyDataSaver";
    private static final String WORK_TAG = "aaaf_expense_manager_backup";

    public static void scheduleDailySave(Context context) {
        Constraints constraints = new Constraints.Builder()
                .build();

        PeriodicWorkRequest saveRequest = new PeriodicWorkRequest.Builder(
                DataSaveWorker.class, 1, TimeUnit.DAYS)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_TAG,
                ExistingPeriodicWorkPolicy.KEEP,
                saveRequest
        );
    }

    public static class DataSaveWorker extends Worker {
        public DataSaveWorker(Context context, WorkerParameters workerParams) {
            super(context, workerParams);
        }
        @NonNull
        @Override
        public Result doWork() {
            Log.d(TAG, "DataSaveWorker: doWork started");
            saveDataToFile(getApplicationContext());
            Log.d(TAG, "DataSaveWorker: doWork finished");
            return Result.success();
        }
        private void saveDataToFile(Context context) {
            Executor executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                String data = getDataToSave(context);
            });
        }
        private String getDataToSave(Context context) {
            SettingsRepository settingsRepository = new SettingsRepository((Application)
                    context.getApplicationContext());
            File file = settingsRepository.getDatabaseFile();
            DatabaseExporter databaseExporter = new DatabaseExporter();
            AutoBackUpSharedPrefs autoBackUpSharedPrefs = new AutoBackUpSharedPrefs(context);
            if (autoBackUpSharedPrefs.getKeyIsAutoBackupEnabled()) {
                String directory = autoBackUpSharedPrefs.getAutoBackupDirectory();
                Uri uriDirectory = Uri.parse(directory);
                String fileName = "AAAF_Expense_Manager_Backup_AUTO_"+
                        LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))+
                        ".zip";
                Uri fileUri = UriUtils.addFileNameToUriSmart(uriDirectory, file.getName());
                databaseExporter.exportDatabase(context, file, fileUri);
            }
            return "Data saved on " + new Date();
        }
    }
    public static void cancelDailySave(Context context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_TAG);
    }
}
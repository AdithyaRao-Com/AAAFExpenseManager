package com.adithya.aaafexpensemanager.dailyBackup;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

/** @noinspection unused*/
public class DailyDataSaver {

    private static final String TAG = "DailyDataSaver";
    private static final String WORK_TAG = "daily_data_save";

    public static void scheduleDailySave(Context context) {
        Constraints constraints = new Constraints.Builder()
                .build(); // Add constraints if needed

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
                if (!data.isEmpty()) {
                    saveStringToFile(data);
                } else {
                    Log.w(TAG, "No data to save.");
                }
            });
        }

        private String getDataToSave(Context context) {
            // TODO - *** PLACEHOLDER FOR YOUR CODE TO BE EXECUTED ***
            // Example: Get data from SharedPreferences, database, sensor, etc.
            // Replace this with your actual data retrieval logic.
            // For demonstration, let's create some dummy data.
            return "Data saved on " + new Date();
        }

        private void saveStringToFile(String data) {
            File file = getOutputFile();
            if (file != null) {
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(data.getBytes());
                    Log.d(TAG, "Data saved to: " + file.getAbsolutePath());
                } catch (IOException e) {
                    Log.e(TAG, "Error saving data to file: " + e.getMessage());
                }
            } else {
                Log.e(TAG, "Failed to create output file.");
            }
        }

        private File getOutputFile() {
            String fileName = "data_" + new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date()) + ".txt";
            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            if (!storageDir.exists()) {
                if (!storageDir.mkdirs()) {
                    Log.e(TAG, "Failed to create directory.");
                    return null;
                }
            }
            return new File(storageDir, fileName);
        }
    }

    public static void cancelDailySave(Context context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_TAG);
    }
}
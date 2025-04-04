package com.adithya.aaafexpensemanager.batchJobs;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.documentfile.provider.DocumentFile;
import androidx.work.Constraints;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.adithya.aaafexpensemanager.recurring.RecurringRepository;
import com.adithya.aaafexpensemanager.settings.SettingsRepository;
import com.adithya.aaafexpensemanager.settings.autoBackup.AutoBackUpSharedPrefs;
import com.adithya.aaafexpensemanager.settings.autoBackup.AutoBackupSchedule;
import com.adithya.aaafexpensemanager.settings.importExportDatabase.DatabaseExporter;
import com.adithya.aaafexpensemanager.util.UriUtils;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @noinspection CallToPrintStackTrace
 */
public class DailyBackupTasks {
    private static final String TAG = "DailyDataSaver";
    private static final String WORK_TAG = "aaaf_expense_manager_backup";

    public static void scheduleDailySave(Context context) {
        Constraints constraints = new Constraints.Builder()
                .build();
        long delay = new AutoBackupSchedule(Duration.ofHours(12)).calculateDelay();
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

    /**
     * @noinspection CallToPrintStackTrace
     */
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
            BatchRunRepository batchRunRepository
                    = new BatchRunRepository((Application) context.getApplicationContext());
            BatchRunLog batchRunLog = new BatchRunLog();
            batchRunRepository.addBatchRunLog(batchRunLog);
            batchRunRepository.addBatchRunDetailLog(
                    new BatchRunDetailLog(batchRunLog,
                            "autoBackupDatabaseEveryDay",
                            "autoBackupDatabaseEveryDay starting"));
            autoBackupDatabaseEveryDay(context, batchRunLog);
            batchRunRepository.addBatchRunDetailLog(
                    new BatchRunDetailLog(batchRunLog,
                            "autoBackupDatabaseEveryDay",
                            "autoBackupDatabaseEveryDay completed"));
            batchRunRepository.addBatchRunDetailLog(
                    new BatchRunDetailLog(batchRunLog,
                            "dailyRecurringTransactionsSetup",
                            "dailyRecurringTransactionsSetup starting"));
            dailyRecurringTransactionsSetup(context, batchRunLog);
            batchRunRepository.addBatchRunDetailLog(
                    new BatchRunDetailLog(batchRunLog,
                            "dailyRecurringTransactionsSetup",
                            "dailyRecurringTransactionsSetup completed"));
            batchRunRepository.removeOldEntriesRunDetail();
            batchRunRepository.removeOldEntriesRun();
        }

        private void dailyRecurringTransactionsSetup(Context context, BatchRunLog batchRunLog) {
            try {
                RecurringRepository recurringScheduleRepository =
                        new RecurringRepository((Application) context.getApplicationContext());
                recurringScheduleRepository.keepFutureTransactionsUpToDate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void autoBackupDatabaseEveryDay(Context context, BatchRunLog batchRunLog) {
            try {
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
            } catch (Exception e) {
                Log.e(TAG, "Database export failed: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
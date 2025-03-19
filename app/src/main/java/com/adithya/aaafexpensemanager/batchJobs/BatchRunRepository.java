package com.adithya.aaafexpensemanager.batchJobs;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.adithya.aaafexpensemanager.util.DatabaseHelper;

/** @noinspection resource, CallToPrintStackTrace , UnusedReturnValue */
public class BatchRunRepository {
    private final SQLiteDatabase db;
    public BatchRunRepository(Application application) {
        DatabaseHelper dbHelper = new DatabaseHelper(application);
        db = dbHelper.getWritableDatabase();
    }
    public boolean addBatchRunLog(BatchRunLog batchRunLog){
        try{
            db.insertOrThrow("batch_run_log", null, batchRunLog.toContentValues());
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    public boolean addBatchRunDetailLog(BatchRunDetailLog batchRunDetailLog){
        try{
            db.insertOrThrow("batch_run_detail_log", null, batchRunDetailLog.toContentValues());
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeOldEntriesRunDetail() {
        try{
            db.delete("batch_run_detail_log", "log_date < ? ", new String[]{
                    String.valueOf(System.currentTimeMillis() - (1000 * 60 * 60 * 24 * 7))});
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    public boolean removeOldEntriesRun() {
        try{
            db.delete("batch_run_log", "run_date < ? ", new String[]{
                    String.valueOf(System.currentTimeMillis() - (1000 * 60 * 60 * 24 * 7))});
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}

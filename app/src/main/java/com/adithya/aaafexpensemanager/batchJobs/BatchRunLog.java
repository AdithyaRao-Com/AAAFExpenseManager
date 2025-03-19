package com.adithya.aaafexpensemanager.batchJobs;

import android.content.ContentValues;

import java.util.UUID;

public class BatchRunLog {
    public String batch_run_uuid;
    public long batch_run_date;
    public BatchRunLog() {
        this.batch_run_uuid = UUID.randomUUID().toString();
        this.batch_run_date = System.currentTimeMillis();;
    }
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put("batch_run_uuid", batch_run_uuid);
        values.put("batch_run_date", batch_run_date);
        return values;
    }
}

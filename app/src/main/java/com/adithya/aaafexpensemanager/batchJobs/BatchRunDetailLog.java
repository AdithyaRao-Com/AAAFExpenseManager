package com.adithya.aaafexpensemanager.batchJobs;

import android.content.ContentValues;

import java.util.UUID;

public class BatchRunDetailLog {
    public String batch_run_detail_uuid;
    public String batch_run_uuid;
    public String tag;
    public String log_text;
    public long log_date;

    public BatchRunDetailLog(String batch_run_uuid, String tag, String log_text) {
        this.batch_run_detail_uuid = UUID.randomUUID().toString();
        this.batch_run_uuid = batch_run_uuid;
        this.tag = tag;
        this.log_text = log_text;
        this.log_date = System.currentTimeMillis();
    }

    public BatchRunDetailLog(BatchRunLog batchRunLog, String tag, String log_text) {
        this.batch_run_detail_uuid = UUID.randomUUID().toString();
        this.batch_run_uuid = batchRunLog.batch_run_uuid;
        this.tag = tag;
        this.log_text = log_text;
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put("batch_run_detail_uuid", batch_run_detail_uuid);
        values.put("batch_run_uuid", batch_run_uuid);
        values.put("tag", tag);
        values.put("log_text", log_text);
        values.put("log_date", log_date);
        return values;
    }
}

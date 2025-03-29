package com.adithya.aaafexpensemanager.settings.importExportQIF;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/** @noinspection CallToPrintStackTrace*/
public class QIFImportExportRepository {
    private final SQLiteDatabase db;
    /** @noinspection FieldCanBeLocal*/
    private final Application application;
    public QIFImportExportRepository(Application application) {
        //noinspection resource
        QIFImportExportDBHelper dbHelper = new QIFImportExportDBHelper(application);
        this.application = application;
        db = dbHelper.getWritableDatabase();
    }
    public void addQIFImportExportRecord(QIFImportExportRecord record) {
        try {
            ContentValues values = new ContentValues();
            values.put("account_name", record.accountName);
            values.put("account_type", record.accountType);
            values.put("transaction_date", record.transactionDate.toString());
            values.put("amount", record.amount);
            values.put("payee", record.payee);
            values.put("memo", record.memo);
            values.put("category", record.category);
            db.insert(QIFImportExportDBHelper.TABLE_TRANSACTIONS, null, values);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void deleteAllQIFImportExportRecords() {
        db.delete(QIFImportExportDBHelper.TABLE_TRANSACTIONS, null, null);
    }
    public void addAllQIFImportExportRecords(List<QIFImportExportRecord> records) {
        for (QIFImportExportRecord record : records) {
            addQIFImportExportRecord(record);
        }
    }
    public List<QIFImportExportRecord> getAllQIFImportExportRecords() {
        List<QIFImportExportRecord> records = new ArrayList<>();
        try(Cursor cursor = db.query(QIFImportExportDBHelper.TABLE_TRANSACTIONS, null, null, null, null, null, null)){
            if (cursor.moveToFirst()) {
                do {
                    QIFImportExportRecord record = getQIFImportExportRecordFromCursor(cursor);
                    if (record != null) {
                        records.add(record);
                    }
                } while (cursor.moveToNext());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return records;
    }
    private QIFImportExportRecord getQIFImportExportRecordFromCursor(Cursor cursor) {
        try {
            String accountName = cursor.getString(cursor.getColumnIndexOrThrow("account_name"));
            String accountType = cursor.getString(cursor.getColumnIndexOrThrow("account_type"));
            String transactionDate = cursor.getString(cursor.getColumnIndexOrThrow("transaction_date"));
            String amount = cursor.getString(cursor.getColumnIndexOrThrow("amount"));
            String payee = cursor.getString(cursor.getColumnIndexOrThrow("payee"));
            String memo = cursor.getString(cursor.getColumnIndexOrThrow("memo"));
            String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
            return new QIFImportExportRecord(accountName, accountType, transactionDate, amount, payee, memo, category);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public List<QIFHeaderRecord> getAllQIFHeaderRecords(){
        List<QIFHeaderRecord> records = new ArrayList<>();
        try(Cursor cursor = db.rawQuery("SELECT DISTINCT account_name, account_type FROM " + QIFImportExportDBHelper.TABLE_TRANSACTIONS, null)){
            if (cursor.moveToFirst()) {
                do {
                    String accountName = cursor.getString(cursor.getColumnIndexOrThrow("account_name"));
                    String accountType = cursor.getString(cursor.getColumnIndexOrThrow("account_type"));
                    QIFHeaderRecord record = new QIFHeaderRecord(accountName, accountType);
                    records.add(record);
                } while (cursor.moveToNext());
            }
            return records;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

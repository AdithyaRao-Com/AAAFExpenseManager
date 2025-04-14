package com.adithya.aaafexpensemanager.transactionFilter;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

import com.adithya.aaafexpensemanager.util.DBHelperActions;
import com.adithya.aaafexpensemanager.util.DatabaseHelper;
import com.adithya.aaafexpensemanager.util.GsonListStringConversion;

import java.util.ArrayList;
import java.util.List;

/**
 * @noinspection resource, CallToPrintStackTrace , FieldCanBeLocal
 */
public class TransactionFilterRepository {
    private final SQLiteDatabase db;
    private final Application application;

    public TransactionFilterRepository(Application application) {
        DatabaseHelper dbHelper = new DatabaseHelper(application);
        db = dbHelper.getWritableDatabase();
        this.application = application;
    }

    @NonNull
    private static TransactionFilter getTransactionFilterFromCursor(Cursor cursor) {
        TransactionFilter transactionFilter = new TransactionFilter();
        transactionFilter.reportName = cursor.getString(cursor.getColumnIndexOrThrow("report_name"));
        transactionFilter.reportType = cursor.getString(cursor.getColumnIndexOrThrow("report_type"));
        transactionFilter.transactionNames = new ArrayList<>(GsonListStringConversion.jsonToList(cursor.getString(cursor.getColumnIndexOrThrow("transaction_names"))));
        transactionFilter.fromTransactionDate = cursor.getInt(cursor.getColumnIndexOrThrow("from_transaction_date"));
        transactionFilter.toTransactionDate = cursor.getInt(cursor.getColumnIndexOrThrow("to_transaction_date"));
        transactionFilter.categories = new ArrayList<>(GsonListStringConversion.jsonToList(cursor.getString(cursor.getColumnIndexOrThrow("categories"))));
        transactionFilter.accountNames = new ArrayList<>(GsonListStringConversion.jsonToList(cursor.getString(cursor.getColumnIndexOrThrow("account_names"))));
        transactionFilter.toAccountNames = new ArrayList<>(GsonListStringConversion.jsonToList(cursor.getString(cursor.getColumnIndexOrThrow("to_account_names"))));
        transactionFilter.fromAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("from_amount"));
        transactionFilter.toAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("to_amount"));
        transactionFilter.transactionTypes = new ArrayList<>(GsonListStringConversion.jsonToList(cursor.getString(cursor.getColumnIndexOrThrow("transaction_types"))));
        transactionFilter.searchText = cursor.getString(cursor.getColumnIndexOrThrow("search_text"));
        transactionFilter.accountTypes = new ArrayList<>(GsonListStringConversion.jsonToList(cursor.getString(cursor.getColumnIndexOrThrow("account_types"))));
        transactionFilter.periodName = cursor.getString(cursor.getColumnIndexOrThrow("period_name"));
        transactionFilter.accountTags = new ArrayList<>(GsonListStringConversion.jsonToList(cursor.getString(cursor.getColumnIndexOrThrow("account_tags"))));
        return transactionFilter;
    }

    @NonNull
    private static ContentValues getContentValues(TransactionFilter transactionFilter) {
        ContentValues values = new ContentValues();
        values.put("report_name", transactionFilter.reportName);
        values.put("report_type", transactionFilter.reportType);
        values.put("transaction_names", GsonListStringConversion.listToJson(transactionFilter.transactionNames));
        values.put("from_transaction_date", transactionFilter.fromTransactionDate);
        values.put("to_transaction_date", transactionFilter.toTransactionDate);
        values.put("categories", GsonListStringConversion.listToJson(transactionFilter.categories));
        values.put("account_names", GsonListStringConversion.listToJson(transactionFilter.accountNames));
        values.put("to_account_names", GsonListStringConversion.listToJson(transactionFilter.toAccountNames));
        values.put("from_amount", transactionFilter.fromAmount);
        values.put("to_amount", transactionFilter.toAmount);
        values.put("transaction_types", GsonListStringConversion.listToJson(transactionFilter.transactionTypes));
        values.put("search_text", transactionFilter.searchText);
        values.put("account_types", GsonListStringConversion.listToJson(transactionFilter.accountTypes));
        values.put("period_name", transactionFilter.periodName);
        values.put("account_tags", GsonListStringConversion.listToJson(transactionFilter.accountTags));
        return values;
    }

    public void addTransactionFilter(TransactionFilter transactionFilter, TransactionFilter previousTransactionFilter) {
        ContentValues values = getContentValues(transactionFilter);
        try {
            if (!previousTransactionFilter.reportName.isBlank()) {
                db.delete(DBHelperActions.TRANSACTION_FILTER, "report_name = ?", new String[]{previousTransactionFilter.reportName});
            }
            db.insertOrThrow(DBHelperActions.TRANSACTION_FILTER, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteTransactionFilter(TransactionFilter transactionFilter) {
        try {
            db.delete(DBHelperActions.TRANSACTION_FILTER, "report_name = ?", new String[]{transactionFilter.reportName});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<TransactionFilter> getAllTransactionFilters() {
        List<TransactionFilter> transactionFilters = new ArrayList<>();
        try (Cursor cursor = db.query(DBHelperActions.TRANSACTION_FILTER, null, null, null, null, null, null)) {
            if (cursor.moveToFirst()) {
                do {
                    TransactionFilter transactionFilter = getTransactionFilterFromCursor(cursor);
                    transactionFilters.add(transactionFilter);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return transactionFilters;
    }
}

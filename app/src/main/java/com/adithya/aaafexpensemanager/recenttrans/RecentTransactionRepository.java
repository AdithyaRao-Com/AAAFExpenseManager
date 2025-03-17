package com.adithya.aaafexpensemanager.recenttrans;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.adithya.aaafexpensemanager.transaction.Transaction;
import com.adithya.aaafexpensemanager.util.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

/** @noinspection resource, CallToPrintStackTrace , unused */
public class RecentTransactionRepository {
    private final SQLiteDatabase db;
    public RecentTransactionRepository(Application application) {
        DatabaseHelper dbHelper = new DatabaseHelper(application);
        db = dbHelper.getWritableDatabase();
    }

    public List<RecentTransaction> getAllRecentTransactions() {
        List<RecentTransaction> transactions = new ArrayList<>();
        try (Cursor cursor = db.query("recent_transactions", null, null, null, null, null,
                "create_date DESC")){
            if (cursor.moveToFirst()) {
                do {
                    RecentTransaction transaction = getRecentTransactionFromCursor(cursor);
                    if (transaction != null) {
                        transactions.add(transaction);
                    }
                } while (cursor.moveToNext());
            }
        }
        return transactions;
    }
    private RecentTransaction getRecentTransactionFromCursor(Cursor cursor) {
        try {
            int nameIndex = cursor.getColumnIndexOrThrow("transaction_name");
            int typeIndex = cursor.getColumnIndexOrThrow("transaction_type");
            int categoryIndex = cursor.getColumnIndexOrThrow("category");
            int notesIndex = cursor.getColumnIndexOrThrow("notes");
            int amountIndex = cursor.getColumnIndexOrThrow("amount");
            int accountIndex = cursor.getColumnIndexOrThrow("account_name");
            int toAccountIndex = cursor.getColumnIndexOrThrow("to_account_name");
            int createDateTimeIndex = cursor.getColumnIndexOrThrow("create_date");
            int lastUpdateDateTimeIndex = cursor.getColumnIndexOrThrow("last_update_date");

            String transactionName = cursor.getString(nameIndex);
            String transactionType = cursor.getString(typeIndex);
            String category = cursor.getString(categoryIndex);
            String notes = cursor.getString(notesIndex);
            double amount = cursor.getDouble(amountIndex);
            String accountName = cursor.getString(accountIndex);
            String toAccountName = cursor.getString(toAccountIndex);
            long createDateTime = cursor.getLong(createDateTimeIndex);
            long lastUpdateDateTime = cursor.getLong(lastUpdateDateTimeIndex);
            return new RecentTransaction(transactionName, transactionType, category, notes, amount, accountName, toAccountName,createDateTime,lastUpdateDateTime);
        } catch (IllegalArgumentException e) { // Catch column not found
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public RecentTransaction getRecentTransactionByName(String transactionName) {
        try (Cursor cursor = db.rawQuery("SELECT * FROM recent_transactions WHERE transaction_name = ?", new String[]{transactionName})) {
            if (cursor.moveToFirst()) {
                return getRecentTransactionFromCursor(cursor);
            }
        }
        return null;
    }
    public void deleteAll(){
        int rowsAffected = db.delete("recent_transactions", null, null);
    }
    public void insertRecentTransaction(RecentTransaction recentTransaction) {
        try{
            ContentValues values = new ContentValues();
            values.put("transaction_name", recentTransaction.transactionName);
            values.put("transaction_type", recentTransaction.transactionType);
            values.put("category", recentTransaction.category);
            values.put("notes", recentTransaction.notes);
            values.put("amount", Math.round(recentTransaction.amount*100)/100);
            values.put("account_name", recentTransaction.accountName);
            values.put("to_account_name", recentTransaction.toAccountName);
            values.put("create_date", recentTransaction.createDateTime);
            values.put("last_update_date", recentTransaction.lastUpdateDateTime);
            db.beginTransaction();
            db.delete("recent_transactions", "transaction_name = ?", new String[]{recentTransaction.transactionName});
            db.insert("recent_transactions", null, values);
            db.setTransactionSuccessful();
        } catch (SQLiteException ignored) {
        }
        finally {
            db.endTransaction();
        }
    }
    public void updateRecentTransaction(RecentTransaction recentTransaction) {
        try {
            insertRecentTransaction(recentTransaction);
        } catch (SQLiteException ignored) {
        }
    }

    public void updateRecentTransaction(Transaction transaction) {
        RecentTransaction recentTransaction = new RecentTransaction(transaction);
        updateRecentTransaction(recentTransaction);
    }

    public void updateAllRecentTransactions(){
        List<RecentTransaction> recentTransactions = recreateAllRecentTransactions();
        Log.d("RecentTransactionRepository", "Updating " + recentTransactions.size() + " recent transactions");
        for (RecentTransaction recentTransaction : recentTransactions) {
            updateRecentTransaction(recentTransaction);
        }
    }

    public List<RecentTransaction> recreateAllRecentTransactions(){
        List<RecentTransaction> recentTransactions = new ArrayList<>();
        String selectQuery = "SELECT * FROM " +
                "(SELECT transaction_name," +
                "transaction_type, " +
                "category, " +
                "notes, " +
                "amount, " +
                "account_name, " +
                "to_account_name, " +
                "create_date, " +
                "last_update_date," +
                "ROW_NUMBER() OVER(PARTITION BY transaction_name " +
                "ORDER BY transaction_date DESC," +
                "create_date DESC) AS row_num " +
                "FROM transactions)" +
                " WHERE row_num =1";
        try (Cursor cursor = db.rawQuery(selectQuery,null)){
            if (cursor.moveToFirst()) {
                do {
                    RecentTransaction transaction = getRecentTransactionFromCursor(cursor);
                    if (transaction != null) {
                        recentTransactions.add(transaction);
                    }
                } while (cursor.moveToNext());
            }
        }
        catch (SQLiteException e){
            e.printStackTrace();
        }
        return recentTransactions;
    }
}
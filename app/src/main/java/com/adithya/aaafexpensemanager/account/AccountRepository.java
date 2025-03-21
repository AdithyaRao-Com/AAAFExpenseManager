package com.adithya.aaafexpensemanager.account;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import androidx.annotation.NonNull;

import com.adithya.aaafexpensemanager.transaction.TransactionRepository;
import com.adithya.aaafexpensemanager.util.DatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** @noinspection resource*/
public class AccountRepository {

    private final SQLiteDatabase db;
    /** @noinspection FieldCanBeLocal, unused */
    private final Application application;

    public AccountRepository(Application application) {
        DatabaseHelper dbHelper = new DatabaseHelper(application);
        db = dbHelper.getWritableDatabase();
        this.application = application;
    }
    public List<Account> getAccounts() {
        List<Account> accounts = new ArrayList<>();
        Cursor cursor = db.query("accounts_all_view", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                accounts.add(getAccountFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return accounts;
    }
    public List<Account> filterAccounts(String searchText) {
        List<Account> filteredAccounts = new ArrayList<>();
        String selection = "account_name LIKE ?";
        String[] selectionArgs = new String[]{"%" + searchText + "%"}; // Use wildcards for "contains"
        Cursor cursor = db.query("accounts_all_view", null, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                filteredAccounts.add(getAccountFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return filteredAccounts;
    }

    public void createAccount(Account account) {
        ContentValues values = getContentValues(account,true);
        db.insert("accounts", null, values);
    }

    @NonNull
    private static ContentValues getContentValues(Account account, boolean isNew) {
        ContentValues values = new ContentValues();
        values.put("account_type", account.accountType);
        values.put("account_tags", account.accountTags);
        values.put("display_order", account.displayOrder);
        values.put("currency_code", account.currencyCode);
        if(isNew){
            values.put("account_name", account.accountName);
            values.put("account_balance", Math.round(account.accountBalance*100.0)/100.0);
        }
        return values;
    }

    public void updateAccountOnly(Account account) {
        ContentValues values = getContentValues(account,false);
        db.update("accounts", values, "account_name = ?", new String[]{account.accountName}); // Update based on name (or ID if you have one)
    }
    public void updateAccount(Account account) {
        updateAccountOnly(account);
    }

    public void updateAccountBalance(String accountName, double newBalance) {
        try {
            ContentValues values = new ContentValues();
            values.put("account_balance", Math.round(newBalance*100.0)/100.0);
            db.update("accounts", values, "account_name = ?", new String[]{accountName});
        } catch (Exception ignored) {
        }
    }
    public void deleteAccount(String accountName) {
        db.delete("accounts", "account_name = ?", new String[]{accountName});
    }
    public Account getAccountByName(String accountName) {
        Cursor cursor = db.query("accounts", null, "account_name = ?", new String[]{accountName}, null, null, null);
        Account account = null;
        if (cursor.moveToFirst()) {
            account = getAccountFromCursor(cursor);
        }
        cursor.close();
        return account;
    }

    public Account getAccountFromCursor(Cursor cursor){
        String accountName = cursor.getString(cursor.getColumnIndexOrThrow("account_name"));
        String type = cursor.getString(cursor.getColumnIndexOrThrow("account_type"));
        double balance = cursor.getDouble(cursor.getColumnIndexOrThrow("account_balance"));
        String tags = cursor.getString(cursor.getColumnIndexOrThrow("account_tags"));
        int displayOrder = cursor.getInt(cursor.getColumnIndexOrThrow("display_order"));
        String currencyCode = cursor.getString(cursor.getColumnIndexOrThrow("currency_code"));
        return new Account(accountName, type, balance, tags,displayOrder,currencyCode);
    }
    public void deleteAll(){
        //noinspection unused
        int rowsAffected = db.delete("accounts", null, null);
    }

    /** @noinspection DataFlowIssue*/
    public void updateAccountBalances(TransactionRepository transactionRepository) {
        HashMap<String,Double> hashMap = transactionRepository.getAccountBalances();
        updateAccountBalanceToZero();
        for (String accountName : hashMap.keySet()) {
            updateAccountBalance(accountName, hashMap.get(accountName));
        }
    }

    private void updateAccountBalanceToZero() {
        try{
            db.execSQL("UPDATE accounts SET account_balance = 0");
        } catch (SQLiteException ignored) {
        }
    }
}
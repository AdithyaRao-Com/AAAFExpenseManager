package com.adithya.aaafexpensemanager.account;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import androidx.annotation.NonNull;

import com.adithya.aaafexpensemanager.transaction.TransactionRepository;
import com.adithya.aaafexpensemanager.transactionFilter.TransactionFilterUtils;
import com.adithya.aaafexpensemanager.util.DatabaseHelper;
import com.adithya.aaafexpensemanager.util.GsonListStringConversion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** @noinspection resource, CallToPrintStackTrace */
public class AccountRepository {

    private final SQLiteDatabase db;
    /**
     * @noinspection FieldCanBeLocal, unused
     */
    private final Application application;

    public AccountRepository(Application application) {
        DatabaseHelper dbHelper = new DatabaseHelper(application);
        db = dbHelper.getWritableDatabase();
        this.application = application;
    }

    public List<Account> getAccounts() {
        return getAccounts(false);
    }

    public List<Account> getAccounts(boolean showClosedAccounts) {
        List<Account> accounts = new ArrayList<>();
        String selection = "1=1";
        if (!showClosedAccounts) {
            selection += " AND close_account_ind = 0";
        }
        Cursor cursor = db.query("accounts_all_view", null, selection, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                accounts.add(getAccountFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return accounts;
    }

    public List<Account> filterAccounts(String searchText, boolean showClosedAccounts) {
        List<Account> filteredAccounts = new ArrayList<>();
        String selection = "1=1 AND account_name LIKE ? ";
        if (!showClosedAccounts) {
            selection += "AND close_account_ind = 0";
        }
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
        addTagsAccount(account.accountTags);
        ContentValues values = getContentValues(account, true);
        db.insert("accounts", null, values);
    }

    @NonNull
    private static ContentValues getContentValues(Account account, boolean isNew) {
        ContentValues values = new ContentValues();
        values.put("account_type", account.accountType);
        values.put("account_tags", account.accountTags);
        values.put("display_order", account.displayOrder);
        values.put("currency_code", account.currencyCode);
        values.put("close_account_ind", account.closeAccountInd ? 1 : 0);
        values.put("do_not_show_in_dropdown", account.doNotShowInDropdownInd ? 1 : 0);
        if (isNew) {
            values.put("account_name", account.accountName);
            values.put("account_balance", Math.round(account.accountBalance * 100.0) / 100.0);
        }
        return values;
    }

    public void updateAccountOnly(Account account) {
        addTagsAccount(account.accountTags);
        ContentValues values = getContentValues(account, false);
        int t1 = db.update("accounts", values, "account_name = ?", new String[]{account.accountName}); // Update based on name (or ID if you have one)
        Log.d("AccountRepository", "Rows updated: " + t1);
        Account acc1 = getAccountByName(account.accountName);
    }

    public void updateAccount(Account account) {
        updateAccountOnly(account);
    }

    public void updateAccountBalance(String accountName, double newBalance) {
        try {
            ContentValues values = new ContentValues();
            values.put("account_balance", Math.round(newBalance * 100.0) / 100.0);
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

    public Account getAccountFromCursor(Cursor cursor) {
        String accountName = cursor.getString(cursor.getColumnIndexOrThrow("account_name"));
        String type = cursor.getString(cursor.getColumnIndexOrThrow("account_type"));
        double balance = cursor.getDouble(cursor.getColumnIndexOrThrow("account_balance"));
        String tags = cursor.getString(cursor.getColumnIndexOrThrow("account_tags"));
        int displayOrder = cursor.getInt(cursor.getColumnIndexOrThrow("display_order"));
        String currencyCode = cursor.getString(cursor.getColumnIndexOrThrow("currency_code"));
        boolean closeAccountInd = cursor.getInt(cursor.getColumnIndexOrThrow("close_account_ind")) == 1;
        boolean doNotShowInDropdownInd = cursor.getInt(cursor.getColumnIndexOrThrow("do_not_show_in_dropdown")) == 1;
        return new Account(accountName, type, balance, tags, displayOrder, currencyCode, closeAccountInd, doNotShowInDropdownInd);
    }

    public void deleteAll() {
        //noinspection unused
        int rowsAffected = db.delete("accounts", null, null);
    }

    /**
     * @noinspection DataFlowIssue
     */
    public void updateAccountBalances(TransactionRepository transactionRepository) {
        HashMap<String, Double> hashMap = transactionRepository.getAccountBalances();
        updateAccountBalanceToZero();
        for (String accountName : hashMap.keySet()) {
            updateAccountBalance(accountName, hashMap.get(accountName));
        }
    }

    private void updateAccountBalanceToZero() {
        try {
            db.execSQL("UPDATE accounts SET account_balance = 0");
        } catch (SQLiteException ignored) {
        }
    }

    public void addTagsAccount(String tagString) {
        if (tagString == null || tagString.isBlank()) {
            return;
        }
        List<String> tagsList = GsonListStringConversion.jsonToList(tagString);
        for (String tag : tagsList) {
            try {
                ContentValues values = new ContentValues();
                values.put("tag_name", tag);
                values.put("tag_type", "Account");
                db.insertOrThrow("tags_master", null, values);
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteAllAccountTags() {
        try {
            db.delete("tags_master", "tag_type = ?", new String[]{"Account"});

        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    public void refreshAccountTags() {
        deleteAllAccountTags();
        List<Account> accounts = getAccounts(true);
        for (Account account : accounts) {
            addTagsAccount(account.accountTags);
        }
    }

    public List<String> getAccountTags() {
        List<String> accountTags = new ArrayList<>();
        try (Cursor cursor = db.query("tags_master", null, "tag_type = ?", new String[]{"Account"}, null, null, null)) {
            if (cursor.moveToFirst()) {
                do {
                    String tagName = cursor.getString(cursor.getColumnIndexOrThrow("tag_name"));
                    accountTags.add(tagName);
                } while (cursor.moveToNext());
            }
        }
        return accountTags;
    }

    public List<String> getAccountTags(String accountName) {
        List<String> accountTags = new ArrayList<>();
        String ACCOUNT_TAGS_QUERY = "SELECT account_tags FROM accounts WHERE account_name = ?";
        try (Cursor cursor = db.rawQuery(ACCOUNT_TAGS_QUERY, new String[]{accountName})) {
            if (cursor.moveToFirst()) {
                String tags = cursor.getString(cursor.getColumnIndexOrThrow("account_tags"));
                accountTags = GsonListStringConversion.jsonToList(tags);
            }
            return accountTags;
        } catch (SQLiteException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<String> getTaggedAccountNames(List<String> accountTags) {
        List<String> accountNames = new ArrayList<>();
        String ACCOUNT_TAGS_QUERY = "SELECT acc1.account_name\n" +
                "  FROM accounts acc1, json_each(account_tags) tag1\n" +
                " WHERE tag1.value IN (<<account_tags>>)\n" +
                "   AND acc1.account_tags IS NOT NULL\n" +
                "   AND acc1.account_tags <> ''";
        StringBuilder generateQueryString = new StringBuilder();
        ArrayList<String> inputArgsList = new ArrayList<>(accountTags);
        ArrayList<String> outputArgsList = new ArrayList<>();
        TransactionFilterUtils.buildValuesToQueryInClause(generateQueryString, inputArgsList, outputArgsList);
        ACCOUNT_TAGS_QUERY = ACCOUNT_TAGS_QUERY.replace("<<account_tags>>", generateQueryString.toString());
        try (Cursor cursor = db.rawQuery(ACCOUNT_TAGS_QUERY, outputArgsList.toArray(new String[0]))) {
            if (cursor.moveToFirst()) {
                do {
                    String accountName = cursor.getString(cursor.getColumnIndexOrThrow("account_name"));
                    accountNames.add(accountName);
                } while (cursor.moveToNext());
                return accountNames;
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return new ArrayList<>();
    }
}






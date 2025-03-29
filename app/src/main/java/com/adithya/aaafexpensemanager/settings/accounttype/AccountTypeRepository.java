package com.adithya.aaafexpensemanager.settings.accounttype;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.adithya.aaafexpensemanager.util.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class AccountTypeRepository {
    private final DatabaseHelper dbHelper;
    private final SQLiteDatabase db;

    public AccountTypeRepository(Application application) {
        dbHelper = new DatabaseHelper(application);
        db = dbHelper.getWritableDatabase();
    }

    public AccountType getAccountTypesFromCursor(Cursor cursor) {
        String accountType = cursor.getString(cursor.getColumnIndexOrThrow("account_type"));
        int accountTypeDisplayOrder = cursor.getInt(cursor.getColumnIndexOrThrow("account_type_display_order"));
        return new AccountType(accountType, accountTypeDisplayOrder);
    }

    public List<AccountType> getAccountTypes() {
        List<AccountType> accountTypes = new ArrayList<>();
        Cursor cursor = db.query("account_types", null, null, null, null, null, "account_type_display_order ASC");
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    accountTypes.add(getAccountTypesFromCursor(cursor));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return accountTypes;
    }

    public List<AccountType> filterAccountTypes(String searchText) {
        List<AccountType> filteredAccounts = new ArrayList<>();
        String selection = "account_type LIKE ?";
        String[] selectionArgs = new String[]{"%" + searchText + "%"}; // Use wildcards for "contains"
        Cursor cursor = db.query("account_types", null, selection, selectionArgs, null, null, "account_type_display_order ASC");
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    filteredAccounts.add(getAccountTypesFromCursor(cursor));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return filteredAccounts;
    }

    public void createAccountType(AccountType accountType) {
        if (getAccountTypeFromName(accountType.accountType) == null) {
            ContentValues values = new ContentValues();
            values.put("account_type", accountType.accountType);
            values.put("account_type_display_order", accountType.accountTypeDisplayOrder);
            db.insert("account_types", null, values);
        }
    }

    public void updateAccountType(AccountType accountType) {
        ContentValues values = new ContentValues();
        values.put("account_type_display_order", accountType.accountTypeDisplayOrder);
        db.update("account_types", values, "account_type = ?", new String[]{accountType.accountType});
    }

    public void deleteAccountType(String accountType) {
        db.delete("account_types", "account_type = ?", new String[]{accountType});
    }

    public void insertDefaultAccountTypes() {
        createAccountType(new AccountType("Cash", 999));
        createAccountType(new AccountType("Savings", 999));
        createAccountType(new AccountType("Credit Card", 999));
        createAccountType(new AccountType("Investment", 999));
        createAccountType(new AccountType("Other Assets", 999));
        createAccountType(new AccountType("Other Liabilities", 999));
    }

    public AccountType getAccountTypeFromName(String accountTypeName) {
        Cursor cursor = db.query("account_types", null, "account_type = ?", new String[]{accountTypeName}, null, null, null);
        AccountType accountType = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                accountType = getAccountTypesFromCursor(cursor);
            }
            cursor.close();
        }
        return accountType;
    }
}

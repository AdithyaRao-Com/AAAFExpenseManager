package com.adithya.aaafexpensemanager.settings.category;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.adithya.aaafexpensemanager.account.AccountRepository;
import com.adithya.aaafexpensemanager.settings.category.exception.CategoryExistsException;
import com.adithya.aaafexpensemanager.transaction.Transaction;
import com.adithya.aaafexpensemanager.transaction.TransactionRepository;
import com.adithya.aaafexpensemanager.transactionFilter.TransactionFilter;
import com.adithya.aaafexpensemanager.util.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @noinspection unused
 */
public class CategoryRepository {
    private final SQLiteDatabase db;
    private final Application application;

    public CategoryRepository(Application application) {
        //noinspection RedundantExplicitVariableType,resource
        DatabaseHelper dbHelper = new DatabaseHelper(application);
        this.application = application;
        db = dbHelper.getWritableDatabase();
    }

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        try (Cursor cursor = db.query("categories", null, null, null, null, null, null)) {
            if (cursor.moveToFirst()) {
                do {
                    Category category = getCategoryFromCursor(cursor);
                    if (category != null) {
                        categories.add(category);
                    }
                } while (cursor.moveToNext());
            }
        }
        return categories;
    }

    public Category getCategoryById(String categoryUUID) {
        Category category = null;
        try (Cursor cursor = db.query("categories", null, "category_uuid = ?", new String[]{categoryUUID}, null, null, null)) {
            if (cursor.moveToFirst()) {
                category = getCategoryFromCursor(cursor);
            }
        }
        return category;
    }

    private Category getCategoryFromCursor(Cursor cursor) {
        try {
            String categoryUUIDStr = cursor.getString(cursor.getColumnIndexOrThrow("category_uuid"));
            UUID categoryUUID = UUID.fromString(categoryUUIDStr);
            String categoryName = cursor.getString(cursor.getColumnIndexOrThrow("category_name"));
            String parentCategory = cursor.getString(cursor.getColumnIndexOrThrow("parent_category"));
            return new Category(categoryUUID, categoryName, parentCategory);
        } catch (Exception e) {
            return null;
        }
    }

    public void addCategory(Category category) throws CategoryExistsException {
        boolean isRecordExists = checkCategoryNameExists(category.categoryName,
                category.categoryUUID);
        if (isRecordExists) {
            throw new CategoryExistsException(category.categoryName);
        }
        ContentValues values = new ContentValues();
        values.put("category_uuid", category.categoryUUID.toString());
        values.put("category_name", category.categoryName);
        values.put("parent_category", category.parentCategory);
        long result = db.insert("categories", null, values);
    }

    public boolean checkCategoryNameExists(String categoryName, UUID categoryUUID) {
        String selection = "(category_name = ? AND category_uuid != ?)";
        String[] selectionArgs = new String[]{categoryName, categoryUUID.toString()};
        try (Cursor cursor = db.query("categories", null, selection, selectionArgs, null, null, null)) {
            if (cursor.getCount() > 0) {
                return true;
            }
        }
        return false;
    }

    public void updateCategory(Category category) throws CategoryExistsException {
        Category originalCategory = getCategoryById(category.categoryUUID.toString());
        if (originalCategory == null) {
            return;
        }
        boolean isRecordExists = checkCategoryNameExists(category.categoryName,
                category.categoryUUID);
        if (isRecordExists) {
            throw new CategoryExistsException(category.categoryName);
        }
        if (!originalCategory.categoryName.equals(category.categoryName)) {
            TransactionRepository transactionRepository = new TransactionRepository(application);
            TransactionFilter transactionFilter = new TransactionFilter();
            ArrayList<String> categories = new ArrayList<>();
            categories.add(originalCategory.categoryName);
            transactionFilter.categories = categories;
            List<Transaction> transactions = transactionRepository.getAllTransactions(transactionFilter, -1);
            for (Transaction transaction : transactions) {
                transaction.category = category.categoryName;
                transactionRepository.updateTransactionTableOnly(transaction);
            }
            AccountRepository accountRepository = new AccountRepository(application);
            accountRepository.updateAccountBalances(transactionRepository);
        }
        updateCategoryOnly(category);
    }

    public void updateCategoryOnly(Category category) {
        ContentValues values = new ContentValues();
        values.put("category_name", category.categoryName);
        values.put("parent_category", category.parentCategory);

        String whereClause = "category_uuid = ?";
        String[] whereArgs = new String[]{category.categoryUUID.toString()};

        int rowsAffected = db.update("categories", values, whereClause, whereArgs);
    }

    public void deleteCategory(Category category) {
        int rowsAffected = db.delete("categories", "category_uuid = ?", new String[]{category.categoryUUID.toString()});
    }

    public void deleteAll() {
        int rowsAffected = db.delete("categories", null, null);
    }

    public List<Category> filterCategories(String searchText) {
        List<Category> filteredCategories = new ArrayList<>();
        String selection = "(category_name LIKE ? OR parent_category LIKE ?)";
        String[] selectionArgs = new String[]{"%" + searchText + "%", "%" + searchText + "%"}; // Use wildcards for "contains"

        Cursor cursor = db.query("categories", null, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Category category = getCategoryFromCursor(cursor);
                filteredCategories.add(category);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return filteredCategories;
    }

    public List<String> getDistinctParentCategories() {
        List<String> parentCategories = new ArrayList<>();
        try (Cursor cursor = db.rawQuery("SELECT DISTINCT parent_category FROM categories WHERE parent_category IS NOT NULL", null)) {
            if (cursor.moveToFirst()) {
                do {
                    String parentCategoryName = cursor.getString(cursor.getColumnIndexOrThrow("parent_category"));
                    parentCategories.add(parentCategoryName);
                } while (cursor.moveToNext());
            }
        }
        return parentCategories;
    }
}
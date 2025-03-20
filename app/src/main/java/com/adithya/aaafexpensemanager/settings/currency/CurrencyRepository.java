package com.adithya.aaafexpensemanager.settings.currency;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.adithya.aaafexpensemanager.settings.currency.exception.CurrencyExistsException;
import com.adithya.aaafexpensemanager.util.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

/** @noinspection unused, FieldCanBeLocal , CallToPrintStackTrace */
public class CurrencyRepository {
    private final SQLiteDatabase db;
    private final Application application;

    public CurrencyRepository(Application application) {
        //noinspection RedundantExplicitVariableType,resource
        DatabaseHelper dbHelper = new DatabaseHelper(application);
        this.application = application;
        db = dbHelper.getWritableDatabase();
    }

    public List<Currency> getAllCurrencies() {
        List<Currency> currencies = new ArrayList<>();
        try (Cursor cursor = db.query("currency_all_details", null, null, null, null, null, null)) {
            if (cursor.moveToFirst()) {
                do {
                    Currency currency = getCurrencyFromCursor(cursor);
                    if (currency != null) {
                        currencies.add(currency);
                    }
                } while (cursor.moveToNext());
            }
        }
        return currencies;
    }

    public Currency getCurrencyById(String currencyName) {
        Currency currency = null;
        try (Cursor cursor = db.query("currency_all_details", null, "currency_name = ?", new String[]{currencyName}, null, null, null)) {
            if (cursor.moveToFirst()) {
                currency = getCurrencyFromCursor(cursor);
            }
        }
        return currency;
    }

    private Currency getCurrencyFromCursor(Cursor cursor) {
        try {
            String currencyName = cursor.getString(cursor.getColumnIndexOrThrow("currency_name"));
            boolean isPrimary;
            isPrimary = cursor.getInt(cursor.getColumnIndexOrThrow("is_primary")) == 1;
            double conversionFactor = cursor.getDouble(cursor.getColumnIndexOrThrow("conversion_factor"));
            String primaryCurrencyName = cursor.getString(cursor.getColumnIndexOrThrow("primary_currency_name"));
            return new Currency(currencyName,isPrimary,conversionFactor,primaryCurrencyName);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public void setPrimaryCurrency(String currencyName){
        try {
            db.delete("primary_currency", null, null);
            ContentValues contentValuesPrimary = new ContentValues();
            contentValuesPrimary.put("primary_currency_name", currencyName);
            db.insertOrThrow("primary_currency", null, contentValuesPrimary);
            updateConversionFactors(currencyName);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateConversionFactors(String primaryCurrencyName) {
        try{
            Currency primaryCurrency = getCurrencyById(primaryCurrencyName);
            List<Currency> currencies = getAllCurrencies()
                    .stream()
                    .filter( currency -> {return !(currency.currencyName.equals(primaryCurrencyName));})
                    .toList();
            for(Currency currency:currencies){
                String selection = "currency_name = ?";
                String[] selectionArgs = new String[]{currency.currencyName};
                ContentValues values = new ContentValues();
                double conversionFactor = currency.conversionFactor / primaryCurrency.conversionFactor;
                values.put("conversion_factor", Math.round(conversionFactor*1000000)/1000000.0);
                db.update("currency", values, selection, selectionArgs);
            }
            primaryCurrency.conversionFactor = 1.0;
            updateCurrency(primaryCurrency);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void updateCurrency(Currency currency) {
        try{
            String selection = "currency_name = ?";
            String[] selectionArgs = new String[]{currency.currencyName};
            ContentValues values = new ContentValues();
            values.put("currency_name", currency.currencyName);
            values.put("conversion_factor", Math.round(currency.conversionFactor*1000000)/1000000.0);
            db.update("currency", values, selection, selectionArgs);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void addCurrency(Currency currency) throws CurrencyExistsException{
        boolean isRecordExists = checkCurrencyExists(currency.currencyName);
        if(isRecordExists){
            throw new CurrencyExistsException(currency.currencyName);
        }
        if(!checkPrimaryCurrencyExists()){
            currency.isPrimary =true;
            currency.conversionFactor = 1.0;
            setPrimaryCurrency(currency.currencyName);
        }
        ContentValues values = new ContentValues();
        values.put("currency_name", currency.currencyName);
        values.put("conversion_factor", Math.round(currency.conversionFactor*1000000)/1000000.0);
        long result = db.insert("currency", null, values);
    }

    public boolean checkCurrencyExists(String currencyName) {
        String selection = "(currency_name = ?)";
        String[] selectionArgs = new String[]{currencyName};
        try(Cursor cursor = db.query("currency", null, selection, selectionArgs, null, null, null)){
            if(cursor.getCount() > 0){
                return true;
            }
        }
        return false;
    }
    public boolean checkPrimaryCurrencyExists(){
        try(Cursor cursor = db.query("primary_currency", null, null, null, null, null, null)){
            return cursor.getCount() > 0;
        }
    }
    public void deleteCurrency(Currency currency) {
        int rowsAffected = db.delete("currency", "currency_name = ?", new String[]{currency.currencyName});
    }

    public void deleteAll(){
        int rowsAffected = db.delete("currency", null, null);
        rowsAffected = db.delete("primary_currency", null, null);
    }

    public List<Currency> filterCurrencies(String searchText) {
        List<Currency> filteredCurrencies = new ArrayList<>();
        String selection = "currency_name LIKE ?";
        String[] selectionArgs = new String[]{"%" + searchText + "%"};

        Cursor cursor = db.query("currency_all_details", null, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Currency currency = getCurrencyFromCursor(cursor);
                filteredCurrencies.add(currency);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return filteredCurrencies;
    }

    public String getPrimaryCurrency() {
        try{
            Cursor cursor = db.query("primary_currency", null, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndexOrThrow("primary_currency_name"));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return "N/A";
    }
}
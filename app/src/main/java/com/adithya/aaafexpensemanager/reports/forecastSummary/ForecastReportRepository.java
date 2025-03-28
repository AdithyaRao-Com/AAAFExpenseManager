package com.adithya.aaafexpensemanager.reports.forecastSummary;

import android.app.Application;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.adithya.aaafexpensemanager.transactionFilter.TransactionFilter;
import com.adithya.aaafexpensemanager.transactionFilter.TransactionFilterUtils;
import com.adithya.aaafexpensemanager.util.DatabaseHelper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/** @noinspection unused, CallToPrintStackTrace , FieldCanBeLocal */
public class ForecastReportRepository {
    /** @noinspection FieldCanBeLocal*/
    private final SQLiteDatabase db;
    private final Application application;
    private final String DATA_QUERY_STRING = "SELECT SplitTransfers.*,ROUND(running_sum,2) signed_amount_sum\n" +
            "  FROM\n" +
            "(SELECT SplitTransfers.*,\n" +
            "      SUM(\n" +
            "\t\tROUND(signed_amount/SplitTransfers.conversion_factor,2)) \n" +
            "\t   OVER(ORDER BY transaction_date) running_sum,\n" +
            "\t   ROW_NUMBER() OVER(\n" +
            "\t   PARTITION BY transaction_date \n" +
            "\t   ORDER BY transaction_uuid) row_num\n" +
            "  FROM SplitAllTransfers SplitTransfers\n" +
            " WHERE <<ALL_OTHER_FILTERS>>\n" +
            "ORDER BY transaction_date) SplitTransfers\n" +
            "WHERE row_num = 1\n" +
            "<<FROM_DATE_FILTER>>\n" +
            " ORDER BY transaction_date ASC";
    public ForecastReportRepository(Application application){
        //noinspection resource
        DatabaseHelper dbHelper = new DatabaseHelper(application);
        db = dbHelper.getReadableDatabase();
        this.application = application;
    }
    public List<ForecastReportRecord> getForecastReportData(TransactionFilter transactionFilters) {
        List<ForecastReportRecord> forecastReportRecords = new ArrayList<>();
        HashMap<String, Object> queryAllData = TransactionFilterUtils.generateTransactionFilterFutureQuery(transactionFilters,this.application);
        String queryString = Objects.requireNonNull(queryAllData.get("QUERY")).toString();
        //noinspection unchecked
        ArrayList<String> queryParms = (ArrayList<String>) queryAllData.get("VALUES");
        assert queryParms != null;
        String finalQueryString = DATA_QUERY_STRING.replace("<<ALL_OTHER_FILTERS>>",queryString);
        finalQueryString = finalQueryString.replace("<<FROM_DATE_FILTER>>",getFromDateFilter(transactionFilters));
        try (Cursor cursor = db.rawQuery(finalQueryString, queryParms.toArray(new String[0]))){
            if (cursor.moveToFirst()) {
                do {
                    ForecastReportRecord forecastReportRecord = getForecastReportDataFromCursor(cursor);
                    if (forecastReportRecord != null) {
                        forecastReportRecords.add(forecastReportRecord);
                    }
                } while (cursor.moveToNext());
            }
        }
        return forecastReportRecords;
    }

    private ForecastReportRecord getForecastReportDataFromCursor(Cursor cursor) {
        try {
            int transactionDateIndex = cursor.getColumnIndexOrThrow("transaction_date");
            int amountIndex = cursor.getColumnIndexOrThrow("signed_amount_sum");
            int currencyIndex = cursor.getColumnIndexOrThrow("primary_currency_code");
            int transactionDate = cursor.getInt(transactionDateIndex);
            if(transactionDate==0){
                return null;
            }
            LocalDate transactionDateObj = LocalDate.parse(String.valueOf(transactionDate), DateTimeFormatter.ofPattern("yyyyMMdd"));
            double amount = cursor.getDouble(amountIndex);
            String currency = cursor.getString(currencyIndex);
            return new ForecastReportRecord(
                    transactionDateObj,
                    amount,
                    currency);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private String getFromDateFilter(TransactionFilter transactionFilters) {
        if(transactionFilters.fromTransactionDate==0){
            transactionFilters.fromTransactionDate = Integer.parseInt(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        }
        String FROM_DATE_TEMPLATE = " AND transaction_date >= <<FROM_DATE>>";
        return FROM_DATE_TEMPLATE.replace("<<FROM_DATE>>",String.valueOf(transactionFilters.fromTransactionDate));
    }
}

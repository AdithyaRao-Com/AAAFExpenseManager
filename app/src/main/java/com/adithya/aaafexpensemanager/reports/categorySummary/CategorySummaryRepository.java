package com.adithya.aaafexpensemanager.reports.categorySummary;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.adithya.aaafexpensemanager.transaction.Transaction;
import com.adithya.aaafexpensemanager.transaction.TransactionRepository;
import com.adithya.aaafexpensemanager.transactionFilter.TransactionFilter;
import com.adithya.aaafexpensemanager.util.DatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** @noinspection unused*/
public class CategorySummaryRepository {
    /** @noinspection FieldCanBeLocal*/
    private final SQLiteDatabase db;
    private final TransactionRepository transactionRepository;
    public CategorySummaryRepository(Application application){
        //noinspection resource
        DatabaseHelper dbHelper = new DatabaseHelper(application);
        db = dbHelper.getReadableDatabase();
        transactionRepository = new TransactionRepository(application);
    }
    public List<CategorySummaryTxnRecord> getMonthlySummaryCategoryWise
            (TransactionFilter transactionFilter,
             CategorySummaryTxnRecord.TimePeriod timePeriod){
        List<Transaction> transactions = transactionRepository.getAllTransactions(transactionFilter,-1);
        var categorySummaryTxnRecordHashMap = new HashMap<Integer, CategorySummaryTxnRecord>();
        double totalAmount = 0.0;
        for(Transaction transaction : transactions){
            totalAmount = totalAmount + ((double) Math.round(transaction.amount * 100.0) /100.0);
        }
        for(Transaction transaction : transactions){
            var startDateLocalDate = timePeriod.truncateToStart(transaction.getTransactionLocalDate());
            var endDateLocalDate = timePeriod.truncateToEnd(transaction.getTransactionLocalDate());
            int categorySummaryHashCode = new CategorySummaryTxnRecord(transaction.category,
                    startDateLocalDate,
                    endDateLocalDate).hashCode();
            var categorySummaryTxnRecord = categorySummaryTxnRecordHashMap.get(categorySummaryHashCode);
            double tempAmount;
            if(transaction.transactionType.equals("Income")){
                tempAmount = (double) Math.round(transaction.amount * 100.0) /100.0;
            }else{
                tempAmount = (double) Math.round(-1*transaction.amount * 100.0) /100.0;
            }
            if(categorySummaryTxnRecord==null){
                categorySummaryTxnRecord =  new CategorySummaryTxnRecord(
                        transaction.category,
                        tempAmount,
                        timePeriod.truncateToStart(transaction.getTransactionLocalDate()),
                        timePeriod.truncateToEnd(transaction.getTransactionLocalDate()),
                        (double) Math.round(tempAmount * 100 /totalAmount * 100.0) /100.0,
                        totalAmount);
            }
            else {
                categorySummaryTxnRecord.amount += tempAmount;
                categorySummaryTxnRecord.pct = (double) Math.round(Math.abs(categorySummaryTxnRecord.amount) * 100.0 /totalAmount * 100.0) /100.0;
                categorySummaryTxnRecord.totalAmount = totalAmount;
            }
            categorySummaryTxnRecordHashMap.put(categorySummaryHashCode,categorySummaryTxnRecord);
        }
        List<CategorySummaryTxnRecord> records = categorySummaryTxnRecordHashMap.values().stream().toList();
        if(records==null){
            return new ArrayList<>();
        }
        else{
            return records;
        }
    }
}

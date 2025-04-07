package com.adithya.aaafexpensemanager.settings.importExportCSV;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.adithya.aaafexpensemanager.settings.category.CategoryRepository;
import com.adithya.aaafexpensemanager.transaction.Transaction;
import com.adithya.aaafexpensemanager.transaction.TransactionRepository;
import com.adithya.aaafexpensemanager.transactionFilter.TransactionFilter;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * @noinspection deprecation, UnusedReturnValue
 */
public class ExportCSVGenerator {
    public static boolean generateCSV(Context context, Uri fileUri) {
        List<ImportExportCSVRecord> records = getCSVRecords(context);
        String[] headers = getCSVHeaders().toArray(String[]::new);
        try (Writer writer = new OutputStreamWriter(context.getContentResolver().openOutputStream(fileUri));
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(headers))) {
            for (ImportExportCSVRecord row : records) {
                csvPrinter.printRecord((Object[]) row.getStringArray());
            }
            csvPrinter.flush();
            Log.d("ExportCSVGenerator", "CSV file generated successfully: " + fileUri);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("ExportCSVGenerator", "Error generating CSV file: " + e.getMessage());
            return false;
        }
    }

    private static List<String> getCSVHeaders() {
        List<String> headers = new ArrayList<>();
        headers.add("Date");
        headers.add("Transaction Name");
        headers.add("Amount");
        headers.add("Category Group Name");
        headers.add("Category");
        headers.add("Account");
        headers.add("Notes");
        return headers;
    }

    private static List<ImportExportCSVRecord> getCSVRecords(Context context) {
        List<ImportExportCSVRecord> records = new ArrayList<>();
        TransactionRepository transactionRepository = new TransactionRepository((Application) context.getApplicationContext());
        CategoryRepository categoryRepository = new CategoryRepository((Application) context.getApplicationContext());
        List<Transaction> transactions = transactionRepository.getAllTransactions(new TransactionFilter(), -1);
        for (Transaction transaction : transactions) {
            String parentCategoryName = categoryRepository.getCategoryByName(transaction.category).parentCategory;
            records.add(new ImportExportCSVRecord(transaction, parentCategoryName));
        }
        return records;
    }
}

package com.adithya.aaafexpensemanager.settings.importExportSchedules;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.adithya.aaafexpensemanager.recurring.RecurringRepository;
import com.adithya.aaafexpensemanager.recurring.RecurringSchedule;
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
public class ExportScheduleCSVGenerator {
    public static boolean generateCSV(Context context, Uri fileUri) {
        List<ImportExportScheduleCSVRecord> records = getCSVRecords(context);
        String[] headers = getCSVHeaders().toArray(String[]::new);
        try (Writer writer = new OutputStreamWriter(context.getContentResolver().openOutputStream(fileUri));
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(headers))) {
            for (ImportExportScheduleCSVRecord row : records) {
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
        headers.add("Transaction Name");
        headers.add("Recurring Frequency");
        headers.add("Repeat Interval");
        headers.add("Recurring Start Date");
        headers.add("Recurring End Date");
        headers.add("Transaction Type");
        headers.add("Amount");
        headers.add("Account Name");
        headers.add("To Account Name");
        headers.add("Category");
        headers.add("Notes");
        return headers;
    }

    private static List<ImportExportScheduleCSVRecord> getCSVRecords(Context context) {
        List<ImportExportScheduleCSVRecord> records = new ArrayList<>();
        RecurringRepository recurringRepository = new RecurringRepository((Application) context.getApplicationContext());
        List<RecurringSchedule> recurringSchedules = recurringRepository.getAllRecurringSchedules(new TransactionFilter(), -1);
        for (RecurringSchedule recurringSchedule : recurringSchedules) {
            records.add(new ImportExportScheduleCSVRecord(recurringSchedule));
        }
        return records;
    }
}

package com.adithya.aaafexpensemanager.settings.importExportQIF;

import android.app.Application;
import android.net.Uri;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** @noinspection CallToPrintStackTrace, FieldCanBeLocal */
public class QIFExporter {
    // TODO - Test functionality for QIF Exporter
    private final QIFImportExportRepository repository;
    private final Application application;
    public QIFExporter(Application application) {
        this.application = application;
        repository = new QIFImportExportRepository(application);
    }
    /** @noinspection UnusedReturnValue*/
    public boolean generateQIF(Uri fileUri) {
        List<QIFImportExportRecord> records = repository.getAllQIFImportExportRecords();
        try {
            OutputStream outputStream = application.getContentResolver().openOutputStream(fileUri);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            writeQIFHeader(writer);
            writeTransactions(writer,records);
            writer.close();
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    private void writeQIFHeader(BufferedWriter writer) throws IOException {
        List<QIFHeaderRecord> headerRecords = repository.getAllQIFHeaderRecords();
        for(QIFHeaderRecord record : headerRecords){
            writer.write("!Account\n");
            writer.write("N" + record.accountName + "\n");
            writer.write("T" + "Bank" + "\n");
            writer.write("^\n");
        }
    }
    private void writeTransactions(BufferedWriter writer, List<QIFImportExportRecord> records) throws IOException {
        for(QIFImportExportRecord record : records){
            writer.write("!Type:Bank\n"); // Assuming all transactions are bank. Adjust if needed.
            writer.write("N" + record.accountName + "\n");

            try {
                String formattedDate = record.transactionDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
                writer.write("D" + formattedDate + "\n");
            } catch (Exception e) {
                writer.write("D" + record.transactionDate + "\n");
            }
            writer.write("T" + record.amount + "\n");
            writer.write("P" + record.payee + "\n");
            writer.write("M" + record.memo + "\n");
            if (record.category != null && !record.category.isEmpty()) {
                writer.write("L" + record.category + "\n");
            }
            writer.write("^\n");
        }
    }
}

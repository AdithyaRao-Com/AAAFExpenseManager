package com.adithya.aaafexpensemanager.settings.importExportQIF;

import android.app.Application;
import android.net.Uri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

/** @noinspection FieldCanBeLocal*/
public class QIFImporter {
    // TODO - Test functionality for QIF Importer
    private final QIFImportExportRepository repository;
    private final Application application;
    public QIFImporter(Application application) {
        this.application = application;
        this.repository = new QIFImportExportRepository(application);
    }

    public boolean importQIF(Uri fileUri) {
        try {
            InputStream inputStream = application.getApplicationContext().getContentResolver().openInputStream(fileUri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)));
            String line;
            String accountName = null;
            String accountType = null;
            String date = null;
            String amount = null;
            String payee = null;
            String memo = null;
            String category = null;
            boolean inTransaction = false;
            repository.deleteAllQIFImportExportRecords();
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("!Account")) {
                    inTransaction = false;
                } else if (line.startsWith("N")) {
                    accountName = line.substring(1);
                } else if (line.startsWith("T") && !inTransaction) {
                    accountType = line.substring(1);
                } else if (line.startsWith("D")) {
                    date = line.substring(1);
                    inTransaction = true;
                } else if (line.startsWith("T") && inTransaction) {
                    amount = line.substring(1);
                } else if (line.startsWith("P")) {
                    payee = line.substring(1);
                } else if (line.startsWith("M")) {
                    memo = line.substring(1);
                } else if (line.startsWith("L")) {
                    category = line.substring(1);
                } else if (line.startsWith("^")) {
                    if (accountName != null && date != null && amount != null) {
                        try {
                            LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
                            QIFImportExportRecord record = new QIFImportExportRecord(accountName, accountType, localDate, amount, payee, memo, category);
                            repository.addQIFImportExportRecord(record);
                        } catch (DateTimeParseException e) {
                            try {
                                LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
                                QIFImportExportRecord record = new QIFImportExportRecord(accountName, accountType, localDate, amount, payee, memo, category);
                                repository.addQIFImportExportRecord(record);
                            } catch (DateTimeParseException e2) {
                                e2.printStackTrace();
                                return false;
                            }
                        }
                        date = null;
                        amount = null;
                        payee = null;
                        memo = null;
                        category = null;
                    }
                    inTransaction = false;
                }
            }
            reader.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
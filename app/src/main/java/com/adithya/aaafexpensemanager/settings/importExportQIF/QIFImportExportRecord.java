package com.adithya.aaafexpensemanager.settings.importExportQIF;

import android.annotation.SuppressLint;

import com.adithya.aaafexpensemanager.transaction.Transaction;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class QIFImportExportRecord {
    public String accountName;
    public String accountType;
    public LocalDate transactionDate;
    public String amount;
    public String payee;
    public String memo;
    public String category;

    public QIFImportExportRecord(String accountName, String accountType, LocalDate transactionDate, String amount, String payee, String memo, String category) {
        this.accountName = accountName;
        this.accountType = accountType;
        this.transactionDate = transactionDate;
        this.amount = amount;
        this.payee = payee;
        this.memo = memo;
        this.category = category;
    }

    public QIFImportExportRecord(String accountName, String accountType, String transactionDate, String amount, String payee, String memo, String category) {
        this.accountName = accountName;
        this.accountType = accountType;
        this.transactionDate = LocalDate.parse(transactionDate, DateTimeFormatter.ISO_LOCAL_DATE);
        this.amount = amount;
        this.payee = payee;
        this.memo = memo;
        this.category = category;
    }

    @SuppressLint("DefaultLocale")
    public QIFImportExportRecord(Transaction record) {
        this.accountName = record.accountName;
        this.accountType = "Bank";
        this.transactionDate = record.getTransactionLocalDate();
        this.amount = String.format("%.2f",record.amount);
        this.payee = record.transactionName;
        this.memo = record.notes;
        this.category = record.category;
    }
}
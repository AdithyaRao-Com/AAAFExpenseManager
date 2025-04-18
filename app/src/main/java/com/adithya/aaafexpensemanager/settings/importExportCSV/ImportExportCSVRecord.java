package com.adithya.aaafexpensemanager.settings.importExportCSV;

import com.adithya.aaafexpensemanager.account.Account;
import com.adithya.aaafexpensemanager.settings.category.Category;
import com.adithya.aaafexpensemanager.transaction.Transaction;

import org.apache.commons.csv.CSVRecord;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class ImportExportCSVRecord {
    public String type;
    public String date;
    public String time;
    public String title;
    public String amount;
    public String currency;
    public String exchangeRate;
    public String categoryGroupName;
    public String category;
    public String account;
    public String notes;
    public String labels;
    public String status;

    public ImportExportCSVRecord(String type, String date, String time, String title, String amount, String currency, String exchangeRate, String categoryGroupName, String category, String account, String notes, String labels, String status) {
        this.type = type;
        this.date = date;
        this.time = time;
        this.title = title;
        this.amount = amount;
        this.currency = currency;
        this.exchangeRate = exchangeRate;
        this.categoryGroupName = categoryGroupName;
        this.category = category;
        this.account = account;
        this.notes = notes;
        this.labels = labels;
        this.status = status;
    }

    public ImportExportCSVRecord(CSVRecord record, CSV_VERSION version) {
        if (version == CSV_VERSION.V1) {
            this.type = record.get(0);
            this.date = record.get(1);
            this.time = record.get(2);
            this.title = record.get(3);
            this.amount = record.get(4);
            this.currency = record.get(5);
            this.exchangeRate = record.get(6);
            this.categoryGroupName = record.get(7);
            this.category = record.get(8);
            this.account = record.get(9);
            this.notes = record.get(10);
            this.labels = record.get(11);
            this.status = record.get(12);
        } else if (version == CSV_VERSION.V2) {
            this.date = record.get(0);
            this.title = record.get(1);
            this.amount = record.get(2);
            this.categoryGroupName = record.get(3);
            this.category = record.get(4);
            this.account = record.get(5);
            this.notes = record.get(6);
            if (Double.parseDouble(this.amount) < 0.0) {
                this.type = "Expense";
            } else {
                this.type = "Income";
            }
        }
    }

    public ImportExportCSVRecord(Transaction transaction, String parentCategoryName) {
        this.type = transaction.transactionType;
        this.date = transaction.getFormattedTransactionDateYYYY_MM_DD();
        this.time = "00:01";
        this.title = transaction.transactionName;
        if (transaction.transactionType.equals("Income")) {
            this.amount = String.valueOf(transaction.amount);
        } else {
            this.amount = String.valueOf(-1 * transaction.amount);
        }
        this.currency = "INR";
        this.exchangeRate = "1";
        this.categoryGroupName = parentCategoryName;
        this.category = transaction.category;
        this.account = transaction.accountName;
        this.notes = transaction.notes;
        this.labels = "";
    }

    public LocalDate getTransactionDate() {
        try {
            try {
                return LocalDate.parse(this.date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (DateTimeParseException ignored) {
            }
            try {
                return LocalDate.parse(this.date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } catch (DateTimeParseException ignored) {
            }
            try {
                return LocalDate.parse(this.date, DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
            } catch (DateTimeParseException ignored) {
            }
        } catch (Exception ignored) {
        }
        return LocalDate.now().minusYears(1);
    }

    public double getAmount() {
        try {
            return Double.parseDouble(this.amount);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public double getAbsAmount() {
        return Math.abs(getAmount());
    }

    public String getTransactionType() {
        if (this.type.equals("Transfer")) {
            if (this.getAmount() <= 0.0) {
                return "Expense";
            } else {
                return "Income";
            }
        } else if (this.type.isBlank()) {
            if (this.getAmount() <= 0.0) {
                return "Expense";
            } else {
                return "Income";
            }
        } else {
            return this.type;
        }
    }

    public Transaction toTransaction() {
        return new Transaction(this.title,
                getTransactionDate(),
                getTransactionType(),
                this.category,
                this.notes,
                getAbsAmount(),
                this.account,
                "", "", "");
    }

    public Category toCategory() {
        return new Category(this.category, this.categoryGroupName);
    }

    public Account toAccount(String defaultCurrency) {
        return new Account(this.account,
                "Cash",
                0.0,
                "",
                999,
                defaultCurrency,
                false,
                false);
    }

    public String[] getStringArray() {
        List<String> headers = new ArrayList<>();
        headers.add(this.date);
        headers.add(this.title);
        headers.add(this.amount);
        headers.add(this.categoryGroupName);
        headers.add(this.category);
        headers.add(this.account);
        headers.add(this.notes);
        return headers.toArray(String[]::new);
    }

    public enum CSV_VERSION {
        V1(),
        V2()
    }
}


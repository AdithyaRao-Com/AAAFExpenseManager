package com.adithya.aaafexpensemanager.importdata;

import com.adithya.aaafexpensemanager.account.Account;
import com.adithya.aaafexpensemanager.settings.category.Category;
import com.adithya.aaafexpensemanager.transaction.Transaction;

import org.apache.commons.csv.CSVRecord;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ImportDataRecord {
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

    public ImportDataRecord(String type, String date, String time, String title, String amount, String currency, String exchangeRate, String categoryGroupName, String category, String account, String notes, String labels, String status) {
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

    public ImportDataRecord(CSVRecord record) {
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
    }

    public LocalDate getTransactionDate() {
        try {
            return LocalDate.parse(this.date, DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
        } catch (DateTimeParseException e) {
            try{
                return LocalDate.parse(this.date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
            catch (Exception e1){
                return LocalDate.now().minusDays(365);
            }
        }
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
        if(this.type.equals("Transfer")){
            if(this.getAmount()<= 0.0){
                return "Expense";
            }
            else{
                return "Income";
            }
        }
        else{
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
                "","","");
    }

    public Category toCategory(){
        return new Category(this.category,this.categoryGroupName);
    }

    public Account toAccount(String defaultCurrency){
        return new Account(this.account,
                "Cash",
                0.0,
                "",
                999,
                defaultCurrency,
                false,
                false);
    }
}


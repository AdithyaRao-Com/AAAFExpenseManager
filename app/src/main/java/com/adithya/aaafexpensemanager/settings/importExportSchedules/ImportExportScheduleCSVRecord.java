package com.adithya.aaafexpensemanager.settings.importExportSchedules;

import android.annotation.SuppressLint;

import com.adithya.aaafexpensemanager.recurring.RecurringSchedule;

import org.apache.commons.csv.CSVRecord;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ImportExportScheduleCSVRecord {
    public String transactionName;
    public String recurringFrequency;
    public String repeatInterval;
    public String recurringStartDate;
    public String recurringEndDate;
    public String transactionType;
    public String amount;
    public String accountName;
    public String toAccountName;
    public String category;
    public String notes;

    @SuppressLint("DefaultLocale")
    public ImportExportScheduleCSVRecord(RecurringSchedule recurringSchedule) {
        this.transactionName = recurringSchedule.transactionName;
        this.recurringFrequency = recurringSchedule.recurringScheduleName;
        this.repeatInterval = Integer.toString(recurringSchedule.repeatIntervalDays);
        this.recurringStartDate = convertDateToString(recurringSchedule.getRecurringStartDateLocalDate());
        this.recurringEndDate = convertDateToString(recurringSchedule.getRecurringEndDateLocalDate());
        this.transactionType = recurringSchedule.transferInd;
        this.amount = String.format("%.2f", recurringSchedule.amount);
        this.accountName = recurringSchedule.accountName;
        this.toAccountName = recurringSchedule.toAccountName;
        this.category = recurringSchedule.category;
        this.notes = recurringSchedule.notes;
    }

    public ImportExportScheduleCSVRecord(CSVRecord value) {
        this.transactionName = value.get(0);
        this.recurringFrequency = value.get(1);
        this.repeatInterval = value.get(2);
        this.recurringStartDate = value.get(3);
        this.recurringEndDate = value.get(4);
        this.transactionType = value.get(5);
        this.amount = value.get(6);
        this.accountName = value.get(7);
        this.toAccountName = value.get(8);
        this.category = value.get(9);
        this.notes = value.get(10);
    }

    public static String convertDateToString(LocalDate inputDate) {
        return inputDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public LocalDate getRecurringStartDate() {
        return LocalDate.parse(recurringStartDate, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public LocalDate getRecurringEndDate() {
        return LocalDate.parse(recurringEndDate, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public String[] getStringArray() {
        return new String[]{
                transactionName,
                recurringFrequency,
                repeatInterval,
                recurringStartDate,
                recurringEndDate,
                transactionType,
                amount,
                accountName,
                toAccountName,
                category,
                notes
        };
    }

    public RecurringSchedule toRecurringSchedule() {
        return new RecurringSchedule(
                this.transactionName,
                this.recurringFrequency,
                this.getRepeatInterval(),
                this.getRecurringStartDate(),
                this.getRecurringEndDate(),
                this.category,
                this.notes,
                this.transactionType,
                this.getAmount(),
                this.accountName,
                this.getToAccountName(),
                this.transactionType);
    }

    private String getToAccountName() {
        if (toAccountName == null || toAccountName.isEmpty()) {
            return "";
        } else {
            return toAccountName;
        }
    }

    private double getAmount() {
        try {
            return Math.round(100.00 * Double.parseDouble(amount)) / 100.00;
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private int getRepeatInterval() {
        try {
            return Integer.parseInt(repeatInterval);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}


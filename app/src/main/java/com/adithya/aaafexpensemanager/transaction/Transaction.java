package com.adithya.aaafexpensemanager.transaction;

import android.os.Parcel;
import android.os.Parcelable;

import com.adithya.aaafexpensemanager.util.CurrencyFormatter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.UUID;

/**
 * @noinspection CallToPrintStackTrace, unused
 */
public class Transaction implements Parcelable {
    public static final Parcelable.Creator<Transaction> CREATOR = new Parcelable.Creator<>() {
        @Override
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };
    public UUID transactionUUID;
    public String transactionName;
    public int transactionDate;
    public String transactionType;
    public String category;
    public String notes;
    public double amount;
    public String accountName;
    public String toAccountName;
    public long createDateTime;
    public String transferInd;
    public long lastUpdateDateTime;
    public String recurringScheduleUUID;
    public String currencyCode;
    public double conversionFactor;
    public String primaryCurrencyCode;
    public double runningBalance;

    public Transaction(String transactionName, LocalDate transactionDate, String transactionType, String category, String notes, double amount, String accountName, String toAccountName, String transferInd, String recurringScheduleUUID) {
        this.transactionUUID = UUID.randomUUID();
        this.transactionName = transactionName;
        this.transactionDate = Integer.parseInt(transactionDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        this.transactionType = transactionType;
        this.transferInd = transferInd;
        this.category = category;
        this.notes = notes;
        this.amount = amount;
        this.accountName = accountName;
        this.toAccountName = toAccountName;
        this.createDateTime = System.currentTimeMillis();
        this.lastUpdateDateTime = this.createDateTime;
        this.recurringScheduleUUID = recurringScheduleUUID;
    }

    public Transaction(UUID transactionUUID, String transactionName, int transactionDate,
                       String transactionType, String category, String notes, double amount,
                       String accountName, String toAccountName, long createDateTime,
                       long lastUpdateDateTime, String transferInd, String recurringScheduleUUID,
                       String currencyCode, double conversionFactor, String primaryCurrencyCode, double runningBalance) {
        this.transactionUUID = transactionUUID;
        this.transactionName = transactionName;
        this.transactionDate = transactionDate;
        this.transactionType = transactionType;
        this.transferInd = transferInd;
        this.category = category;
        this.notes = notes;
        this.amount = amount;
        this.accountName = accountName;
        this.toAccountName = toAccountName;
        this.createDateTime = createDateTime;
        this.lastUpdateDateTime = lastUpdateDateTime;
        this.recurringScheduleUUID = recurringScheduleUUID;
        this.currencyCode = currencyCode;
        this.conversionFactor = conversionFactor;
        this.primaryCurrencyCode = primaryCurrencyCode;
        this.runningBalance = runningBalance;
    }

    public Transaction(Transaction transaction) {
        this.transactionUUID = UUID.randomUUID();
        this.transactionName = transaction.transactionName;
        this.transactionDate = Integer.parseInt(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        this.transactionType = transaction.transactionType;
        this.transferInd = transaction.transferInd;
        this.category = transaction.category;
        this.notes = transaction.notes;
        this.amount = transaction.amount;
        this.accountName = transaction.accountName;
        this.toAccountName = transaction.toAccountName;
        this.createDateTime = System.currentTimeMillis();
        this.lastUpdateDateTime = this.createDateTime;
        this.recurringScheduleUUID = "";
        this.currencyCode = transaction.currencyCode;
        this.conversionFactor = transaction.conversionFactor;
        this.primaryCurrencyCode = transaction.primaryCurrencyCode;
        this.runningBalance = transaction.runningBalance;
    }

    // Parcelable implementation
    protected Transaction(Parcel in) {
        String uuidStr = in.readString();
        this.transactionUUID = uuidStr != null ? UUID.fromString(uuidStr) : null;
        this.transactionName = in.readString();
        this.transactionDate = in.readInt();
        this.transactionType = in.readString();
        this.transferInd = in.readString();
        this.category = in.readString();
        this.notes = in.readString();
        this.amount = in.readDouble();
        this.accountName = in.readString();
        this.toAccountName = in.readString();
        this.createDateTime = in.readLong();
        this.lastUpdateDateTime = in.readLong();
        this.recurringScheduleUUID = in.readString();
        this.currencyCode = in.readString();
        this.conversionFactor = in.readDouble();
        this.primaryCurrencyCode = in.readString();
        this.runningBalance = in.readDouble();
    }

    public double getSignedAmount() {
        if (transactionType.equals("Income")) {
            return amount;
        } else {
            return -1 * amount;
        }
    }

    public LocalDate getTransactionLocalDate() {
        try {
            return LocalDate.parse(String.valueOf(transactionDate), DateTimeFormatter.ofPattern("yyyyMMdd"));
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getFormattedTransactionDate() {
        LocalDate localDate = getTransactionLocalDate();
        return localDate != null ? localDate.format(DateTimeFormatter.ofPattern("dd/MMM/yyyy")) : "";
    }

    public String getFormattedTransactionDateYYYY_MM_DD() {
        LocalDate localDate = getTransactionLocalDate();
        return localDate != null ? localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(transactionUUID != null ? transactionUUID.toString() : null);
        dest.writeString(transactionName);
        dest.writeInt(transactionDate);
        dest.writeString(transactionType);
        dest.writeString(transferInd);
        dest.writeString(category);
        dest.writeString(notes);
        dest.writeDouble(amount);
        dest.writeString(accountName);
        dest.writeString(toAccountName);
        dest.writeLong(createDateTime);
        dest.writeLong(lastUpdateDateTime);
        dest.writeString(recurringScheduleUUID);
        dest.writeString(currencyCode);
        dest.writeDouble(conversionFactor);
        dest.writeString(primaryCurrencyCode);
        dest.writeDouble(runningBalance);
    }

    public String getDateE_MMMM_dd_yyyy() {
        LocalDate localDate = getTransactionLocalDate();
        return localDate != null ? localDate.format(DateTimeFormatter.ofPattern("E dd MMM yyyy")) : "";
    }

    public String amountToIndianFormat() {
        return CurrencyFormatter.formatIndianStyle(this.amount, this.currencyCode);
    }

    public String amountToStandardFormat() {
        return CurrencyFormatter.formatStandardStyle(this.amount, this.currencyCode);
    }
}
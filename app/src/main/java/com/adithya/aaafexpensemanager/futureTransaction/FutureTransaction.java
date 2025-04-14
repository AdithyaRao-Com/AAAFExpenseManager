package com.adithya.aaafexpensemanager.futureTransaction;

import android.os.Parcel;
import android.os.Parcelable;

import com.adithya.aaafexpensemanager.recurring.RecurringSchedule;
import com.adithya.aaafexpensemanager.transaction.Transaction;
import com.adithya.aaafexpensemanager.util.CurrencyFormatter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.UUID;

/**
 * @noinspection CallToPrintStackTrace
 */
public class FutureTransaction implements Parcelable {
    public static final Parcelable.Creator<FutureTransaction> CREATOR = new Parcelable.Creator<>() {
        @Override
        public FutureTransaction createFromParcel(Parcel in) {
            return new FutureTransaction(in);
        }

        @Override
        public FutureTransaction[] newArray(int size) {
            return new FutureTransaction[size];
        }
    };
    public UUID transactionUUID;
    public UUID recurringScheduleUUID;
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
    public String currencyCode;
    public double conversionFactor;
    public String primaryCurrencyCode;

    public FutureTransaction(UUID recurringScheduleUUID, String transactionName, LocalDate transactionDate, String transactionType, String category, String notes, double amount, String accountName, String toAccountName, String transferInd) {
        this(UUID.randomUUID(), recurringScheduleUUID, transactionName,
                Integer.parseInt(transactionDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"))),
                transactionType, category, notes, amount, accountName, toAccountName,
                System.currentTimeMillis(), System.currentTimeMillis(), transferInd,
                "", 1.0, "");
    }

    public FutureTransaction(UUID transactionUUID, UUID recurringScheduleUUID,
                             String transactionName, int transactionDate, String transactionType,
                             String category, String notes, double amount, String accountName,
                             String toAccountName, long createDateTime, long lastUpdateDateTime,
                             String transferInd, String currencyCode, double conversionFactor,
                             String primaryCurrencyCode) {
        this.transactionUUID = transactionUUID;
        this.recurringScheduleUUID = recurringScheduleUUID;
        this.transactionName = transactionName;
        if (transactionDate == 0) {
            transactionDate = Integer.parseInt(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        }
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
        this.currencyCode = currencyCode;
        this.conversionFactor = conversionFactor;
        this.primaryCurrencyCode = primaryCurrencyCode;
    }

    public FutureTransaction(RecurringSchedule recurringSchedule, LocalDate transactionDate) {
        this.transactionUUID = UUID.randomUUID();
        this.recurringScheduleUUID = recurringSchedule.recurringScheduleUUID;
        this.transactionName = recurringSchedule.transactionName;
        this.transactionDate = Integer.parseInt(transactionDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        this.transactionType = recurringSchedule.transactionType;
        this.transferInd = recurringSchedule.transferInd;
        this.category = recurringSchedule.category;
        this.notes = recurringSchedule.notes;
        this.amount = recurringSchedule.amount;
        this.accountName = recurringSchedule.accountName;
        this.toAccountName = recurringSchedule.toAccountName;
        this.createDateTime = System.currentTimeMillis();
        this.lastUpdateDateTime = this.createDateTime;
        this.currencyCode = recurringSchedule.currencyCode;
        this.conversionFactor = recurringSchedule.conversionFactor;
        this.primaryCurrencyCode = recurringSchedule.primaryCurrencyCode;
    }

    // Parcelable implementation
    protected FutureTransaction(Parcel in) {
        String uuidStr = in.readString();
        this.transactionUUID = uuidStr != null ? UUID.fromString(uuidStr) : null;
        this.recurringScheduleUUID = UUID.fromString(in.readString());
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
        this.currencyCode = in.readString();
        this.conversionFactor = in.readDouble();
        this.primaryCurrencyCode = in.readString();
    }

    public LocalDate getTransactionLocalDate() {
        try {
            return LocalDate.parse(String.valueOf(transactionDate), DateTimeFormatter.ofPattern("yyyyMMdd"));
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setTransactionLocalDate(LocalDate transactionDate) {
        this.transactionDate = Integer.parseInt(transactionDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
    }

    public String getFormattedTransactionDate() {
        LocalDate localDate = getTransactionLocalDate();
        return localDate != null ? localDate.format(DateTimeFormatter.ofPattern("dd/MMM/yyyy")) : "";
    }

    public String getFormattedTransactionDateYYYY_MM_DD() {
        LocalDate localDate = getTransactionLocalDate();
        return localDate != null ? localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "";
    }

    public int getFormattedTransactionDate_YYYYMMDD() {
        LocalDate localDate = getTransactionLocalDate();
        return localDate != null ? Integer.parseInt(localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"))) : 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(transactionUUID != null ? transactionUUID.toString() : null);
        dest.writeString(recurringScheduleUUID.toString());
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
        dest.writeString(currencyCode);
        dest.writeDouble(conversionFactor);
        dest.writeString(primaryCurrencyCode);
    }

    public String getDateE_MMMM_dd_yyyy() {
        LocalDate localDate = getTransactionLocalDate();
        return localDate != null ? localDate.format(DateTimeFormatter.ofPattern("E dd MMM yyyy")) : "";
    }

    public Transaction getTransaction() {
        return new Transaction(this.transactionUUID,
                this.transactionName,
                this.transactionDate,
                this.transactionType,
                this.category,
                this.notes,
                this.amount,
                this.accountName,
                this.toAccountName,
                this.createDateTime,
                this.lastUpdateDateTime,
                this.transferInd,
                this.recurringScheduleUUID.toString(),
                this.currencyCode,
                this.conversionFactor,
                this.primaryCurrencyCode,
                0.0d);
    }

    public String amountToIndianFormat() {
        return CurrencyFormatter.formatIndianStyle(this.amount, "INR");
    }

    public String amountToStandardFormat() {
        return CurrencyFormatter.formatStandardStyle(this.amount, "INR");
    }
}
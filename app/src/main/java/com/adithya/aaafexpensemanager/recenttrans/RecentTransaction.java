package com.adithya.aaafexpensemanager.recenttrans;

import android.os.Parcel;
import android.os.Parcelable;

import com.adithya.aaafexpensemanager.transaction.Transaction;

public class RecentTransaction implements Parcelable {
    public String transactionName;
    public String transactionType;
    public String category;
    public String notes;
    public double amount;
    public String accountName;
    public String toAccountName;
    public long createDateTime;
    public long lastUpdateDateTime;
    public RecentTransaction(String transactionName, String transactionType, String category, String notes, double amount, String accountName, String toAccountName) {
        this.transactionName = transactionName;
        this.transactionType = transactionType;
        this.category = category;
        this.notes = notes;
        this.amount = amount;
        this.accountName = accountName;
        this.toAccountName = toAccountName;
        this.createDateTime = System.currentTimeMillis();
        this.lastUpdateDateTime = this.createDateTime;
    }

    public RecentTransaction(String transactionName, String transactionType, String category, String notes, double amount, String accountName, String toAccountName, long createDateTime, long lastUpdateDateTime) {
        this.transactionName = transactionName;
        this.transactionType = transactionType;
        this.category = category;
        this.notes = notes;
        this.amount = amount;
        this.accountName = accountName;
        this.toAccountName = toAccountName;
        this.createDateTime = createDateTime;
        this.lastUpdateDateTime = lastUpdateDateTime;
    }

    public RecentTransaction(Transaction transaction) {
        this.transactionName = transaction.transactionName;
        this.transactionType = transaction.transactionType;
        this.category = transaction.category;
        this.notes = transaction.notes;
        this.amount = transaction.amount;
        this.accountName = transaction.accountName;
        this.toAccountName = transaction.toAccountName;
        this.createDateTime = System.currentTimeMillis();
        this.lastUpdateDateTime = System.currentTimeMillis();
    }
    // Parcelable implementation
    protected RecentTransaction(Parcel in) {
        this.transactionName = in.readString();
        this.transactionType = in.readString();
        this.category = in.readString();
        this.notes = in.readString();
        this.amount = in.readDouble();
        this.accountName = in.readString();
        this.toAccountName = in.readString();
        this.createDateTime = in.readLong();
        this.lastUpdateDateTime = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(transactionName);
        dest.writeString(transactionType);
        dest.writeString(category);
        dest.writeString(notes);
        dest.writeDouble(amount);
        dest.writeString(accountName);
        dest.writeString(toAccountName);
        dest.writeLong(createDateTime);
        dest.writeLong(lastUpdateDateTime);
    }

    public static final Creator<RecentTransaction> CREATOR = new Creator<>() {
        @Override
        public RecentTransaction createFromParcel(Parcel in) {
            return new RecentTransaction(in);
        }

        @Override
        public RecentTransaction[] newArray(int size) {
            return new RecentTransaction[size];
        }
    };
}
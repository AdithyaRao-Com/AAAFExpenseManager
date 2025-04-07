package com.adithya.aaafexpensemanager.transactionFilter;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TransactionFilter implements Parcelable,Cloneable {
    public static final Parcelable.Creator<TransactionFilter> CREATOR = new Parcelable.Creator<>() {
        @Override
        public TransactionFilter createFromParcel(Parcel in) {
            return new TransactionFilter(in);
        }

        @Override
        public TransactionFilter[] newArray(int size) {
            return new TransactionFilter[size];
        }
    };
    public ArrayList<String> transactionNames;
    public int fromTransactionDate;
    public int toTransactionDate;
    public ArrayList<String> categories;
    public ArrayList<String> accountNames;
    public ArrayList<String> toAccountNames;
    public double fromAmount;
    public double toAmount;
    public ArrayList<String> transactionTypes;
    public String searchText;
    public ArrayList<String> accountTypes;
    public ArrayList<String> accountTags;
    public String reportName;
    public String periodName;
    public String reportType;

    public TransactionFilter() {
        transactionNames = new ArrayList<>();
        fromTransactionDate = 0;
        toTransactionDate = 0;
        categories = new ArrayList<>();
        accountNames = new ArrayList<>();
        toAccountNames = new ArrayList<>();
        fromAmount = 0;
        toAmount = 0;
        transactionTypes = new ArrayList<>();
        accountTypes = new ArrayList<>();
        searchText = "";
        reportName = "";
        periodName = "";
        accountTags = new ArrayList<>();
        reportType = "";
    }

    protected TransactionFilter(Parcel in) {
        transactionNames = in.createStringArrayList();
        fromTransactionDate = in.readInt();
        toTransactionDate = in.readInt();
        categories = in.createStringArrayList();
        accountNames = in.createStringArrayList();
        toAccountNames = in.createStringArrayList();
        fromAmount = in.readDouble();
        toAmount = in.readDouble();
        transactionTypes = in.createStringArrayList();
        searchText = in.readString();
        accountTypes = in.createStringArrayList();
        reportName = in.readString();
        periodName = in.readString();
        accountTags = in.createStringArrayList();
        reportType = in.readString();
    }

    @NonNull
    @Override
    public String toString() {
        return "TransactionFilterParameters{" +
                "transactionName='" + transactionNames + '\'' +
                ", fromTransactionDate=" + fromTransactionDate +
                ", toTransactionDate=" + toTransactionDate +
                ", category=" + categories +
                ", accountName=" + accountNames +
                ", toAccountName=" + toAccountNames +
                ", fromAmount=" + fromAmount +
                ", toAmount=" + toAmount +
                ", transactionType=" + transactionTypes +
                ", searchText='" + searchText + '\'' +
                ", accountType=" + accountTypes +
                ", reportName='" + reportName + '\'' +
                ", periodName='" + periodName + '\'' +
                ", accountTags=" + accountTags +
                ", reportType='" + reportType + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(transactionNames);
        dest.writeInt(fromTransactionDate);
        dest.writeInt(toTransactionDate);
        dest.writeStringList(categories);
        dest.writeStringList(accountNames);
        dest.writeStringList(toAccountNames);
        dest.writeDouble(fromAmount);
        dest.writeDouble(toAmount);
        dest.writeStringList(transactionTypes);
        dest.writeString(searchText);
        dest.writeStringList(accountTypes);
        dest.writeString(reportName);
        dest.writeString(periodName);
        dest.writeStringList(accountTags);
        dest.writeString(reportType);
    }

    public boolean isEmpty() {
        if (transactionNames != null && !transactionNames.isEmpty()) return false;
        if (fromTransactionDate != 0) return false;
        if (toTransactionDate != 0) return false;
        if (categories != null && !categories.isEmpty()) return false;
        if (accountNames != null && !accountNames.isEmpty()) return false;
        if (toAccountNames != null && !toAccountNames.isEmpty()) return false;
        if (fromAmount != 0) return false;
        if (toAmount != 0) return false;
        if (transactionTypes != null && !transactionTypes.isEmpty()) return false;
        if (searchText != null && !searchText.isBlank()) return false;
        if (accountTypes != null && !accountTypes.isEmpty()) return false;
        if (reportName != null && !reportName.isBlank()) return false;
        if (periodName != null && !periodName.isBlank()) return false;
        if (accountTags != null && !accountTags.isEmpty()) return false;
        if (reportType != null && !reportType.isBlank()) return false;
        return true;
    }

    public void clear() {
        transactionNames.clear();
        fromTransactionDate = 0;
        toTransactionDate = 0;
        categories.clear();
        accountNames.clear();
        toAccountNames.clear();
        fromAmount = 0;
        toAmount = 0;
        transactionTypes.clear();
        accountTypes.clear();
        searchText = "";
        reportName = "";
        periodName = "";
        accountTags.clear();
        reportType = "";
    }

    public LocalDate fromTransactionDateToLocalDate() {
        LocalDate localDate;
        try {
            localDate = LocalDate.parse(String.valueOf(fromTransactionDate), DateTimeFormatter.ofPattern("yyyyMMdd"));
        } catch (Exception e) {
            localDate = LocalDate.now();
        }
        return localDate;
    }

    public LocalDate toTransactionDateToLocalDate() {
        LocalDate localDate;
        try {
            localDate = LocalDate.parse(String.valueOf(toTransactionDate), DateTimeFormatter.ofPattern("yyyyMMdd"));
        } catch (Exception e) {
            localDate = LocalDate.now();
        }
        return localDate;
    }

    public void setFromTransactionDate(LocalDate fromTransactionDate) {
        this.fromTransactionDate = Integer.parseInt(fromTransactionDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
    }

    public void setToTransactionDate(LocalDate toTransactionDate) {
        this.toTransactionDate = Integer.parseInt(toTransactionDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
    }

    public void addAccountNames(List<String> taggedAccounts) {
        if (accountNames == null) accountNames = new ArrayList<>();
        HashMap<String, String> deDupedAccounts = new HashMap<>();
        for (String accountName : taggedAccounts) {
            deDupedAccounts.put(accountName, accountName);
        }
        for (String accountName : accountNames) {
            deDupedAccounts.put(accountName, accountName);
        }
        accountNames.clear();
        accountNames.addAll(deDupedAccounts.keySet());
    }

    @Override
    public TransactionFilter clone() {
        try {
            TransactionFilter clone = (TransactionFilter) super.clone();
            clone.transactionNames = new ArrayList<>(this.transactionNames);
            clone.fromTransactionDate = this.fromTransactionDate;
            clone.toTransactionDate = this.toTransactionDate;
            clone.categories = new ArrayList<>(this.categories);
            clone.accountNames = new ArrayList<>(this.accountNames);
            clone.toAccountNames = new ArrayList<>(this.toAccountNames);
            clone.fromAmount = this.fromAmount;
            clone.toAmount = this.toAmount;
            clone.transactionTypes = new ArrayList<>(this.transactionTypes);
            clone.accountTypes = new ArrayList<>(this.accountTypes);
            clone.searchText = this.searchText;
            clone.reportName = this.reportName;
            clone.periodName = this.reportName;
            clone.accountTags = new ArrayList<>(this.accountTags);
            clone.reportType = this.reportType;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
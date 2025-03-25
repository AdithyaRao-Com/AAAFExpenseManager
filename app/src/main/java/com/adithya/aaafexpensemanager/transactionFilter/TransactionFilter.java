package com.adithya.aaafexpensemanager.transactionFilter;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class TransactionFilter implements Parcelable {
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
    }

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
        searchText=in.readString();
        accountTypes = in.createStringArrayList();
    }

    public boolean isEmpty() {
        if(transactionNames!=null && !transactionNames.isEmpty()) return false;
        if(fromTransactionDate!=0) return false;
        if(toTransactionDate!=0) return false;
        if(categories!=null && !categories.isEmpty()) return false;
        if(accountNames!=null && !accountNames.isEmpty()) return false;
        if(toAccountNames!=null && !toAccountNames.isEmpty()) return false;
        if(fromAmount!=0) return false;
        if(toAmount!=0) return false;
        if(transactionTypes!=null && !transactionTypes.isEmpty()) return false;
        if(searchText!=null && !searchText.isBlank()) return false;
        if(accountTypes!=null && !accountTypes.isEmpty()) return false;
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
    }

    public LocalDate fromTransactionDateToLocalDate(){
        LocalDate localDate;
        try {
            localDate = LocalDate.parse(String.valueOf(fromTransactionDate), DateTimeFormatter.ofPattern("yyyyMMdd"));
        }
        catch (Exception e){
            localDate = LocalDate.now();
        }
        return localDate;
    }
    public LocalDate toTransactionDateToLocalDate(){
        LocalDate localDate;
        try {
            localDate = LocalDate.parse(String.valueOf(toTransactionDate), DateTimeFormatter.ofPattern("yyyyMMdd"));
        }
        catch (Exception e){
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
}
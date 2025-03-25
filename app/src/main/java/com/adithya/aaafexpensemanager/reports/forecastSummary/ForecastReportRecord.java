package com.adithya.aaafexpensemanager.reports.forecastSummary;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.adithya.aaafexpensemanager.util.CurrencyFormatter;

import java.time.LocalDate;

public class ForecastReportRecord implements Parcelable {
    public LocalDate date;
    public double amount;
    public String currency;
    public ForecastReportRecord(LocalDate date, double amount,String currency) {
        this.date = date;
        this.amount = amount;
        this.currency = currency;
    }
    public String getDateText(){
        return date.toString();
    }
    public String getAmountText(){
        return CurrencyFormatter.formatIndianStyle(amount,currency);
    }
    protected ForecastReportRecord(Parcel in) {
        amount = in.readDouble();
        date = in.readSerializable(ForecastReportRecord.class.getClassLoader(),LocalDate.class);
        currency = in.readString();
    }

    public static final Creator<ForecastReportRecord> CREATOR = new Creator<>() {
        @Override
        public ForecastReportRecord createFromParcel(Parcel in) {
            return new ForecastReportRecord(in);
        }
        @Override
        public ForecastReportRecord[] newArray(int size) {
            return new ForecastReportRecord[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeSerializable(this.date);
        dest.writeDouble(this.amount);
        dest.writeString(this.currency);
    }
}

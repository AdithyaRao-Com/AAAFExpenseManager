package com.adithya.aaafexpensemanager.reports.forecastSummary;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.adithya.aaafexpensemanager.util.CurrencyFormatter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ForecastReportRecord implements Parcelable {
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
    public LocalDate transactionDate;
    public double amount;
    public String currency;

    public ForecastReportRecord(LocalDate transactionDate, double amount, String currency) {
        this.transactionDate = transactionDate;
        this.amount = amount;
        this.currency = currency;
    }

    protected ForecastReportRecord(Parcel in) {
        amount = in.readDouble();
        transactionDate = in.readSerializable(ForecastReportRecord.class.getClassLoader(), LocalDate.class);
        currency = in.readString();
    }

    public String getDateText() {
        return transactionDate.toString();
    }

    public String getDateText_DD_MMM_YYYY() {
        return transactionDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }

    public String getAmountText() {
        return CurrencyFormatter.formatIndianStyle(amount, currency);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeSerializable(this.transactionDate);
        dest.writeDouble(this.amount);
        dest.writeString(this.currency);
    }
}

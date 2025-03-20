package com.adithya.aaafexpensemanager.settings.currency;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Currency implements Parcelable {
    public String currencyName;
    public boolean isPrimary;
    public double conversionFactor;
    public String primaryCurrencyName;
    public Currency(String currencyName, boolean isPrimary, double conversionFactor,String primaryCurrencyName) {
        this.currencyName = currencyName;
        this.isPrimary = isPrimary;
        this.conversionFactor = conversionFactor;
        this.primaryCurrencyName = primaryCurrencyName;
    }
    public Currency(String currencyName, double conversionFactor) {
        this.currencyName = currencyName;
        this.isPrimary = false;
        this.conversionFactor = conversionFactor;
    }

    protected Currency(Parcel in) {
        currencyName = in.readString();
        isPrimary = in.readByte() != 0;
        conversionFactor = in.readDouble();
        primaryCurrencyName = in.readString();
    }

    public static final Creator<Currency> CREATOR = new Creator<>() {
        @Override
        public Currency createFromParcel(Parcel in) {
            return new Currency(in);
        }

        @Override
        public Currency[] newArray(int size) {
            return new Currency[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(currencyName);
        dest.writeByte((byte) (isPrimary ? 1 : 0));
        dest.writeDouble(conversionFactor);
        dest.writeString(primaryCurrencyName);
    }
}

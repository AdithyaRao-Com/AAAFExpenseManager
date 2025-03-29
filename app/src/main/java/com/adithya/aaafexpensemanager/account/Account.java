package com.adithya.aaafexpensemanager.account;

import android.os.Parcel;
import android.os.Parcelable;

import com.adithya.aaafexpensemanager.util.CurrencyFormatter;

import java.util.Objects;

/**
 * @noinspection unused
 */
public class Account implements Parcelable {
    public static final Parcelable.Creator<Account> CREATOR = new Parcelable.Creator<>() {
        @Override
        public Account createFromParcel(Parcel in) {
            return new Account(in);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };
    public String accountName;
    public String accountType;
    public double accountBalance;
    public String accountTags;
    public int displayOrder;
    public String currencyCode;
    public boolean closeAccountInd;
    public boolean doNotShowInDropdownInd;

    public Account(String accountName, String accountType, double accountBalance,
                   String accountTags, int displayOrder, String currencyCode, boolean closeAccountInd,
                   boolean doNotShowInDropdownInd) {
        this.accountName = accountName;
        this.accountType = accountType;
        this.accountBalance = accountBalance;
        this.accountTags = accountTags;
        this.displayOrder = displayOrder;
        this.currencyCode = currencyCode;
        this.closeAccountInd = closeAccountInd;
        this.doNotShowInDropdownInd = doNotShowInDropdownInd;
    }

    protected Account(Parcel in) {
        accountName = in.readString();
        accountType = in.readString();
        accountBalance = in.readDouble();
        accountTags = in.readString();
        displayOrder = in.readInt();
        currencyCode = in.readString();
        closeAccountInd = in.readByte() != 0;
        doNotShowInDropdownInd = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(accountName);
        dest.writeString(accountType);
        dest.writeDouble(accountBalance);
        dest.writeString(accountTags);
        dest.writeInt(displayOrder);
        dest.writeString(currencyCode);
        dest.writeByte((byte) (closeAccountInd ? 1 : 0));
        dest.writeByte((byte) (doNotShowInDropdownInd ? 1 : 0));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(accountName, account.accountName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(accountName);
    }

    public String accountBalanceToIndianFormat() {
        return CurrencyFormatter.formatIndianStyle(accountBalance, currencyCode);
    }

    public String accountBalanceToStandardFormat() {
        return CurrencyFormatter.formatStandardStyle(accountBalance, currencyCode);
    }
}
package com.adithya.aaafexpensemanager.settings.accounttype;

import android.os.Parcel;
import android.os.Parcelable;

public class AccountType implements Parcelable {
    public String accountType;
    public int accountTypeDisplayOrder;
    public AccountType(String accountType, int accountTypeDisplayOrder) {
        this.accountType = accountType;
        this.accountTypeDisplayOrder = accountTypeDisplayOrder;
    }
    protected AccountType(Parcel in){
        accountType = in.readString();
        accountTypeDisplayOrder = in.readInt();
    }
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(accountType);
        dest.writeInt(accountTypeDisplayOrder);
    }
    public static final Parcelable.Creator<AccountType> CREATOR = new Parcelable.Creator<>() {
        @Override
        public AccountType createFromParcel(Parcel in) {
            return new AccountType(in);
        }

        @Override
        public AccountType[] newArray(int size) {
            return new AccountType[size];
        }
    };

}

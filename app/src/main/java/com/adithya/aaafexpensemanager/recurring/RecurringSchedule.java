package com.adithya.aaafexpensemanager.recurring;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adithya.aaafexpensemanager.transaction.Transaction;
import com.adithya.aaafexpensemanager.util.AppConstants;
import com.adithya.aaafexpensemanager.util.CurrencyFormatter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.UUID;

/** @noinspection CallToPrintStackTrace, unused */
public class RecurringSchedule implements Parcelable {
    public UUID recurringScheduleUUID;
    public String transactionName;
    public String recurringScheduleName;
    public int repeatIntervalDays;
    public int recurringStartDate;
    public int recurringEndDate;
    public String category;
    public String notes;
    public String transactionType;
    public double amount;
    public String accountName;
    public String toAccountName;
    public long createDateTime;
    public long lastUpdateDateTime;
    public String transferInd;
    public int nextRecurringDate;
    public String currencyCode;
    public double conversionFactor;
    public String primaryCurrencyCode;

    public RecurringSchedule(UUID recurringScheduleUUID, String transactionName,
                             String recurringScheduleName, int repeatIntervalDays,
                             int recurringStartDate, int recurringEndDate, String notes,
                             String transactionType, String category, double amount,
                             String accountName, String toAccountName, long createDateTime,
                             long lastUpdateDateTime, String transferInd, int nextRecurringDate,
                             String currencyCode, double conversionFactor, String primaryCurrencyCode) {
        this.category = category;
        this.recurringScheduleUUID = recurringScheduleUUID;
        this.transactionName = transactionName;
        this.recurringScheduleName = recurringScheduleName;
        this.repeatIntervalDays = repeatIntervalDays;
        this.recurringStartDate = recurringStartDate;
        this.recurringEndDate = recurringEndDate;
        this.notes = notes;
        this.transactionType = transactionType;
        this.amount = amount;
        this.accountName = accountName;
        this.toAccountName = toAccountName;
        this.createDateTime = createDateTime;
        this.lastUpdateDateTime = lastUpdateDateTime;
        this.transferInd = transferInd;
        try{
            LocalDate.parse(String.valueOf(nextRecurringDate), DateTimeFormatter.ofPattern("yyyyMMdd"));
            this.nextRecurringDate = nextRecurringDate;
        }
        catch (Exception e){
            this.nextRecurringDate = Integer.parseInt(AppConstants.TRANSACTION_DATE_DUMMY.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        }
        this.currencyCode = currencyCode;
        this.conversionFactor = conversionFactor;
        this.primaryCurrencyCode = primaryCurrencyCode;
    }

    public RecurringSchedule(String transactionName, String recurringScheduleName,
                             int repeatIntervalDays, LocalDate recurringStartDate,
                             LocalDate recurringEndDate, String category, String notes,
                             String transactionType, double amount, String accountName,
                             String toAccountName, String transferInd) {
        this.recurringScheduleUUID = UUID.randomUUID();
        this.transactionName = transactionName;
        this.recurringScheduleName = recurringScheduleName;
        this.repeatIntervalDays = repeatIntervalDays;
        this.recurringStartDate = Integer.parseInt(recurringStartDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        this.recurringEndDate = Integer.parseInt(recurringEndDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        this.category = category;
        this.notes = notes;
        this.transactionType = transactionType;
        this.amount = amount;
        this.accountName = accountName;
        this.toAccountName = toAccountName;
        this.createDateTime = System.currentTimeMillis();
        this.lastUpdateDateTime = this.createDateTime;
        this.transferInd = transferInd;
    }
    public RecurringSchedule(String transactionName, String recurringScheduleName,
                             int repeatIntervalDays, int recurringStartDate, int recurringEndDate,
                             String category, String notes, String transactionType, double amount,
                             String accountName, String toAccountName, String transferInd) {
        this.recurringScheduleUUID = UUID.randomUUID();
        this.transactionName = transactionName;
        this.recurringScheduleName = recurringScheduleName;
        this.repeatIntervalDays = repeatIntervalDays;
        this.recurringStartDate = recurringStartDate;
        this.recurringEndDate = recurringEndDate;
        this.category = category;
        this.notes = notes;
        this.transactionType = transactionType;
        this.amount = amount;
        this.accountName = accountName;
        this.toAccountName = toAccountName;
        this.createDateTime = System.currentTimeMillis();
        this.lastUpdateDateTime = this.createDateTime;
        this.transferInd = transferInd;
    }
    public RecurringSchedule(Parcel in) {
        String recurringScheduleUUIDString = in.readString();
        if (recurringScheduleUUIDString != null) {
            recurringScheduleUUID = UUID.fromString(recurringScheduleUUIDString);
        }
        transactionName = in.readString();
        recurringScheduleName = in.readString();
        repeatIntervalDays = in.readInt();
        recurringStartDate = in.readInt();
        recurringEndDate = in.readInt();
        category = in.readString();
        notes = in.readString();
        transactionType = in.readString();
        amount = in.readDouble();
        accountName = in.readString();
        toAccountName = in.readString();
        createDateTime = in.readInt();
        lastUpdateDateTime = in.readInt();
        transferInd = in.readString();
        nextRecurringDate = in.readInt();
        currencyCode = in.readString();
        conversionFactor = in.readDouble();
        primaryCurrencyCode = in.readString();
    }

    public RecurringSchedule() {
        this.recurringScheduleUUID = UUID.randomUUID();
        this.transactionName = "";
        this.recurringScheduleName = "";
        this.repeatIntervalDays = 0;
        this.recurringStartDate = Integer.parseInt(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        this.recurringEndDate = Integer.parseInt(LocalDate.now().plusYears(AppConstants.DEFAULT_RECURRING_END_INTERVAL).format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        this.category = "";
        this.notes = "";
        this.transactionType = "Expense";
        this.amount = 0.0;
        this.accountName = "";
        this.toAccountName = "";
        this.createDateTime = System.currentTimeMillis();
        this.lastUpdateDateTime = this.createDateTime;
        this.transferInd = "Expense";
        this.nextRecurringDate = Integer.parseInt(AppConstants.TRANSACTION_DATE_DUMMY.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        this.currencyCode = "INR";
        this.conversionFactor = 1.0;
        this.primaryCurrencyCode = "INR";
    }

    public RecurringSchedule(Transaction transaction,
                             String recurringScheduleName,
                             int repeatIntervalDays,
                             LocalDate recurringStartDate,
                             LocalDate recurringEndDate) {
        this.recurringScheduleUUID = UUID.randomUUID();
        this.transactionName = transaction.transactionName;
        this.recurringScheduleName = recurringScheduleName;
        this.repeatIntervalDays = repeatIntervalDays;
        this.recurringStartDate = Integer.parseInt(recurringStartDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        this.recurringEndDate = Integer.parseInt(recurringEndDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        this.category = transaction.category;
        this.notes = transaction.notes;
        this.transactionType = transaction.transactionType;
        this.amount = transaction.amount;
        this.accountName = transaction.accountName;
        this.toAccountName = transaction.toAccountName;
        this.createDateTime = System.currentTimeMillis();
        this.lastUpdateDateTime = this.createDateTime;
        this.transferInd = transaction.transferInd;
        this.currencyCode = transaction.currencyCode;
        this.conversionFactor = transaction.conversionFactor;
        this.primaryCurrencyCode = transaction.primaryCurrencyCode;
    }

    public RecurringSchedule(Transaction tran) {
        this(tran,
                AppConstants.RECURRING_SCHEDULES.get(0),
                0,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusYears(AppConstants.DEFAULT_RECURRING_END_INTERVAL));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(recurringScheduleUUID != null ? recurringScheduleUUID.toString() : null);
        dest.writeString(transactionName);
        dest.writeString(recurringScheduleName);
        dest.writeInt(repeatIntervalDays);
        dest.writeInt(recurringStartDate);
        dest.writeInt(recurringEndDate);
        dest.writeString(category);
        dest.writeString(notes);
        dest.writeString(transactionType);
        dest.writeDouble(amount);
        dest.writeString(accountName);
        dest.writeString(toAccountName);
        dest.writeLong(createDateTime);
        dest.writeLong(lastUpdateDateTime);
        dest.writeString(transferInd);
        dest.writeInt(nextRecurringDate);
        dest.writeString(currencyCode);
        dest.writeDouble(conversionFactor);
        dest.writeString(primaryCurrencyCode);
    }
    public static final Parcelable.Creator<RecurringSchedule> CREATOR = new Parcelable.Creator<>() {
        @Override
        public RecurringSchedule createFromParcel(Parcel in) {
            return new RecurringSchedule(in);
        }

        @Override
        public RecurringSchedule[] newArray(int size) {
            return new RecurringSchedule[size];
        }
    };
    public LocalDate getRecurringStartDateLocalDate() {
        return getLocalDateFromYYYYMMDDInt(recurringStartDate);
    }
    public LocalDate getRecurringEndDateLocalDate() {
        return getLocalDateFromYYYYMMDDInt(recurringEndDate);
    }

    public LocalDate getNextRecurringDateLocalDate() {
        return getLocalDateFromYYYYMMDDInt(nextRecurringDate);
    }
    /** @noinspection unused*/
    public int getRecurringDateStringToInt(String dateStringInYYYY_MM_DD){
        try {
            LocalDate date = LocalDate.parse(dateStringInYYYY_MM_DD, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return Integer.parseInt(date.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Nullable
    public static LocalDate getLocalDateFromYYYYMMDDInt(int inputDate) {
        try {
            return LocalDate.parse(String.valueOf(inputDate), DateTimeFormatter.ofPattern("yyyyMMdd"));
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getRecurringStartDateString() {
        try {
            return getRecurringStartDateLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException e) {
            return "";
        }
    }
    public String getRecurringEndDateString() {
        try {
            return getRecurringEndDateLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException e) {
            return "";
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "RecurringSchedule{" +
                "recurringScheduleUUID=" + recurringScheduleUUID +
                ", transactionName='" + transactionName + '\'' +
                ", recurringSchedule='" + recurringScheduleName + '\'' +
                ", repeatIntervalDays=" + repeatIntervalDays +
                ", recurringStartDate=" + recurringStartDate +
                ", recurringEndDate=" + recurringEndDate +
                ", category='" + category + '\'' +
                ", notes='" + notes + '\'' +
                ", transactionType='" + transactionType + '\'' +
                ", amount=" + amount +
                ", accountName='" + accountName + '\'' +
                ", toAccountName='" + toAccountName + '\'' +
                ", createDateTime=" + createDateTime +
                ", lastUpdateDateTime=" + lastUpdateDateTime +
                ", transferInd='" + transferInd + '\'' +
                ", nextRecurringDate=" + nextRecurringDate +
                ", currencyCode='" + currencyCode + '\'' +
                ", conversionFactor=" + conversionFactor +
                ", primaryCurrencyCode='" + primaryCurrencyCode + '\'' +
                '}';
    }
    public String amountToIndianFormat() {
        return CurrencyFormatter.formatIndianStyle(this.amount,"INR");
    }
    public String amountToStandardFormat(){
        return CurrencyFormatter.formatStandardStyle(this.amount,"INR");
    }
}

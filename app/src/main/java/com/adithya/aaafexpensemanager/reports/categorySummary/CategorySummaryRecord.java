package com.adithya.aaafexpensemanager.reports.categorySummary;

import androidx.annotation.NonNull;

import com.adithya.aaafexpensemanager.reusableComponents.lookupEditText.LookupEditText;

import java.time.LocalDate;
import java.util.Objects;

/** @noinspection unused*/
public class CategorySummaryRecord {
    public String category;
    public double amount;
    public double pct;
    public LocalDate startDate;
    public LocalDate endDate;
    public double totalAmount;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CategorySummaryRecord that = (CategorySummaryRecord) o;
        return Objects.equals(category, that.category) && Objects.equals(startDate, that.startDate) && Objects.equals(endDate, that.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(category, startDate, endDate);
    }
    public CategorySummaryRecord(String category, double amount, LocalDate startDate, LocalDate endDate, double pct, double totalAmount) {
        this.category = category;
        this.amount = amount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.pct = (double) Math.round(pct * 100.0)/100.0;
        this.totalAmount = (double) Math.round(totalAmount * 100.0)/100.0;
    }

    public CategorySummaryRecord(String category, LocalDate startDate, LocalDate endDate) {
        this.category = category;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static LocalDate truncateToMonth(LocalDate date){
        return LocalDate.of(date.getYear(), date.getMonth(), 1);
    }
    public static LocalDate truncateToMonthEnd(LocalDate date){
        return truncateToMonth(date).plusMonths(1).minusDays(1);
    }
    public static LocalDate truncateToWeek(LocalDate date){
        int dayOfWeek = date.getDayOfWeek().getValue();
        if(dayOfWeek==7){
            dayOfWeek=0;
        }
        return date.minusDays(dayOfWeek);
    }
    public static LocalDate truncateToWeekEnd(LocalDate date){
        return truncateToWeek(date).plusDays(6);
    }
    public static LocalDate truncateToYear(LocalDate date){
        return LocalDate.of(date.getYear(), 1, 1);
    }
    public static LocalDate truncateToYearEnd(LocalDate date){
        return truncateToYear(date).plusYears(1).minusDays(1);
    }
    public enum TimePeriod implements LookupEditText.LookupEditTextItem{
        WEEKLY(){
            @Override
            public String toEditTextLookupString() {
                return toString();
            }
            @Override
            @NonNull
            public String toString() {
                return "Weekly";
            }
            @NonNull
            @Override
            public LocalDate truncateToStart(LocalDate localDate) {
                return CategorySummaryRecord.truncateToWeek(localDate);
            }
            @NonNull
            @Override
            public LocalDate truncateToEnd(LocalDate localDate) {
                return CategorySummaryRecord.truncateToWeekEnd(localDate);
            }
            @NonNull
            @Override
            public LocalDate truncateToStart() {
                return truncateToStart(LocalDate.now());
            }

            @NonNull
            @Override
            public LocalDate truncateToEnd() {
                return truncateToEnd(LocalDate.now());
            }
        },
        MONTHLY(){
            @Override
            public String toEditTextLookupString() {
                return toString();
            }
            @Override
            @NonNull
            public String toString() {
                return "Monthly";
            }
            @NonNull
            @Override
            public LocalDate truncateToStart(LocalDate localDate) {
                return CategorySummaryRecord.truncateToMonth(localDate);
            }
            @NonNull
            @Override
            public LocalDate truncateToEnd(LocalDate localDate) {
                return CategorySummaryRecord.truncateToMonthEnd(localDate);
            }
            @NonNull
            @Override
            public LocalDate truncateToStart() {
                return truncateToStart(LocalDate.now());
            }

            @NonNull
            @Override
            public LocalDate truncateToEnd() {
                return truncateToEnd(LocalDate.now());
            }
        },
        YEARLY(){
            @Override
            public String toEditTextLookupString() {
                return toString();
            }
            @Override
            @NonNull
            public String toString() {
                return "Yearly";
            }
            @NonNull
            @Override
            public LocalDate truncateToStart(LocalDate localDate) {
                return truncateToYear(localDate);
            }

            @NonNull
            @Override
            public LocalDate truncateToEnd(LocalDate localDate) {
                return truncateToYearEnd(localDate);
            }
            @NonNull
            @Override
            public LocalDate truncateToStart() {
                return truncateToStart(LocalDate.now());
            }

            @NonNull
            @Override
            public LocalDate truncateToEnd() {
                return truncateToEnd(LocalDate.now());
            }
        };
        @NonNull
        public abstract String toString();
        @NonNull
        public abstract LocalDate truncateToStart(LocalDate localDate);
        @NonNull
        public abstract LocalDate truncateToEnd(LocalDate localDate);
        @NonNull
        public abstract LocalDate truncateToStart();
        @NonNull
        public abstract LocalDate truncateToEnd();
    }
}

package com.adithya.aaafexpensemanager.reports.forecastSummary;

import androidx.annotation.NonNull;

import com.adithya.aaafexpensemanager.reusableComponents.lookupEditText.LookupEditText;

import java.time.LocalDate;

public class ForecastConstants {
    public enum ForecastTimePeriod implements LookupEditText.LookupEditTextItem {
        THIS_MONTH(){
            @Override
            public String toEditTextLookupString() {
                return toString();
            }

            @NonNull
            @Override
            public LocalDate getEndDate(LocalDate localDate) {
                return LocalDate.now().withDayOfMonth(1).plusMonths(1).minusDays(1);
            }

            @NonNull
            @Override
            public String toString() {
                return "This Month";
            }
        },
        NEXT_3_MONTHS(){
            @Override
            public String toEditTextLookupString() {
                return toString();
            }
            @NonNull
            @Override
            public LocalDate getEndDate(LocalDate localDate) {
                return LocalDate.now().withDayOfMonth(1).plusMonths(3).minusDays(1);
            }

            @NonNull
            @Override
            public String toString() {
                return "Next 3 Months";
            }
        },
        NEXT_6_MONTHS(){
            @Override
            public String toEditTextLookupString() {
                return toString();
            }
            @NonNull
            @Override
            public LocalDate getEndDate(LocalDate localDate) {
                return LocalDate.now().withDayOfMonth(1).plusMonths(6).minusDays(1);
            }

            @NonNull
            @Override
            public String toString() {
                return "Next 6 Months";
            }
        },
        NEXT_12_MONTHS() {
            @Override
            public String toEditTextLookupString() {
                return toString();
            }
            @NonNull
            @Override
            public LocalDate getEndDate(LocalDate localDate) {
                return LocalDate.now().withDayOfMonth(1).plusMonths(12).minusDays(1);
            }

            @NonNull
            @Override
            public String toString() {
                return "Next 12 Months";
            }
        },
        CURRENT_YEAR(){
            @Override
            public String toEditTextLookupString() {
                return toString();
            }
            @NonNull
            @Override
            public String toString() {
                return "Current Year";
            }
            @NonNull
            @Override
            public LocalDate getEndDate(LocalDate localDate) {
                return LocalDate.now().withDayOfYear(1).plusYears(1).minusDays(1);
            }
        },
        NEXT_3_YEARS(){
            @Override
            public String toEditTextLookupString() {
                return toString();
            }
            @NonNull
            @Override
            public LocalDate getEndDate(LocalDate localDate) {
                return LocalDate.now().withDayOfYear(1).plusYears(3).minusDays(1);
            }

            @NonNull
            @Override
            public String toString() {
                return "Next 3 Years";
            }
        };
        @NonNull
        public abstract LocalDate getEndDate(LocalDate localDate);
    }
}

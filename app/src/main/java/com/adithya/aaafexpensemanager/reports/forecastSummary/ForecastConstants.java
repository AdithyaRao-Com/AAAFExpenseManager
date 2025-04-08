package com.adithya.aaafexpensemanager.reports.forecastSummary;

import androidx.annotation.NonNull;

import com.adithya.aaafexpensemanager.reusableComponents.lookupEditText.LookupEditText;

import java.time.LocalDate;

public class ForecastConstants {
    public enum ForecastTimePeriod implements LookupEditText.LookupEditTextItem {
        THIS_MONTH() {
            @Override
            public String toEditTextLookupString() {
                return toString();
            }

            @NonNull
            @Override
            public LocalDate getEndDate() {
                return LocalDate.now().withDayOfMonth(1).plusMonths(1).minusDays(1);
            }

            @NonNull
            @Override
            public LocalDate getStartDate() {
                return LocalDate.now().withDayOfMonth(1);
            }

            @NonNull
            @Override
            public String toString() {
                return "This Month";
            }
        },
        NEXT_3_MONTHS() {
            @Override
            public String toEditTextLookupString() {
                return toString();
            }

            @NonNull
            @Override
            public LocalDate getEndDate() {
                return LocalDate.now().withDayOfMonth(1).plusMonths(3).minusDays(1);
            }

            @NonNull
            @Override
            public LocalDate getStartDate() {
                return LocalDate.now();
            }

            @NonNull
            @Override
            public String toString() {
                return "Next 3 Months";
            }
        },
        NEXT_6_MONTHS() {
            @Override
            public String toEditTextLookupString() {
                return toString();
            }

            @NonNull
            @Override
            public LocalDate getEndDate() {
                return LocalDate.now().withDayOfMonth(1).plusMonths(6).minusDays(1);
            }

            @NonNull
            @Override
            public LocalDate getStartDate() {
                return LocalDate.now();
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
            public LocalDate getEndDate() {
                return LocalDate.now().withDayOfMonth(1).plusMonths(12).minusDays(1);
            }

            @NonNull
            @Override
            public LocalDate getStartDate() {
                return LocalDate.now();
            }

            @NonNull
            @Override
            public String toString() {
                return "Next 12 Months";
            }
        },
        CURRENT_YEAR() {
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
            public LocalDate getEndDate() {
                return LocalDate.now().withDayOfYear(1).plusYears(1).minusDays(1);
            }

            @NonNull
            @Override
            public LocalDate getStartDate() {
                return LocalDate.now().withDayOfYear(1);
            }
        },
        NEXT_3_YEARS() {
            @Override
            public String toEditTextLookupString() {
                return toString();
            }

            @NonNull
            @Override
            public LocalDate getEndDate() {
                return LocalDate.now().withDayOfYear(1).plusYears(3).minusDays(1);
            }

            @NonNull
            @Override
            public LocalDate getStartDate() {
                return LocalDate.now();
            }

            @NonNull
            @Override
            public String toString() {
                return "Next 3 Years";
            }
        },
        LAST_MONTH() {
            @Override
            public String toEditTextLookupString() {
                return toString();
            }
            @NonNull
            @Override
            public LocalDate getEndDate() {
                return LocalDate.now();
            }
            @NonNull
            @Override
            public LocalDate getStartDate() {
                return LocalDate.now().withDayOfMonth(1).minusMonths(1);
            }
            @NonNull
            @Override
            public String toString() {
                return "Last Month";
            }
        },
        LAST_3_MONTHS() {
            @Override
            public String toEditTextLookupString() {
                return toString();
            }
            @NonNull
            @Override
            public LocalDate getEndDate() {
                return LocalDate.now();
            }
            @NonNull
            @Override
            public LocalDate getStartDate() {
                return LocalDate.now().withDayOfMonth(1).minusMonths(3);
            }
            @NonNull
            @Override
            public String toString() {
                return "Last 3 Months";
            }
        },
        LAST_6_MONTHS() {
            @Override
            public String toEditTextLookupString() {
                return toString();
            }
            @NonNull
            @Override
            public LocalDate getEndDate() {
                return LocalDate.now();
            }
            @NonNull
            @Override
            public LocalDate getStartDate() {
                return LocalDate.now().withDayOfMonth(1).minusMonths(6);
            }
            @NonNull
            @Override
            public String toString() {
                return "Last 6 Months";
            }
        },
        LAST_12_MONTHS() {
            @Override
            public String toEditTextLookupString() {
                return toString();
            }
            @NonNull
            @Override
            public LocalDate getEndDate() {
                return LocalDate.now();
            }
            @NonNull
            @Override
            public LocalDate getStartDate() {
                return LocalDate.now().withDayOfMonth(1).minusMonths(12);
            }
            @NonNull
            @Override
            public String toString() {
                return "Last 12 Months";
            }
        },
        LAST_YEAR() {
            @Override
            public String toEditTextLookupString() {
                return toString();
            }
            @NonNull
            @Override
            public LocalDate getEndDate() {
                return LocalDate.now().withDayOfYear(1).minusDays(1);
            }
            @NonNull
            @Override
            public LocalDate getStartDate() {
                return LocalDate.now().withDayOfYear(1).minusYears(1);
            }
            @NonNull
            @Override
            public String toString() {
                return "Last Year";
            }
        },
        CUSTOM(){
            @Override
            public String toEditTextLookupString() {
                return toString();
            }
            @NonNull
            @Override
            public LocalDate getEndDate() {
                return LocalDate.now();
            }
            @NonNull
            @Override
            public LocalDate getStartDate() {
                return LocalDate.now();
            }
            @NonNull
            @Override
            public String toString() {
                return "Custom";
            }
        };

        @NonNull
        public abstract LocalDate getEndDate();

        @NonNull
        public abstract LocalDate getStartDate();
    }
}

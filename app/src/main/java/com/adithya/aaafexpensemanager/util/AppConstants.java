package com.adithya.aaafexpensemanager.util;

import java.time.LocalDate;
import java.util.List;

public class AppConstants {
    public static final int BATCH_SIZE = 1000;
    public  static final String RECURRING_SCHEDULES_DAILY = "Daily";
    public  static final String RECURRING_SCHEDULES_WEEKLY = "Weekly";
    public  static final String RECURRING_SCHEDULES_MONTHLY = "Monthly";
    public  static final String RECURRING_SCHEDULES_QUARTERLY = "Quarterly";
    public  static final String RECURRING_SCHEDULES_YEARLY = "Yearly";
    public  static final String RECURRING_SCHEDULES_CUSTOM= "Custom";
    public static final List<String> RECURRING_SCHEDULES =
            List.of(RECURRING_SCHEDULES_DAILY,
                    RECURRING_SCHEDULES_WEEKLY,
                    RECURRING_SCHEDULES_MONTHLY,
                    RECURRING_SCHEDULES_QUARTERLY,
                    RECURRING_SCHEDULES_YEARLY,
                    RECURRING_SCHEDULES_CUSTOM);
    public static final long DEFAULT_RECURRING_END_INTERVAL = 10L;
    public static final boolean IS_DEV_MODE = true;
    public static final int DATABASE_VERSION = 29;
    public static final LocalDate TRANSACTION_DATE_DUMMY = LocalDate.of(2000, 1,1);
    public static final int COPY_LIMIT = 10;
    public static final String DATABASE_NAME = "AAAF_Personal_Expense_33.db";
}

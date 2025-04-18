package com.adithya.aaafexpensemanager.util;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBHelperActions {
    public static final String SPLIT_TRANSFERS = "SplitTransfers";
    public static final String ACCOUNTS_ALL_VIEW = "accounts_all_view";
    public static final String RECURRING_SCHEDULE_NEXT_DATE = "RecurringScheduleNextDate";
    public static final String CURRENCY_ALL_DETAILS = "currency_all_details";
    public static final String RECURRING_TRANSACTIONS_VIEW = "recurring_transactions_view";
    public static final String TRANSACTIONS_VIEW = "transactions_view";
    public static final String FUTURE_SPLIT_TRANSFERS = "FutureSplitTransfers";
    public static final String SPLIT_ALL_TRANSFERS = "SplitAllTransfers";
    public static final String TRANSACTIONS = "transactions";
    public static final String ACCOUNTS = "accounts";
    public static final String CATEGORIES = "categories";
    public static final String RECENT_TRANSACTIONS = "recent_transactions";
    public static final String ACCOUNT_TYPES = "account_types";
    public static final String RECURRING_SCHEDULES = "recurring_schedules";
    public static final String RECURRING_TRANSACTIONS = "recurring_transactions";
    public static final String SETTINGS_PAIRS = "setting_pairs";
    public static final String BATCH_RUN_LOG = "batch_run_log";
    public static final String BATCH_RUN_DETAIL_LOG = "batch_run_detail_log";
    public static final String PRIMARY_CURRENCY = "primary_currency";
    public static final String CURRENCY = "currency";
    public static final String TAGS_MASTER = "tags_master";
    public static final String TRANSACTION_FILTER = "transaction_filter";

    public static void dropActionsV1(SQLiteDatabase db) {
        db.execSQL("DROP VIEW IF EXISTS SplitTransfers");
        db.execSQL("DROP VIEW IF EXISTS accounts_all_view");
        db.execSQL("DROP VIEW IF EXISTS RecurringScheduleNextDate");
        db.execSQL("DROP VIEW IF EXISTS currency_all_details");
        db.execSQL("DROP VIEW IF EXISTS recurring_transactions_view");
        db.execSQL("DROP VIEW IF EXISTS transactions_view");
        db.execSQL("DROP VIEW IF EXISTS FutureSplitTransfers");
        db.execSQL("DROP VIEW IF EXISTS SplitAllTransfers");
        // Drop the table if it exists
        db.execSQL("DROP TABLE IF EXISTS transactions");
        db.execSQL("DROP TABLE IF EXISTS accounts");
        db.execSQL("DROP TABLE IF EXISTS categories");
        db.execSQL("DROP TABLE IF EXISTS recent_transactions");
        db.execSQL("DROP TABLE IF EXISTS account_types");
        db.execSQL("DROP TABLE IF EXISTS recurring_schedules");
        db.execSQL("DROP TABLE IF EXISTS recurring_transactions");
        db.execSQL("DROP TABLE IF EXISTS setting_pairs");
        db.execSQL("DROP TABLE IF EXISTS batch_run_log");
        db.execSQL("DROP TABLE IF EXISTS batch_run_detail_log");
        db.execSQL("DROP TABLE IF EXISTS primary_currency");
        db.execSQL("DROP TABLE IF EXISTS currency");
        db.execSQL("DROP TABLE IF EXISTS tags_master");
        db.execSQL("DROP TABLE IF EXISTS transaction_filter");
        Log.d("Database Helper", "Dropped tables");
        Log.d("DatabaseHelper", "Recreating tables");
    }

    public static void createActionsV1(SQLiteDatabase db) {
        String CREATE_TRANSACTION_FILTER_TABLE = "CREATE TABLE transaction_filter (" +
                "report_name TEXT PRIMARY KEY," +
                "report_type TEXT," +
                "transaction_names TEXT," +
                "from_transaction_date INTEGER," +
                "to_transaction_date INTEGER," +
                "categories TEXT," +
                "account_names TEXT," +
                "to_account_names TEXT," +
                "from_amount REAL," +
                "to_amount REAL," +
                "transaction_types TEXT," +
                "search_text TEXT," +
                "account_types TEXT," +
                "period_name TEXT," +
                "account_tags TEXT)";
        db.execSQL(CREATE_TRANSACTION_FILTER_TABLE);
        String CREATE_TAGS_MASTER_TABLE = "CREATE TABLE tags_master (" +
                "tag_name TEXT PRIMARY KEY," +
                "tag_type TEXT NOT NULL)";
        db.execSQL(CREATE_TAGS_MASTER_TABLE);
        String CREATE_PRIMARY_CURRENCY_TABLE = "CREATE TABLE primary_currency (" +
                "primary_currency_code TEXT PRIMARY KEY)";
        db.execSQL(CREATE_PRIMARY_CURRENCY_TABLE);
        String CREATE_CURRENCY_TABLE = "CREATE TABLE currency (" +
                "currency_code TEXT PRIMARY KEY, " +
                "conversion_factor REAL)";
        db.execSQL(CREATE_CURRENCY_TABLE);
        currencyAllDetails(db);
        String CREATE_ACCOUNTS_TABLE = "CREATE TABLE accounts (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "account_name TEXT NOT NULL UNIQUE," +
                "account_type TEXT," +
                "account_balance REAL," +
                "account_tags TEXT," +
                "display_order INTEGER," +
                "currency_code TEXT NOT NULL, " +
                "close_account_ind INTEGER DEFAULT 0, " +
                "do_not_show_in_dropdown INTEGER DEFAULT 0 )";
        db.execSQL(CREATE_ACCOUNTS_TABLE);
        String CREATE_TRANSACTIONS_TABLE = "CREATE TABLE transactions (" +
                "transaction_uuid TEXT PRIMARY KEY," +
                "transaction_name TEXT NOT NULL," +
                "transaction_date INTEGER," +
                "transaction_type TEXT," +
                "category TEXT," +
                "notes TEXT," +
                "amount REAL," +
                "account_name TEXT," +
                "to_account_name TEXT," +
                "create_date INTEGER," +
                "last_update_date INTEGER," +
                "transfer_ind TEXT," +
                "recurring_schedule_uuid TEXT)";
        db.execSQL(CREATE_TRANSACTIONS_TABLE);
        createSplitTransfersView(db);
        db.execSQL("CREATE TABLE categories (" +
                "category_uuid TEXT PRIMARY KEY, " +
                "category_name TEXT NOT NULL UNIQUE," +
                "parent_category TEXT)");
        String CREATE_RECENT_TRAN_TABLE = "CREATE TABLE recent_transactions (" +
                "transaction_name TEXT PRIMARY KEY," +
                "transaction_type TEXT," +
                "category TEXT," +
                "notes TEXT," +
                "amount REAL," +
                "account_name TEXT," +
                "to_account_name TEXT," +
                "create_date INTEGER," +
                "last_update_date INTEGER)";
        db.execSQL(CREATE_RECENT_TRAN_TABLE);
        String ACCOUNT_TYPE_TABLE = "CREATE TABLE account_types (" +
                "account_type TEXT PRIMARY KEY," +
                "account_type_display_order INTEGER)";
        db.execSQL(ACCOUNT_TYPE_TABLE);
        createAccountsAllView(db);
        String CREATE_RECURRING_SCHEDULE_TABLE = "CREATE TABLE recurring_schedules (" +
                "recurring_schedule_uuid TEXT PRIMARY KEY, " +
                "transaction_name TEXT NOT NULL, " +
                "recurring_schedule TEXT, " +
                "repeat_interval_days INTEGER, " +
                "recurring_start_date INTEGER, " +
                "recurring_end_date INTEGER, " +
                "category TEXT, " +
                "notes TEXT, " +
                "transaction_type TEXT, " +
                "amount REAL, " +
                "account_name TEXT, " +
                "to_account_name TEXT, " +
                "create_date INTEGER, " +
                "last_update_date INTEGER," +
                "transfer_ind TEXT)";
        db.execSQL(CREATE_RECURRING_SCHEDULE_TABLE);
        String CREATE_RECURRING_TRANSACTIONS_TABLE = "CREATE TABLE recurring_transactions (" +
                "transaction_uuid TEXT PRIMARY KEY," +
                "recurring_schedule_uuid TEXT, " +
                "transaction_name TEXT NOT NULL," +
                "transaction_date INTEGER," +
                "transaction_type TEXT," +
                "category TEXT," +
                "notes TEXT," +
                "amount REAL," +
                "account_name TEXT," +
                "to_account_name TEXT," +
                "create_date INTEGER," +
                "last_update_date INTEGER," +
                "transfer_ind TEXT)";
        db.execSQL(CREATE_RECURRING_TRANSACTIONS_TABLE);
        recurringScheduleNextDate(db);
        createRecurringTransactionsView(db);
        createTransactionsAllView(db);
        String SETTINGS_TABLE_QUERY = "CREATE TABLE setting_pairs " +
                "( " +
                "setting_name TEXT PRIMARY KEY, " +
                "setting_value TEXT )";
        db.execSQL(SETTINGS_TABLE_QUERY);
        String BATCH_RUN_LOG = "CREATE TABLE batch_run_log " +
                "( " +
                "batch_run_uuid TEXT PRIMARY KEY, " +
                "batch_run_date INTEGER)";
        db.execSQL(BATCH_RUN_LOG);
        String BATCH_RUN_DETAIL_LOG = "CREATE TABLE batch_run_detail_log " +
                "( " +
                "batch_run_detail_uuid TEXT PRIMARY KEY, " +
                "batch_run_uuid TEXT, " +
                "tag            TEXT, " +
                "log_text       TEXT, " +
                "log_date       INTEGER )";
        db.execSQL(BATCH_RUN_DETAIL_LOG);
        createFutureSplitTransfersView(db);
        createSplitAllTransfersView(db);
    }

    private static void currencyAllDetails(SQLiteDatabase db) {
        db.execSQL("CREATE VIEW IF NOT EXISTS currency_all_details AS " +
                "SELECT " +
                "c1.currency_code," +
                "c1.conversion_factor," +
                "pc1.primary_currency_code, " +
                "CASE WHEN c1.currency_code = pc1.primary_currency_code THEN 1 ELSE 0 END AS is_primary " +
                "FROM currency c1 " +
                "LEFT JOIN primary_currency pc1 " +
                "ON (1=1)");
    }

    private static void recurringScheduleNextDate(SQLiteDatabase db) {
        db.execSQL("CREATE VIEW IF NOT EXISTS RecurringScheduleNextDate AS " +  // Use IF NOT EXISTS for upgrades
                "SELECT " +
                "t1.recurring_schedule_uuid , " +
                "t1.transaction_name, " +
                "t1.recurring_schedule, " +
                "t1.repeat_interval_days, " +
                "t1.recurring_start_date, " +
                "t1.recurring_end_date, " +
                "t1.category, " +
                "t1.notes, " +
                "t1.transaction_type, " +
                "t1.amount, " +
                "t1.account_name, " +
                "t1.to_account_name, " +
                "t1.create_date, " +
                "t1.last_update_date," +
                "t1.transfer_ind, " +
                "curr1.currency_code, " +
                "curr1.conversion_factor, " +
                "curr1.primary_currency_code, " +
                "MIN(t2.transaction_date) AS next_date " +
                "FROM recurring_schedules t1 " +
                "LEFT JOIN recurring_transactions t2 " +
                "ON t1.recurring_schedule_uuid = t2.recurring_schedule_uuid " +
                "LEFT JOIN accounts ac1 " +
                "ON t1.account_name = ac1.account_name " +
                "LEFT JOIN currency_all_details curr1 " +
                "ON ac1.currency_code = curr1.currency_code " +
                "WHERE 1=1 " +
                "GROUP BY " +
                "t1.recurring_schedule_uuid , " +
                "t1.transaction_name, " +
                "t1.recurring_schedule, " +
                "t1.repeat_interval_days, " +
                "t1.recurring_start_date, " +
                "t1.recurring_end_date, " +
                "t1.category, " +
                "t1.notes, " +
                "t1.transaction_type, " +
                "t1.amount, " +
                "t1.account_name, " +
                "t1.to_account_name, " +
                "t1.create_date, " +
                "t1.last_update_date," +
                "t1.transfer_ind, " +
                "curr1.currency_code, " +
                "curr1.conversion_factor, " +
                "curr1.primary_currency_code ");
    }

    private static void createSplitTransfersView(SQLiteDatabase db) {
        String SPLIT_TRANSFERS_VIEW =
                """
                        CREATE VIEW IF NOT EXISTS SplitTransfers AS
                        SELECT t1.*,
                        SUM(CASE
                            WHEN t1.transaction_type = 'Income' THEN t1.amount
                            WHEN t1.transaction_type = 'Expense' THEN (-1) * t1.amount
                            END) OVER(PARTITION BY t1.account_name
                        	              ORDER BY t1.transaction_date ASC
                        						  ,t1.create_date ASC
                        						  ,t1.transaction_uuid
                        			  ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) account_balance
                        FROM
                        (SELECT
                        t1.transaction_uuid,
                        t1.transaction_name,
                        t1.transaction_date,
                        t1.transaction_type,
                        t1.category,
                        t1.notes,
                        t1.amount,
                        t1.account_name,
                        t1.to_account_name,
                        t1.create_date,
                        t1.last_update_date,
                        t1.transaction_type transfer_ind,
                        curr1.currency_code,
                        curr1.conversion_factor,
                        curr1.primary_currency_code,
                        t1.recurring_schedule_uuid
                        FROM transactions t1
                        LEFT JOIN accounts ac1
                        ON t1.account_name = ac1.account_name
                        LEFT JOIN currency_all_details curr1
                        ON ac1.currency_code = curr1.currency_code
                        WHERE transaction_type != 'Transfer'
                        UNION ALL
                        SELECT
                        t1.transaction_uuid,
                        t1.transaction_name,
                        t1.transaction_date,
                        'Expense' as transaction_type,
                        t1.category,
                        t1.notes,
                        t1.amount,
                        t1.account_name,
                        '' as to_account_name,
                        t1.create_date,
                        t1.last_update_date,
                        t1.transaction_type transfer_ind,
                        curr1.currency_code,
                        curr1.conversion_factor,
                        curr1.primary_currency_code,
                        t1.recurring_schedule_uuid
                        FROM transactions t1
                        LEFT JOIN accounts ac1
                        ON t1.account_name = ac1.account_name
                        LEFT JOIN currency_all_details curr1
                        ON ac1.currency_code = curr1.currency_code
                        WHERE transaction_type = 'Transfer'
                        UNION ALL
                        SELECT
                        t1.transaction_uuid,
                        t1.transaction_name,
                        t1.transaction_date,
                        'Income' as transaction_type,
                        t1.category,
                        t1.notes,
                        t1.amount,
                        t1.to_account_name AS account_name,
                        '' as to_account_name,
                        t1.create_date,
                        t1.last_update_date,
                        t1.transaction_type transfer_ind,
                        curr1.currency_code,
                        curr1.conversion_factor,
                        curr1.primary_currency_code,
                        t1.recurring_schedule_uuid
                        FROM transactions t1
                        LEFT JOIN accounts ac1
                        ON t1.to_account_name = ac1.account_name
                        LEFT JOIN currency_all_details curr1
                        ON ac1.currency_code = curr1.currency_code
                        WHERE transaction_type = 'Transfer') t1
                        """;
        db.execSQL(SPLIT_TRANSFERS_VIEW);
    }

    private static void createFutureSplitTransfersView(SQLiteDatabase db) {
        db.execSQL("CREATE VIEW IF NOT EXISTS FutureSplitTransfers AS " +
                "SELECT " +
                "t1.transaction_uuid, " +
                "t1.transaction_name, " +
                "t1.transaction_date, " +
                "t1.transaction_type, " +
                "t1.category, " +
                "t1.notes, " +
                "t1.amount, " +
                "t1.account_name, " +
                "t1.to_account_name," +
                "t1.create_date," +
                "t1.last_update_date," +
                "t1.transaction_type transfer_ind," +
                "curr1.currency_code, " +
                "curr1.conversion_factor, " +
                "curr1.primary_currency_code, " +
                "t1.recurring_schedule_uuid " +
                "FROM recurring_transactions t1 " +
                "LEFT JOIN accounts ac1 " +
                "ON t1.account_name = ac1.account_name " +
                "LEFT JOIN currency_all_details curr1 " +
                "ON ac1.currency_code = curr1.currency_code " +
                "WHERE transaction_type != 'Transfer' " +
                "UNION ALL " +
                "SELECT " +
                "t1.transaction_uuid, " +
                "t1.transaction_name, " +
                "t1.transaction_date, " +
                "'Expense' as transaction_type, " +
                "t1.category, " +
                "t1.notes, " +
                "t1.amount, " +
                "t1.account_name, " +
                "'' as to_account_name," +
                "t1.create_date," +
                "t1.last_update_date," +
                "t1.transaction_type transfer_ind," +
                "curr1.currency_code, " +
                "curr1.conversion_factor, " +
                "curr1.primary_currency_code, " +
                "t1.recurring_schedule_uuid " +
                "FROM recurring_transactions t1 " +
                "LEFT JOIN accounts ac1 " +
                "ON t1.account_name = ac1.account_name " +
                "LEFT JOIN currency_all_details curr1 " +
                "ON ac1.currency_code = curr1.currency_code " +
                "WHERE transaction_type = 'Transfer' " +
                "UNION ALL " +
                "SELECT " +
                "t1.transaction_uuid, " +
                "t1.transaction_name, " +
                "t1.transaction_date, " +
                "'Income' as transaction_type, " +
                "t1.category, " +
                "t1.notes, " +
                "t1.amount, " +
                "t1.to_account_name AS account_name, " +
                "'' as to_account_name, " +
                "t1.create_date," +
                "t1.last_update_date," +
                "t1.transaction_type transfer_ind," +
                "curr1.currency_code, " +
                "curr1.conversion_factor, " +
                "curr1.primary_currency_code, " +
                "t1.recurring_schedule_uuid " +
                "FROM recurring_transactions t1 " +
                "LEFT JOIN accounts ac1 " +
                "ON t1.to_account_name = ac1.account_name " +
                "LEFT JOIN currency_all_details curr1 " +
                "ON ac1.currency_code = curr1.currency_code " +
                "WHERE transaction_type = 'Transfer' ");
    }

    private static void createSplitAllTransfersView(SQLiteDatabase db) {
        String CREATE_SPLIT_ALL_TRANSFERS =
                """
                        CREATE VIEW IF NOT EXISTS SplitAllTransfers AS
                        SELECT t1.*,
                               CASE
                                   WHEN t1.transaction_type = 'Income' THEN t1.amount
                                   WHEN t1.transaction_type = 'Expense' THEN (-1) * t1.amount
                               END signed_amount,
                               DATE(
                                   SUBSTR(transaction_date, 1, 4) || '-' ||
                                   SUBSTR(transaction_date, 5, 2) || '-' ||
                                   SUBSTR(transaction_date, 7, 2)
                               ) txn_dt
                          FROM SplitTransfers t1
                        UNION ALL
                        SELECT t1.*,
                               CASE
                                   WHEN t1.transaction_type = 'Income' THEN t1.amount
                                   WHEN t1.transaction_type = 'Expense' THEN (-1) * t1.amount
                               END signed_amount,
                               DATE(
                                   SUBSTR(transaction_date, 1, 4) || '-' ||
                                   SUBSTR(transaction_date, 5, 2) || '-' ||
                                   SUBSTR(transaction_date, 7, 2)
                               ) txn_dt
                          FROM FutureSplitTransfers t1;
                        """;
        db.execSQL(CREATE_SPLIT_ALL_TRANSFERS);
    }

    private static void createAccountsAllView(SQLiteDatabase db) {
        db.execSQL("""
                CREATE VIEW IF NOT EXISTS accounts_all_view AS
                select ac1.*,
                  curr1.currency_code,
                  curr1.conversion_factor,
                  curr1.primary_currency_code
                  from accounts ac1
                  left join account_types at1
                		 on ac1.account_type = at1.account_type
                 left join currency_all_details curr1
                		on ac1.currency_code = curr1.currency_code
                order by at1.account_type_display_order ASC,
                 at1.account_type ASC,
                 ac1.display_order ASC,
                 ac1.account_name ASC""");
    }

    private static void createRecurringTransactionsView(SQLiteDatabase db) {
        db.execSQL("CREATE VIEW IF NOT EXISTS recurring_transactions_view AS " +
                "SELECT " +
                "t1.transaction_uuid, " +
                "t1.recurring_schedule_uuid, " +
                "t1.transaction_name, " +
                "t1.transaction_date, " +
                "t1.transaction_type, " +
                "t1.category, " +
                "t1.notes, " +
                "t1.amount, " +
                "t1.account_name, " +
                "t1.to_account_name," +
                "t1.create_date," +
                "t1.last_update_date," +
                "t1.transfer_ind, " +
                "curr1.currency_code, " +
                "curr1.conversion_factor, " +
                "curr1.primary_currency_code " +
                " FROM recurring_transactions t1 " +
                " LEFT JOIN accounts ac1 " +
                " ON t1.account_name = ac1.account_name " +
                " LEFT JOIN currency_all_details curr1 " +
                " ON ac1.currency_code = curr1.currency_code");
    }

    private static void createTransactionsAllView(SQLiteDatabase db) {
        db.execSQL("CREATE VIEW IF NOT EXISTS transactions_view AS " +
                "SELECT " +
                "t1.transaction_uuid, " +
                "t1.recurring_schedule_uuid, " +
                "t1.transaction_name, " +
                "t1.transaction_date, " +
                "t1.transaction_type, " +
                "t1.category, " +
                "t1.notes, " +
                "t1.amount, " +
                "t1.account_name, " +
                "t1.to_account_name," +
                "t1.create_date," +
                "t1.last_update_date," +
                "t1.transfer_ind, " +
                "curr1.currency_code, " +
                "curr1.conversion_factor, " +
                "curr1.primary_currency_code, " +
                "0.0 as account_balance " +
                " FROM transactions t1 " +
                " LEFT JOIN accounts ac1 " +
                " ON t1.account_name = ac1.account_name " +
                " LEFT JOIN currency_all_details curr1 " +
                " ON ac1.currency_code = curr1.currency_code");
    }

    public static void dropCreateActionsV2(SQLiteDatabase db) {
        Log.d("Database Helper", "Started dropCreateActionsV2");
        db.execSQL("DROP VIEW IF EXISTS " + SPLIT_ALL_TRANSFERS);
        createSplitAllTransfersView(db);
        Log.d("Database Helper", "Completed dropCreateActionsV2");
    }

    public static void dropCreateActionsV3(SQLiteDatabase db) {
        Log.d("Database Helper", "Started dropCreateActionsV3");
        db.execSQL("DROP VIEW IF EXISTS " + SPLIT_TRANSFERS);
        createSplitTransfersView(db);
        Log.d("Database Helper", "Completed dropCreateActionsV3");
    }

    public static void dropCreateActionsV4(SQLiteDatabase db) {
        Log.d("Database Helper", "Started dropCreateActionsV4");
        db.execSQL("DROP VIEW IF EXISTS " + TRANSACTIONS_VIEW);
        createTransactionsAllView(db);
        Log.d("Database Helper", "Completed dropCreateActionsV4");
    }
}

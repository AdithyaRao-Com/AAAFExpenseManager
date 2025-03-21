package com.adithya.aaafexpensemanager.util;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBHelperActions {
    public static void dropActions(SQLiteDatabase db) {
        db.execSQL("DROP VIEW IF EXISTS SplitTransfers");
        db.execSQL("DROP VIEW IF EXISTS accounts_all_view");
        db.execSQL("DROP VIEW IF EXISTS RecurringScheduleNextDate");
        db.execSQL("DROP VIEW IF EXISTS currency_all_details");
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
        Log.d("Database Helper","Dropped tables");
        Log.d("DatabaseHelper","Recreating tables");
    }
    public static void createActions(SQLiteDatabase db) {
        String CREATE_PRIMARY_CURRENCY_TABLE = "CREATE TABLE primary_currency (" +
                "primary_currency_name TEXT PRIMARY KEY)";
        db.execSQL(CREATE_PRIMARY_CURRENCY_TABLE);
        String CREATE_CURRENCY_TABLE = "CREATE TABLE currency (" +
                "currency_name TEXT PRIMARY KEY, " +
                "conversion_factor REAL)";
        db.execSQL(CREATE_CURRENCY_TABLE);
        currencyAllDetails(db);
        String CREATE_ACCOUNTS_TABLE = "CREATE TABLE accounts (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "account_name TEXT NOT NULL," +
                "account_type TEXT," +
                "account_balance REAL," +
                "account_tags TEXT," +
                "display_order INTEGER," +
                "currency_code TEXT)";
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
                "account_name TEXT, "+
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
                "tag            TEXT, "+
                "log_text       TEXT, " +
                "log_date       INTEGER )";
        db.execSQL(BATCH_RUN_DETAIL_LOG);
    }
    private static void currencyAllDetails(SQLiteDatabase db){
        db.execSQL("CREATE VIEW IF NOT EXISTS currency_all_details AS " +
                "SELECT " +
                "c1.currency_name," +
                "c1.conversion_factor," +
                "pc1.primary_currency_name, " +
                "CASE WHEN c1.currency_name = pc1.primary_currency_name THEN 1 ELSE 0 END AS is_primary " +
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
                "t1.account_name, "+
                "t1.to_account_name, " +
                "t1.create_date, " +
                "t1.last_update_date," +
                "t1.transfer_ind, " +
                "curr1.currency_name, " +
                "curr1.conversion_factor, " +
                "curr1.primary_currency_name, " +
                "MIN(t2.transaction_date) AS next_date " +
                "FROM recurring_schedules t1 " +
                "LEFT JOIN recurring_transactions t2 " +
                "ON t1.recurring_schedule_uuid = t2.recurring_schedule_uuid " +
                "LEFT JOIN accounts ac1 " +
                "ON t1.account_name = ac1.account_name " +
                "LEFT JOIN currency_all_details curr1 " +
                "ON t1.currency_code = curr1.currency_name " +
                "WHERE 1=1 " +
                "GROUP BY "+
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
                "t1.account_name, "+
                "t1.to_account_name, " +
                "t1.create_date, " +
                "t1.last_update_date," +
                "t1.transfer_ind, " +
                "curr1.currency_name, " +
                "curr1.conversion_factor, " +
                "curr1.primary_currency_name ");
    }
    private static void createSplitTransfersView(SQLiteDatabase db) {
        db.execSQL("CREATE VIEW IF NOT EXISTS SplitTransfers AS " +
                "SELECT " +
                "transaction_uuid, " +
                "transaction_name, " +
                "transaction_date, " +
                "transaction_type, " +
                "category, " +
                "notes, " +
                "amount, " +
                "account_name, " +
                "to_account_name," +
                "create_date," +
                "last_update_date," +
                "transaction_type transfer_ind," +
                "recurring_schedule_uuid " +
                "FROM transactions " +
                "LEFT JOIN accounts ac1 " +
                "ON transactions.account_name = ac1.account_name " +
                "LEFT JOIN currency_all_details curr1 " +
                "ON ac1.currency_code = curr1.currency_name " +
                "WHERE transaction_type != 'Transfer' " +
                "UNION ALL " +
                "SELECT " +
                "transaction_uuid, " +
                "transaction_name, " +
                "transaction_date, " +
                "'Expense' as transaction_type, " +
                "category, " +
                "notes, " +
                "amount, " +
                "account_name, " +
                "'' as to_account_name," +
                "create_date," +
                "last_update_date," +
                "transaction_type transfer_ind," +
                "recurring_schedule_uuid " +
                "FROM transactions " +
                "LEFT JOIN accounts ac1 " +
                "ON transactions.account_name = ac1.account_name " +
                "LEFT JOIN currency_all_details curr1 " +
                "ON ac1.currency_code = curr1.currency_name " +
                "WHERE transaction_type != 'Transfer' " +
                "UNION ALL " +
                "SELECT " +
                "transaction_uuid, " +
                "transaction_name, " +
                "transaction_date, " +
                "'Income' as transaction_type, " +
                "category, " +
                "notes, " +
                "amount, " +
                "to_account_name AS account_name, " +
                "'' as to_account_name, " +
                "create_date," +
                "last_update_date," +
                "transaction_type transfer_ind," +
                "recurring_schedule_uuid " +
                "FROM transactions " +
                "LEFT JOIN accounts ac1 " +
                "ON transactions.to_account_name = ac1.account_name " +
                "LEFT JOIN currency_all_details curr1 " +
                "ON ac1.currency_code = curr1.currency_name " +
                "WHERE transaction_type != 'Transfer' ");
    }
    private static void createAccountsAllView(SQLiteDatabase db) {
        db.execSQL("CREATE VIEW IF NOT EXISTS accounts_all_view AS \n" +
                "select ac1.*,\n" +
                "  curr1.currency_name, "+
                "  curr1.conversion_factor, "+
                "  curr1.primary_currency_name "+
                "  from accounts ac1\n" +
                "  left join account_types at1\n" +
                "         on ac1.account_type = at1.account_type\n" +
                " left join currency_all_details curr1 " +
                "        on ac1.currency_code = curr1.currency_name " +
                "order by at1.account_type_display_order ASC, ac1.display_order ASC");
    }
}

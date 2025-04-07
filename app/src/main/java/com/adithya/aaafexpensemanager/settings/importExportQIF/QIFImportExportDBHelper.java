package com.adithya.aaafexpensemanager.settings.importExportQIF;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class QIFImportExportDBHelper extends SQLiteOpenHelper {
    public static final String TABLE_TRANSACTIONS = "qif_interface_table";
    private static final String DATABASE_NAME = "QIFDatabase.db";
    private static final int DATABASE_VERSION = 2;
    private static final String SQL_CREATE_TRANSACTIONS =
            "CREATE TABLE " + TABLE_TRANSACTIONS + " (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "account_name TEXT," +
                    "account_type TEXT," +
                    "transaction_date TEXT," +
                    "amount TEXT," +
                    "payee TEXT," +
                    "memo TEXT," +
                    "category TEXT)";

    QIFImportExportDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TRANSACTIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_TRANSACTIONS + " ADD COLUMN category TEXT;");
        }
    }
}

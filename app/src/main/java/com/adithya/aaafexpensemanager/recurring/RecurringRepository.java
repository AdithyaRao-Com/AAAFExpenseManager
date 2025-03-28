package com.adithya.aaafexpensemanager.recurring;
import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.NonNull;

import com.adithya.aaafexpensemanager.account.Account;
import com.adithya.aaafexpensemanager.account.AccountRepository;
import com.adithya.aaafexpensemanager.futureTransaction.FutureTransactionRepository;
import com.adithya.aaafexpensemanager.transaction.exception.InterCurrencyTransferNotSupported;
import com.adithya.aaafexpensemanager.transactionFilter.TransactionFilter;
import com.adithya.aaafexpensemanager.transactionFilter.TransactionFilterUtils;
import com.adithya.aaafexpensemanager.util.AppConstants;
import com.adithya.aaafexpensemanager.util.DatabaseHelper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/** @noinspection unused, CallToPrintStackTrace , UnusedReturnValue */
public class RecurringRepository {
    private final SQLiteDatabase db;
    private static final String INSERTS = "Insert";
    private static final String UPDATES = "Update";
    /** @noinspection unused*/
    private static final String DELETES = "Delete";
    /** @noinspection FieldCanBeLocal*/
    private final int batchSize = AppConstants.BATCH_SIZE;

    private final FutureTransactionRepository futureTransactionRepository;

    public int recordCount = 0;
    private final Application application;
    /** @noinspection resource*/
    public RecurringRepository(Application application) {
        DatabaseHelper dbHelper = new DatabaseHelper(application);
        db = dbHelper.getWritableDatabase();
        this.application = application;
        futureTransactionRepository = new FutureTransactionRepository(application);
    }

    public List<RecurringSchedule> getAllRecurringSchedules(TransactionFilter transactionFilters, int pageNumber) {
        List<RecurringSchedule> recurringSchedules = new ArrayList<>();
        disableTransactionDateFilters(transactionFilters);
        HashMap<String, Object> queryAllData = TransactionFilterUtils.generateTransactionFilterQuery(transactionFilters,application);
        String queryString = Objects.requireNonNull(queryAllData.get("QUERY")).toString();
        //noinspection unchecked
        ArrayList<String> queryParms = (ArrayList<String>) queryAllData.get("VALUES");
        assert queryParms != null;
        String orderByArgs;
        if(pageNumber<0) {
            orderByArgs = "next_date ASC,create_date DESC";
        }
        else {
            orderByArgs = "next_date ASC,create_date DESC LIMIT <<batchSize>> OFFSET <<offset>>"
                    .replace("<<batchSize>>",String.valueOf(batchSize))
                    .replace("<<offset>>",String.valueOf((pageNumber-1)*batchSize));
        }
        try (Cursor cursor = db.query("RecurringScheduleNextDate", null, queryString, queryParms.toArray(new String[0]), null, null, orderByArgs)){
            if (cursor.moveToFirst()) {
                do {
                    RecurringSchedule recurringSchedule = getRecurringSchedulesFromCursor(cursor);
                    if (recurringSchedule != null) {
                        recurringSchedules.add(recurringSchedule);
                    }
                } while (cursor.moveToNext());
            }
        }
        return recurringSchedules;
    }
    private void disableTransactionDateFilters(TransactionFilter transactionFilters) {
        transactionFilters.fromTransactionDate = 0;
        transactionFilters.toTransactionDate = 0;
    }
    private RecurringSchedule getRecurringSchedulesFromCursor(Cursor cursor) {
        try {
            int uuidIndex = cursor.getColumnIndexOrThrow("recurring_schedule_uuid");
            int nameIndex = cursor.getColumnIndexOrThrow("transaction_name");
            int recurringScheduleIndex = cursor.getColumnIndexOrThrow("recurring_schedule");
            int repeatIntervalDaysIndex = cursor.getColumnIndexOrThrow("repeat_interval_days");
            int recurringStartDateIndex = cursor.getColumnIndexOrThrow("recurring_start_date");
            int recurringEndDateIndex = cursor.getColumnIndexOrThrow("recurring_end_date");
            int typeIndex = cursor.getColumnIndexOrThrow("transaction_type");
            int categoryIndex = cursor.getColumnIndexOrThrow("category");
            int notesIndex = cursor.getColumnIndexOrThrow("notes");
            int amountIndex = cursor.getColumnIndexOrThrow("amount");
            int accountIndex = cursor.getColumnIndexOrThrow("account_name");
            int toAccountIndex = cursor.getColumnIndexOrThrow("to_account_name");
            int createDateTimeIndex = cursor.getColumnIndexOrThrow("create_date");
            int lastUpdateDateTimeIndex = cursor.getColumnIndexOrThrow("last_update_date");
            int transferIndIndex = cursor.getColumnIndexOrThrow("transfer_ind");
            int nextDateIndex = cursor.getColumnIndexOrThrow("next_date");
            int currencyCodeIndex = cursor.getColumnIndexOrThrow("currency_code");
            int conversionFactorIndex = cursor.getColumnIndexOrThrow("conversion_factor");
            int primaryCurrencyCodeIndex = cursor.getColumnIndexOrThrow("primary_currency_code");
            String transactionUUIDStr = cursor.getString(uuidIndex);
            UUID transactionUUID = UUID.fromString(transactionUUIDStr);
            String transactionName = cursor.getString(nameIndex);
            String recurringSchedule = cursor.getString(recurringScheduleIndex);
            int repeatIntervalDays = cursor.getInt(repeatIntervalDaysIndex);
            int recurringStartDate = cursor.getInt(recurringStartDateIndex);
            int recurringEndDate = cursor.getInt(recurringEndDateIndex);
            String transactionType = cursor.getString(typeIndex);
            String category = cursor.getString(categoryIndex);
            String notes = cursor.getString(notesIndex);
            double amount = cursor.getDouble(amountIndex);
            String accountName = cursor.getString(accountIndex);
            String toAccountName = cursor.getString(toAccountIndex);
            long createDateTime = cursor.getLong(createDateTimeIndex);
            long lastUpdateDateTime = cursor.getLong(lastUpdateDateTimeIndex);
            String transferInd = cursor.getString(transferIndIndex);
            int nextDate = cursor.getInt(nextDateIndex);
            String currencyCode = cursor.getString(currencyCodeIndex);
            double conversionFactor = cursor.getDouble(conversionFactorIndex);
            String primaryCurrencyCode = cursor.getString(primaryCurrencyCodeIndex);
            return new RecurringSchedule(transactionUUID,transactionName,recurringSchedule,
                    repeatIntervalDays,recurringStartDate,recurringEndDate,notes,transactionType,
                    category,amount,accountName,toAccountName,createDateTime,lastUpdateDateTime,
                    transferInd,nextDate,currencyCode,conversionFactor,primaryCurrencyCode);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean addRecurringSchedule(RecurringSchedule recurringSchedule) {
        ContentValues values = getContentValuesForChange(recurringSchedule,INSERTS);
        try {
            long result = db.insertOrThrow("recurring_schedules", null, values);
            this.recordCount = this.recordCount + 1;
            if(this.recordCount%100==0){
                Log.d("RecurringRepository",this.recordCount + "records added");
            }
            futureTransactionRepository.deleteFutureTransactions(recurringSchedule);
            futureTransactionRepository.insertAllRecurringTransactions(recurringSchedule, LocalDate.now());
            return true;
        } catch (SQLException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            return false;
        }
    }

    public boolean keepFutureTransactionsUpToDate(){
        try{
            List<RecurringSchedule> recurringSchedules =
                    getAllRecurringSchedules(new TransactionFilter(),-1);
            for(RecurringSchedule recurringSchedule : recurringSchedules){
                futureTransactionRepository.insertAllRecurringTransactions(recurringSchedule, LocalDate.now());
            }
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /** @noinspection unused*/
    public void updateRecurringSchedule(RecurringSchedule recurringSchedule) {
        ContentValues values = getContentValuesForChange(recurringSchedule,UPDATES);
        String whereClause = "recurring_schedule_uuid = ?";
        String[] whereArgs = new String[]{recurringSchedule.recurringScheduleUUID.toString()};
        int rowsAffected = db.update("recurring_schedules", values, whereClause, whereArgs);
        futureTransactionRepository.deleteFutureTransactions(recurringSchedule);
        futureTransactionRepository.insertAllRecurringTransactions(recurringSchedule, LocalDate.now());
    }
    @NonNull
    private ContentValues getContentValuesForChange(RecurringSchedule recurringSchedule,String operationType) {
        checkInterCurrencyTransfers(recurringSchedule);
        ContentValues values = new ContentValues();
        values.put("transaction_name", recurringSchedule.transactionName);
        values.put("recurring_schedule", recurringSchedule.recurringScheduleName);
        values.put("repeat_interval_days", recurringSchedule.repeatIntervalDays);
        values.put("recurring_start_date", recurringSchedule.recurringStartDate);
        values.put("recurring_end_date", recurringSchedule.recurringEndDate);
        values.put("transaction_type", recurringSchedule.transactionType);
        values.put("transfer_ind", recurringSchedule.transactionType);
        values.put("category", recurringSchedule.category);
        values.put("notes", recurringSchedule.notes);
        values.put("amount", Math.round(recurringSchedule.amount*100.0)/100.0);
        values.put("account_name", recurringSchedule.accountName);
        values.put("to_account_name", recurringSchedule.toAccountName);
        if(operationType.equals(INSERTS)){
            values.put("recurring_schedule_uuid", recurringSchedule.recurringScheduleUUID.toString());
            values.put("create_date", recurringSchedule.createDateTime);
        }
        values.put("last_update_date", recurringSchedule.lastUpdateDateTime);
        return values;
    }

    private void checkInterCurrencyTransfers(RecurringSchedule recurringSchedule) {
        if(recurringSchedule.transactionType.equals("Transfer")){
            AccountRepository accountRepository = new AccountRepository(this.application);
            Account accountFrom = accountRepository.getAccountByName(recurringSchedule.accountName);
            Account accountTo = accountRepository.getAccountByName(recurringSchedule.toAccountName);
            if(!(accountFrom.currencyCode.equals(accountTo.currencyCode))){
                throw new InterCurrencyTransferNotSupported(accountFrom.currencyCode,accountTo.currencyCode);
            }
        }
    }

    public RecurringSchedule getRecurringScheduleById(UUID recurringScheduleUUID) {
        final RecurringSchedule[] result = new RecurringSchedule[1]; // Array to hold the result
        try (Cursor cursor = db.rawQuery("SELECT * FROM RecurringScheduleNextDate WHERE recurring_schedule_uuid = ?", new String[]{recurringScheduleUUID.toString()})) {
            if (cursor.moveToFirst()) {
                return getRecurringSchedulesFromCursor(cursor);
            }
        }
        return null;
    }
    public void deleteRecurringSchedule(RecurringSchedule recurringSchedule) {
        try {
            int rowsAffected = db.delete("recurring_schedules", "recurring_schedule_uuid = ?", new String[]{recurringSchedule.recurringScheduleUUID.toString()});
            futureTransactionRepository.deleteFutureTransactions(recurringSchedule);
        } catch (RuntimeException ignored) {

        }
    }
    public void deleteAll(){
        try {
            int rowsAffected = db.delete("recurring_schedules", null, null);
        } catch (RuntimeException ignored) {
        }
    }
    public boolean deleteInvalidSchedules() {
        try {
            int rowsAffected = db.delete("recurring_schedules", "recurring_end_date < ?",
                    new String[]{LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))});
            futureTransactionRepository.deleteInvalidFutureTxns();
            return true;
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return false;
    }
}
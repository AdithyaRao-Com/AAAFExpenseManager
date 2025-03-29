package com.adithya.aaafexpensemanager.futureTransaction;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.adithya.aaafexpensemanager.account.Account;
import com.adithya.aaafexpensemanager.account.AccountRepository;
import com.adithya.aaafexpensemanager.recurring.RecurringSchedule;
import com.adithya.aaafexpensemanager.transaction.TransactionRepository;
import com.adithya.aaafexpensemanager.transaction.exception.InterCurrencyTransferNotSupported;
import com.adithya.aaafexpensemanager.transactionFilter.TransactionFilter;
import com.adithya.aaafexpensemanager.transactionFilter.TransactionFilterUtils;
import com.adithya.aaafexpensemanager.util.AppConstants;
import com.adithya.aaafexpensemanager.util.DatabaseHelper;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @noinspection CallToPrintStackTrace, UnusedReturnValue
 */
public class FutureTransactionRepository {
    private static final String INSERTS = "Insert";
    private static final String UPDATES = "Update";
    /**
     * @noinspection unused
     */
    private static final String DELETES = "Delete";
    final LocalDate TRANSACTION_DATE_DUMMY = AppConstants.TRANSACTION_DATE_DUMMY;
    final TransactionRepository transactionRepository;
    private final SQLiteDatabase db;
    private final Application application;
    private RecurringSchedule recurringSchedule;

    public FutureTransactionRepository(Application application) {
        //noinspection resource
        DatabaseHelper dbHelper = new DatabaseHelper(application);
        db = dbHelper.getWritableDatabase();
        this.application = application;
        transactionRepository = new TransactionRepository(application);
    }

    public FutureTransaction getNextTransaction(RecurringSchedule recurringSchedule) {
        int transactionDateInt;
        LocalDate transactionDate = LocalDate.now();
        try (Cursor cursor = db.rawQuery("SELECT MIN(transaction_date) transaction_date FROM recurring_transactions WHERE recurring_schedule_uuid = ?", new String[]{recurringSchedule.recurringScheduleUUID.toString()})) {
            if (cursor.moveToFirst()) {
                transactionDateInt = cursor.getInt(0);
                transactionDate = LocalDate.parse(String.valueOf(transactionDateInt), DateTimeFormatter.ofPattern("yyyyMMdd"));
            }
        } catch (Exception e) {
            transactionDate = TRANSACTION_DATE_DUMMY;
        }
        return new FutureTransaction(recurringSchedule, transactionDate);
    }

    public FutureTransaction getLastAvailableTransaction(RecurringSchedule recurringSchedule) {
        int transactionDateInt;
        LocalDate transactionDate = LocalDate.now();
        try (Cursor cursor = db.rawQuery("SELECT MAX(transaction_date) transaction_date FROM recurring_transactions WHERE recurring_schedule_uuid = ?", new String[]{recurringSchedule.recurringScheduleUUID.toString()})) {
            if (cursor.moveToFirst()) {
                transactionDateInt = cursor.getInt(0);
                transactionDate = LocalDate.parse(String.valueOf(transactionDateInt), DateTimeFormatter.ofPattern("yyyyMMdd"));
            }
        } catch (Exception e) {
            transactionDate = TRANSACTION_DATE_DUMMY;
        }
        return new FutureTransaction(recurringSchedule, transactionDate);
    }

    public FutureTransaction getLastTransaction(RecurringSchedule recurringSchedule) {
        int transactionDateInt;
        LocalDate transactionDate = LocalDate.now();
        try (Cursor cursor = db.rawQuery("SELECT MAX(transaction_date) transaction_date FROM recurring_transactions WHERE recurring_schedule_uuid = ?", new String[]{recurringSchedule.recurringScheduleUUID.toString()})) {
            if (cursor.moveToFirst()) {
                transactionDateInt = cursor.getInt(0);
                transactionDate = LocalDate.parse(String.valueOf(transactionDateInt), DateTimeFormatter.ofPattern("yyyyMMdd"));
            }
        } catch (Exception e) {
            transactionDate = TRANSACTION_DATE_DUMMY;
        }
        return new FutureTransaction(recurringSchedule, transactionDate);
    }

    public boolean addFutureTransaction(FutureTransaction futureTransaction) {
        if (isDuplicateFutureTransactionExists(futureTransaction)) {
            return false;
        }
        ContentValues values = ContentValuesFromObject(futureTransaction, INSERTS);
        try {
            db.insertOrThrow("recurring_transactions", null, values);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isDuplicateFutureTransactionExists(FutureTransaction futureTransaction) {
        try (Cursor cursor = db.rawQuery("SELECT * FROM recurring_transactions WHERE recurring_schedule_uuid = ? AND transaction_date = ?",
                new String[]{futureTransaction.recurringScheduleUUID.toString(),
                        String.valueOf(futureTransaction.transactionDate)})) {
            return cursor.getCount() > 0;
        }
    }

    public boolean updateFutureTransaction(FutureTransaction futureTransaction) {
        ContentValues values = ContentValuesFromObject(futureTransaction, UPDATES);
        try {
            db.update("recurring_transactions", values, "transaction_uuid = ?", new String[]{futureTransaction.transactionUUID.toString()});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteFutureTransaction(FutureTransaction futureTransaction) {
        try {
            db.delete("recurring_transactions", "transaction_uuid = ?", new String[]{futureTransaction.transactionUUID.toString()});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteFutureTransactions(RecurringSchedule recurringSchedule) {
        try {
            db.delete("recurring_transactions", "recurring_schedule_uuid = ?", new String[]{recurringSchedule.recurringScheduleUUID.toString()});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteAll() {
        try {
            db.delete("recurring_transactions", null, null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<FutureTransaction> getAllFutureTransactions(TransactionFilter transactionFilters, int pageNumber) {
        List<FutureTransaction> futureTransactions = new ArrayList<>();
        HashMap<String, Object> queryAllData = TransactionFilterUtils.generateTransactionFilterQuery(transactionFilters, recurringSchedule, "", this.application);
        String queryString = Objects.requireNonNull(queryAllData.get("QUERY")).toString();
        if (recurringSchedule != null) {
            queryString = queryString.replace("<<recurring_schedule_uuid>>", recurringSchedule.recurringScheduleUUID.toString());
        }
        //noinspection unchecked
        ArrayList<String> queryParms = (ArrayList<String>) queryAllData.get("VALUES");
        assert queryParms != null;
        int batchSize = AppConstants.BATCH_SIZE;
        String orderByArgs = "transaction_date ASC, create_date ASC LIMIT <<batchSize>> OFFSET <<offset>>"
                .replace("<<batchSize>>", String.valueOf(batchSize))
                .replace("<<offset>>", String.valueOf((pageNumber - 1) * batchSize));
        try (Cursor cursor = db.query("recurring_transactions_view", null, queryString, queryParms.toArray(new String[0]), null, null, orderByArgs)) {
            if (cursor.moveToFirst()) {
                do {
                    FutureTransaction transaction = getRecurringTransactionFromCursor(cursor);
                    if (transaction != null) {
                        futureTransactions.add(transaction);
                    }
                } while (cursor.moveToNext());
            }
        }
        return futureTransactions;
    }

    public FutureTransaction getFutureTransaction(UUID transactionUUID) {
        try (Cursor cursor = db.rawQuery("SELECT * FROM recurring_transactions_view WHERE transaction_uuid = ?", new String[]{transactionUUID.toString()})) {
            if (cursor.moveToFirst()) {
                return getRecurringTransactionFromCursor(cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private ContentValues ContentValuesFromObject(
            FutureTransaction futureTransaction,
            String operationType) {
        checkInterCurrencyTransfers(futureTransaction);
        ContentValues values = new ContentValues();
        values.put("recurring_schedule_uuid", futureTransaction.recurringScheduleUUID.toString());
        values.put("transaction_name", futureTransaction.transactionName);
        values.put("transaction_date", futureTransaction.transactionDate);
        values.put("transaction_type", futureTransaction.transactionType);
        values.put("transfer_ind", futureTransaction.transactionType);
        values.put("category", futureTransaction.category);
        values.put("notes", futureTransaction.notes);
        values.put("amount", Math.round(futureTransaction.amount * 100.0) / 100.0);
        values.put("account_name", futureTransaction.accountName);
        values.put("to_account_name", futureTransaction.toAccountName);
        if (operationType.equals(INSERTS)) {
            values.put("transaction_uuid", futureTransaction.transactionUUID.toString());
            values.put("create_date", futureTransaction.createDateTime);
        }
        values.put("last_update_date", futureTransaction.lastUpdateDateTime);
        return values;
    }

    private void checkInterCurrencyTransfers(FutureTransaction futureTransaction) {
        if (futureTransaction.transferInd.equals("Transfer")) {
            AccountRepository accountRepository = new AccountRepository(this.application);
            Account accountFrom = accountRepository.getAccountByName(futureTransaction.accountName);
            Account accountTo = accountRepository.getAccountByName(futureTransaction.toAccountName);
            if (!(accountFrom.currencyCode.equals(accountTo.currencyCode))) {
                throw new InterCurrencyTransferNotSupported(accountFrom.currencyCode, accountTo.currencyCode);
            }
        }
    }

    private FutureTransaction getRecurringTransactionFromCursor(Cursor cursor) {
        try {
            int uuidIndex = cursor.getColumnIndexOrThrow("transaction_uuid");
            int scheduleUuidIndex = cursor.getColumnIndexOrThrow("recurring_schedule_uuid");
            int nameIndex = cursor.getColumnIndexOrThrow("transaction_name");
            int dateIndex = cursor.getColumnIndexOrThrow("transaction_date");
            int typeIndex = cursor.getColumnIndexOrThrow("transaction_type");
            int categoryIndex = cursor.getColumnIndexOrThrow("category");
            int notesIndex = cursor.getColumnIndexOrThrow("notes");
            int amountIndex = cursor.getColumnIndexOrThrow("amount");
            int accountIndex = cursor.getColumnIndexOrThrow("account_name");
            int toAccountIndex = cursor.getColumnIndexOrThrow("to_account_name");
            int createDateTimeIndex = cursor.getColumnIndexOrThrow("create_date");
            int lastUpdateDateTimeIndex = cursor.getColumnIndexOrThrow("last_update_date");
            int transferIndIndex = cursor.getColumnIndexOrThrow("transfer_ind");
            int currencyCodeIndex = cursor.getColumnIndexOrThrow("currency_code");
            int conversionFactorIndex = cursor.getColumnIndexOrThrow("conversion_factor");
            int primaryCurrencyCodeIndex = cursor.getColumnIndexOrThrow("primary_currency_code");
            String transactionUUIDStr = cursor.getString(uuidIndex);
            UUID transactionUUID = UUID.fromString(transactionUUIDStr);
            UUID scheduleUUID = UUID.fromString(cursor.getString(scheduleUuidIndex));
            String transactionName = cursor.getString(nameIndex);
            int transactionDateInt = cursor.getInt(dateIndex);
            String transactionType = cursor.getString(typeIndex);
            String category = cursor.getString(categoryIndex);
            String notes = cursor.getString(notesIndex);
            double amount = cursor.getDouble(amountIndex);
            String accountName = cursor.getString(accountIndex);
            String toAccountName = cursor.getString(toAccountIndex);
            long createDateTime = cursor.getLong(createDateTimeIndex);
            long lastUpdateDateTime = cursor.getLong(lastUpdateDateTimeIndex);
            String transferInd = cursor.getString(transferIndIndex);
            String currencyCode = cursor.getString(currencyCodeIndex);
            double conversionFactor = cursor.getDouble(conversionFactorIndex);
            String primaryCurrencyCode = cursor.getString(primaryCurrencyCodeIndex);
            return new FutureTransaction(transactionUUID,
                    scheduleUUID,
                    transactionName,
                    transactionDateInt,
                    transactionType,
                    category,
                    notes,
                    amount,
                    accountName,
                    toAccountName,
                    createDateTime,
                    lastUpdateDateTime,
                    transferInd,
                    currencyCode,
                    conversionFactor,
                    primaryCurrencyCode);

        } catch (IllegalArgumentException e) { // Catch column not found
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean insertAllRecurringTransactions
            (RecurringSchedule recurringSchedule, LocalDate referenceDate) {
        try {
            insertAllRecurringTransactions(recurringSchedule, referenceDate, LocalDate.now().plusYears(3));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertAllRecurringTransactions
            (RecurringSchedule recurringSchedule, LocalDate referenceDate, LocalDate endDate) {
        try {
            List<FutureTransaction> futureTransactions =
                    getRecurringTransactionsFromSchedule(recurringSchedule, referenceDate, endDate);
            for (FutureTransaction futureTransaction : futureTransactions) {
                addFutureTransaction(futureTransaction);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<FutureTransaction> getRecurringTransactionsFromSchedule
            (RecurringSchedule recurringSchedule,
             LocalDate referenceDate,
             LocalDate endDate) {
        List<FutureTransaction> futureTransactions = new ArrayList<>();
        FutureTransaction lastFutureTransaction = getLastTransaction(recurringSchedule);
        FutureTransaction lastAvailableFutureTransaction = getLastAvailableTransaction(recurringSchedule);
        LocalDate lastTransactionInsertedDate = transactionRepository.getLastRecurringTransactionInserted(recurringSchedule);
        boolean isTodayInsert = transactionRepository.checkScheduleInsertedForToday(recurringSchedule, LocalDate.now());
        if (lastFutureTransaction.getTransactionLocalDate().isEqual(TRANSACTION_DATE_DUMMY)) {
            if (referenceDate.isEqual(recurringSchedule.getRecurringEndDateLocalDate())
                    || referenceDate.isAfter(recurringSchedule.getRecurringEndDateLocalDate())) {
                return new ArrayList<>();
            }
        }
        if (lastFutureTransaction.getTransactionLocalDate().isEqual(
                recurringSchedule.getRecurringEndDateLocalDate()
        )) {
            return new ArrayList<>();
        }
        if (!isTodayInsert &&
                recurringSchedule.getRecurringStartDateLocalDate().isEqual(LocalDate.now()) &&
                referenceDate.isEqual(LocalDate.now())
        ) {
            futureTransactions.add(new FutureTransaction(recurringSchedule, referenceDate));
        }
        LocalDate referenceEndDate;
        if (endDate.isBefore(recurringSchedule.getRecurringEndDateLocalDate())) {
            referenceEndDate = endDate;
        } else {
            referenceEndDate = recurringSchedule.getRecurringEndDateLocalDate().plusDays(1);
        }
        List<FutureTransaction> futureTransactions1 = referenceDate.datesUntil(referenceEndDate,
                        getPeriodFromSchedule(recurringSchedule))
                .map(date1 -> new FutureTransaction(recurringSchedule, date1))
                .filter(e -> e.getTransactionLocalDate().isAfter(lastAvailableFutureTransaction.getTransactionLocalDate()))
                .filter(e -> e.getTransactionLocalDate().isAfter(lastTransactionInsertedDate))
                .filter(e -> e.getTransactionLocalDate().isAfter(recurringSchedule.getRecurringStartDateLocalDate()))
                .filter(e -> e.getTransactionLocalDate().isBefore(recurringSchedule.getRecurringEndDateLocalDate())
                        || e.getTransactionLocalDate().isEqual(recurringSchedule.getRecurringEndDateLocalDate()))
                .collect(Collectors.toList());
        futureTransactions.addAll(futureTransactions1);
        return futureTransactions;
    }

    private Period getPeriodFromSchedule(RecurringSchedule recurringSchedule) {
        switch (recurringSchedule.recurringScheduleName) {
            case AppConstants.RECURRING_SCHEDULES_DAILY:
                return Period.ofDays(1);
            case AppConstants.RECURRING_SCHEDULES_WEEKLY:
                return Period.ofWeeks(1);
            case AppConstants.RECURRING_SCHEDULES_MONTHLY:
                return Period.ofMonths(1);
            case AppConstants.RECURRING_SCHEDULES_QUARTERLY:
                return Period.ofMonths(3);
            case AppConstants.RECURRING_SCHEDULES_YEARLY:
                return Period.ofYears(1);
            case AppConstants.RECURRING_SCHEDULES_CUSTOM:
                if (recurringSchedule.repeatIntervalDays > 0) {
                    return Period.ofDays(recurringSchedule.repeatIntervalDays);
                }
                break;
        }
        return Period.ofYears(10);
    }

    public List<FutureTransaction> getRecurringTransactionsCurrentDate() {
        try (Cursor cursor = db.rawQuery("SELECT * FROM recurring_transactions_view WHERE transaction_date <= ?", new String[]{LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))})) {
            List<FutureTransaction> futureTransactions = new ArrayList<>();
            while (cursor.moveToNext()) {
                futureTransactions.add(getRecurringTransactionFromCursor(cursor));
            }
            return futureTransactions;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean applyRecurringTransactions() {
        try {
            List<FutureTransaction> futureTransactions = getRecurringTransactionsCurrentDate();
            for (FutureTransaction futureTransaction : futureTransactions) {
                transactionRepository.addTransaction(futureTransaction.getTransaction());
                deleteFutureTransaction(futureTransaction);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void setRecurringSchedule(RecurringSchedule recurringSchedule) {
        this.recurringSchedule = recurringSchedule;
    }

    public void deleteInvalidFutureTxns() {
        try {
            db.delete("recurring_transactions", " NOT EXISTS (SELECT 1 FROM recurring_schedules " +
                            " WHERE recurring_transactions.recurring_schedule_uuid = recurring_schedules.recurring_schedule_uuid)",
                    null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean applyFutureTransaction(FutureTransaction originalTransaction) {
        try {
            originalTransaction.setTransactionLocalDate(LocalDate.now());
            transactionRepository.addTransaction(originalTransaction.getTransaction());
            deleteFutureTransaction(originalTransaction);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

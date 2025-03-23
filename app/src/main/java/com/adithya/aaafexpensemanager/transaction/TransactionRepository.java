package com.adithya.aaafexpensemanager.transaction;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.NonNull;

import com.adithya.aaafexpensemanager.account.Account;
import com.adithya.aaafexpensemanager.account.AccountRepository;
import com.adithya.aaafexpensemanager.recenttrans.RecentTransactionRepository;
import com.adithya.aaafexpensemanager.recurring.RecurringSchedule;
import com.adithya.aaafexpensemanager.transaction.exception.InterCurrencyTransferNotSupported;
import com.adithya.aaafexpensemanager.transaction.exception.InvalidAccountDataException;
import com.adithya.aaafexpensemanager.transactionFilter.TransactionFilter;
import com.adithya.aaafexpensemanager.transactionFilter.TransactionFilterUtils;
import com.adithya.aaafexpensemanager.util.AppConstants;
import com.adithya.aaafexpensemanager.util.DatabaseHelper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/** @noinspection CallToPrintStackTrace, SameParameterValue */
public class TransactionRepository {
    private final SQLiteDatabase db;
    private final AccountRepository accountRepository;
    private final RecentTransactionRepository recentTransactionRepository;
    private static final String INSERTS = "Insert";
    private static final String DELETES = "Delete";
    private static final String UPDATES = "Update";

    public int recordCount = 0;
    /** @noinspection resource*/
    public TransactionRepository(Application application) {
        DatabaseHelper dbHelper = new DatabaseHelper(application);
        db = dbHelper.getWritableDatabase();
        accountRepository = new AccountRepository(application);
        recentTransactionRepository =  new RecentTransactionRepository(application);
    }

    public List<Transaction> getAllTransactions(TransactionFilter transactionFilters, int pageNumber) {
        List<Transaction> transactions = new ArrayList<>();
        HashMap<String, Object> queryAllData = TransactionFilterUtils.generateTransactionFilterQuery(transactionFilters);
        String queryString = Objects.requireNonNull(queryAllData.get("QUERY")).toString();
        //noinspection unchecked
        ArrayList<String> queryParms = (ArrayList<String>) queryAllData.get("VALUES");
        assert queryParms != null;
        int batchSize = AppConstants.BATCH_SIZE;
        String orderByArgs;
        if(pageNumber<0) {
            orderByArgs = "transaction_date DESC, create_date DESC";
        }
        else {
            orderByArgs = "transaction_date DESC, create_date DESC LIMIT <<batchSize>> OFFSET <<offset>>"
                    .replace("<<batchSize>>", String.valueOf(batchSize))
                    .replace("<<offset>>", String.valueOf((pageNumber - 1) * batchSize));
        }
        try (Cursor cursor = db.query("SplitTransfers", null, queryString, queryParms.toArray(new String[0]), null, null, orderByArgs)){
            if (cursor.moveToFirst()) {
                do {
                    Transaction transaction = getTransactionFromCursor(cursor);
                    if (transaction != null) {
                        transactions.add(transaction);
                    }
                } while (cursor.moveToNext());
            }
        }
        return transactions;
    }

    private Transaction getTransactionFromCursor(Cursor cursor) {
        try {
            int uuidIndex = cursor.getColumnIndexOrThrow("transaction_uuid");
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
            int recurringScheduleUUIDIndex = cursor.getColumnIndexOrThrow("recurring_schedule_uuid");
            int currencyCodeIndex = cursor.getColumnIndexOrThrow("currency_code");
            int conversionFactorIndex = cursor.getColumnIndexOrThrow("conversion_factor");
            int primaryCurrencyCodeIndex = cursor.getColumnIndexOrThrow("primary_currency_code");
            String transactionUUIDStr = cursor.getString(uuidIndex);
            UUID transactionUUID = UUID.fromString(transactionUUIDStr);
            String transactionName = cursor.getString(nameIndex);
            int transactionDateInt = cursor.getInt(dateIndex);
            LocalDate transactionDate = convertIntToLocalDate(transactionDateInt);
            String recurringScheduleUUID = cursor.getString(recurringScheduleUUIDIndex);
            if (transactionDate == null) {
                return null;
            }
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
            return new Transaction(transactionUUID, transactionName, transactionDateInt,
                    transactionType, category, notes, amount, accountName, toAccountName,
                    createDateTime, lastUpdateDateTime, transferInd,recurringScheduleUUID,
                    currencyCode,conversionFactor,primaryCurrencyCode);

        } catch (IllegalArgumentException e) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public void addTransaction(Transaction transaction) {
        if (updateAccountBalances(transaction,TransactionRepository.INSERTS)) return;
        try {
            ContentValues values = getContentValues(transaction,TransactionRepository.INSERTS);
            db.insertOrThrow("transactions", null, values);
            recentTransactionRepository.updateRecentTransaction(transaction);
            this.recordCount = this.recordCount + 1;
            if(this.recordCount%100==0){
                Log.d("TransactionRepository",this.recordCount + "records added");
            }
        }
        catch (IndexOutOfBoundsException e){
            throw new InterCurrencyTransferNotSupported(e.getMessage());
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void checkInterCurrencyTransfers(Transaction transaction) {
        if(transaction.transferInd.equals("Transfer")) {
            Account accountFrom = accountRepository.getAccountByName(transaction.accountName);
            Account accountTo = accountRepository.getAccountByName(transaction.toAccountName);
            if(!(accountFrom.currencyCode.equals(accountTo.currencyCode))){
                throw new InterCurrencyTransferNotSupported();
            }
        }
    }

    @NonNull
    private ContentValues getContentValues(Transaction transaction,String operation) {
        checkInterCurrencyTransfers(transaction);
        ContentValues values = new ContentValues();
        if(operation.equals(TransactionRepository.INSERTS)){
            values.put("transaction_uuid", transaction.transactionUUID.toString());
            values.put("create_date", transaction.createDateTime);
        }
        values.put("transaction_name", transaction.transactionName);
        values.put("transaction_date", transaction.transactionDate);
        values.put("transaction_type", transaction.transactionType);
        values.put("transfer_ind", transaction.transactionType);
        values.put("category", transaction.category);
        values.put("notes", transaction.notes);
        values.put("amount", Math.round(transaction.amount*100.0)/100.0);
        values.put("account_name", transaction.accountName);
        values.put("to_account_name", transaction.toAccountName);
        values.put("last_update_date", transaction.lastUpdateDateTime);
        values.put("recurring_schedule_uuid", transaction.recurringScheduleUUID);
        return values;
    }

    public void updateTransaction(Transaction transaction) {
        Transaction beforeTran = this.getTransactionById(transaction.transactionUUID);
        if (updateAccountBalances(beforeTran,TransactionRepository.DELETES)) return;
        if (updateAccountBalances(transaction,TransactionRepository.INSERTS)) return;
        ContentValues values = getContentValues(transaction,TransactionRepository.UPDATES);
        String whereClause = "transaction_uuid = ?";
        String[] whereArgs = new String[]{transaction.transactionUUID.toString()};
        try {
            int updatedCount = db.update("transactions", values, whereClause, whereArgs);
            if(updatedCount<=0) throw new RuntimeException("Transaction not updated "+ transaction);
            recentTransactionRepository.updateRecentTransaction(transaction);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updateTransactionTableOnly(Transaction transaction) {
        ContentValues values = getContentValues(transaction,TransactionRepository.UPDATES);
        String whereClause = "transaction_uuid = ?";
        String[] whereArgs = new String[]{transaction.transactionUUID.toString()};
        try {
            int updatedCount = db.update("transactions", values, whereClause, whereArgs);
            if(updatedCount<=0) throw new RuntimeException("Transaction not updated "+ transaction);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    private LocalDate convertIntToLocalDate(int dateInt) {
        try {
            String dateStr = String.valueOf(dateInt);
            return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyyMMdd"));
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    public Transaction getTransactionById(UUID transactionUUID) {
        try (Cursor cursor = db.rawQuery("SELECT * FROM transactions_view WHERE transaction_uuid = ?", new String[]{transactionUUID.toString()})) {
            if (cursor.moveToFirst()) {
                return getTransactionFromCursor(cursor);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public void deleteTransaction(Transaction transaction) {
        try {
            if (updateAccountBalances(transaction,TransactionRepository.DELETES)) return;
            db.delete("transactions", "transaction_uuid = ?", new String[]{transaction.transactionUUID.toString()});
        } catch (RuntimeException ignored) {

        }
    }
    public void deleteAll(){
        try {
            db.delete("transactions", null, null);
        } catch (RuntimeException ignored) {
        }
    }
    private boolean updateAccountBalances(Transaction transaction,String operation) {
        double modifiedAmount = transaction.amount;
        Transaction transactionActual = transaction;
        String transactionTypeActual = "";
        if(operation.equals(TransactionRepository.INSERTS)){
            modifiedAmount = 1 * modifiedAmount;
            transactionTypeActual = transaction.transactionType;
        } else if (operation.equals(TransactionRepository.DELETES)) {
            modifiedAmount = -1 * modifiedAmount;
            transactionActual = getTransactionById(transaction.transactionUUID);
            transactionTypeActual = transactionActual.transactionType;
        }
        switch (transactionTypeActual) {
            case "Expense": {
                Account account = accountRepository.getAccountByName(transaction.accountName);
                if (account == null) {
                    throw new InvalidAccountDataException(
                            InvalidAccountDataException.INVALID_ACCOUNT_DATA_NULL);
                }
                double newBalance = account.accountBalance - modifiedAmount;
                accountRepository.updateAccountBalance(transaction.accountName, newBalance);
                break;
            }
            case "Income": {
                Account account = accountRepository.getAccountByName(transaction.accountName);
                if (account == null) {
                    throw new InvalidAccountDataException(
                            InvalidAccountDataException.INVALID_ACCOUNT_DATA_NULL);
                }
                double newBalance = account.accountBalance + modifiedAmount;
                accountRepository.updateAccountBalance(transaction.accountName, newBalance);
                break;
            }
            case "Transfer":
                Account accountFrom = accountRepository.getAccountByName(transactionActual.accountName);
                Account accountTo = accountRepository.getAccountByName(transactionActual.toAccountName);
                if ((accountFrom == null) || (accountTo == null)) {
                    throw new InvalidAccountDataException(
                            InvalidAccountDataException.INVALID_ACCOUNT_DATA_NULL);
                }
                double newBalanceFrom = accountFrom.accountBalance - modifiedAmount;
                accountRepository.updateAccountBalance(transactionActual.accountName, newBalanceFrom);
                double newBalanceTo = accountTo.accountBalance + modifiedAmount;
                accountRepository.updateAccountBalance(transactionActual.toAccountName, newBalanceTo);
                break;
        }
        return false;
    }
    public void addTransactionsRaw(List<Transaction> transactions) {
        try {
            db.beginTransaction();
            for(Transaction transaction : transactions){
                try {
                    ContentValues values = getContentValues(transaction, TransactionRepository.INSERTS);
                    db.insertOrThrow("transactions", null, values);
                    this.recordCount = this.recordCount + 1;
                    if (this.recordCount % 100 == 0) {
                        Log.d("TransactionRepository", this.recordCount + "records added");
                    }
                }
                catch (InterCurrencyTransferNotSupported e){
                    e.printStackTrace();
                }
            }
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            db.endTransaction();
        }
    }
    public HashMap<String,Double> getAccountBalances() {
        HashMap<String,Double> balances = new HashMap<>();
        try (Cursor cursor = db.rawQuery("SELECT account_name," +
                "SUM(CASE transaction_type " +
                "WHEN 'Expense' THEN -1*amount " +
                "WHEN 'Income'  THEN 1*amount " +
                "END) as amount from SplitTransfers " +
                "GROUP BY account_name", null)){
            if (cursor.moveToFirst()) {
                do {
                    balances.put(
                            cursor.getString(0),
                            cursor.getDouble(1));
                } while (cursor.moveToNext());
            }
        }
        return balances;
    }

    public void deleteAllFilteredTransactions(TransactionFilter transactionFilters) {
        HashMap<String, Object> queryAllData = TransactionFilterUtils.generateTransactionFilterQuery(transactionFilters);
        String queryString = Objects.requireNonNull(queryAllData.get("QUERY")).toString();
        //noinspection unchecked
        ArrayList<String> queryParms = (ArrayList<String>) queryAllData.get("VALUES");
        assert queryParms != null;
        try {
            db.delete("transactions", queryString, queryParms.toArray(new String[0]));
        } catch (RuntimeException ignored) {
        }
        accountRepository.updateAccountBalances(this);
        recentTransactionRepository.deleteAll();
        recentTransactionRepository.updateAllRecentTransactions();
    }

    public boolean checkScheduleInsertedForToday(RecurringSchedule recurringSchedule,LocalDate localDate) {
        try (Cursor cursor = db.rawQuery("SELECT COUNT(*) count_1 FROM transactions " +
                " WHERE recurring_schedule_uuid = ? " +
                " AND transaction_date = ?",new String[]{recurringSchedule.recurringScheduleUUID.toString()
        ,localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"))})) {
            int countData;
            if (cursor.moveToFirst()) {
                countData = cursor.getInt(0);
                if(countData>0){
                    return true;
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public LocalDate getLastRecurringTransactionInserted(RecurringSchedule recurringSchedule) {
        int transactionDateInt;
        LocalDate transactionDate = LocalDate.now();
        try (Cursor cursor = db.rawQuery("SELECT MAX(transaction_date) transaction_date FROM transactions WHERE recurring_schedule_uuid = ?", new String[]{recurringSchedule.recurringScheduleUUID.toString()})) {
            if (cursor.moveToFirst()) {
                transactionDateInt = cursor.getInt(0);
                transactionDate = LocalDate.parse(String.valueOf(transactionDateInt), DateTimeFormatter.ofPattern("yyyyMMdd"));
            }
        }
        catch (Exception e){
            transactionDate = AppConstants.TRANSACTION_DATE_DUMMY;
        }
        return transactionDate;
    }

    public void updateTransactionField(Transaction transaction,
                                       String fieldName,
                                       String updatedValue) {
        try {
            var fieldContentValues = new ContentValues();
            fieldContentValues.put(fieldName, updatedValue);
            db.update("transactions", fieldContentValues, "transaction_uuid = ?", new String[]{transaction.transactionUUID.toString()});
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updateTransactionFields(List<Transaction> transactions,
                                        String fieldName,
                                        String selectedText) {
        if(fieldName.equals("account_name")){
            for (Transaction transaction : transactions) {
                if(transaction.transferInd.equals("Transfer")){
                    if(!transaction.transactionType.equals("Income")){
                        updateTransactionField(transaction,"account_name",selectedText);
                    }
                }
                else{
                    updateTransactionField(transaction,"account_name",selectedText);
                }
            }
        }
        else if(fieldName.equals("to_account_name")){
            for (Transaction transaction : transactions) {
                if(transaction.transferInd.equals("Transfer")){
                    if(!transaction.transactionType.equals("Expense")){
                        updateTransactionField(transaction,"to_account_name",selectedText);
                    }
                }
            }
        }
        else{
            for (Transaction transaction : transactions) {
                updateTransactionField(transaction,fieldName,selectedText);
            }
        }
        recentTransactionRepository.deleteAll();
        recentTransactionRepository.updateAllRecentTransactions();
        accountRepository.updateAccountBalances(this);
    }
}
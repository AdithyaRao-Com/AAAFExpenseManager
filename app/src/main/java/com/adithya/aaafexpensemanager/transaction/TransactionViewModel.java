package com.adithya.aaafexpensemanager.transaction;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.adithya.aaafexpensemanager.transactionFilter.TransactionFilter;

import java.util.List;
import java.util.UUID;

public class TransactionViewModel extends AndroidViewModel {
    private final TransactionRepository repository;
    private final MutableLiveData<List<Transaction>> transactions = new MutableLiveData<>();
    private TransactionFilter transactionFilter;

    public TransactionViewModel(Application application) {
        super(application);
        repository = new TransactionRepository(application);
    }

    public void addTransaction(Transaction transaction) {
        repository.addTransaction(transaction);
        Log.d("TransactionViewModel", "Transaction added. Reloading transactions.");
    }

    public LiveData<List<Transaction>> getTransactions(TransactionFilter transactionFilter, int pageNumber) {
        this.transactionFilter = transactionFilter;
        loadTransactions(pageNumber);
        return transactions;
    }

    private void loadTransactions(int pageNumber) {
        if (transactionFilter == null) {
            transactionFilter = new TransactionFilter();
        }
        List<Transaction> transactionList = repository.getAllTransactions(transactionFilter, pageNumber);
        transactions.setValue(transactionList);
        Log.d("TransactionViewModel", "Transactions loaded. Size: " + (transactionList == null ? 0 : transactionList.size()));
    }

    public void updateTransaction(Transaction transaction) {
        repository.updateTransaction(transaction);
        Log.d("TransactionViewModel", "Transaction updated. Reloading transactions.");
    }

    public Transaction getTransactionById(UUID transactionUUID) {
        return repository.getTransactionById(transactionUUID);
    }

    public void deleteTransaction(Transaction transaction) {
        repository.deleteTransaction(transaction);
    }

    public void deleteAllFilteredTransactions(TransactionFilter transactionFilter) {
        repository.deleteAllFilteredTransactions(transactionFilter);
    }

    public void copyTransaction(@NonNull Transaction transaction) {
        Transaction originalTransaction = repository.getTransactionById(transaction.transactionUUID);
        Transaction transactionClone = new Transaction(originalTransaction);
        repository.addTransaction(transactionClone);
    }

    public void updateTransactionFields(List<Transaction> transactions,
                                        String fieldName,
                                        String selectedText) {
        repository.updateTransactionFields(transactions, fieldName, selectedText);
    }
}
package com.adithya.aaafexpensemanager.futureTransaction;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.adithya.aaafexpensemanager.recurring.RecurringSchedule;
import com.adithya.aaafexpensemanager.transactionFilter.TransactionFilter;

import java.util.List;
import java.util.UUID;

/**
 * @noinspection UnusedReturnValue
 */
public class FutureTransactionViewModel extends AndroidViewModel {
    private final FutureTransactionRepository repository;
    private final MutableLiveData<List<FutureTransaction>> futureTransactions = new MutableLiveData<>();
    private TransactionFilter transactionFilter;

    public FutureTransactionViewModel(Application application) {
        super(application);
        repository = new FutureTransactionRepository(application);
    }

    public FutureTransaction getNextTransaction(RecurringSchedule recurringSchedule) {
        return repository.getNextTransaction(recurringSchedule);
    }

    public boolean updateFutureTransaction(FutureTransaction futureTransaction) {
        return repository.updateFutureTransaction(futureTransaction);
    }

    public boolean deleteFutureTransaction(FutureTransaction futureTransaction) {
        return repository.deleteFutureTransaction(futureTransaction);
    }

    public boolean deleteFutureTransactions(RecurringSchedule recurringSchedule) {
        return repository.deleteFutureTransactions(recurringSchedule);
    }

    public LiveData<List<FutureTransaction>> getFutureTransactions(TransactionFilter transactionFilter, int pageNumber) {
        {
            this.transactionFilter = transactionFilter;
            loadTransactions(pageNumber);
            return futureTransactions;
        }
    }

    private void loadTransactions(int pageNumber) {
        if (transactionFilter == null) {
            transactionFilter = new TransactionFilter();
        }
        futureTransactions.setValue(repository.getAllFutureTransactions(transactionFilter, pageNumber));
    }

    public FutureTransaction getFutureTransaction(UUID transactionUUID) {
        return repository.getFutureTransaction(transactionUUID);
    }

    public void setRecurringSchedule(RecurringSchedule recurringSchedule) {
        repository.setRecurringSchedule(recurringSchedule);
    }

    public boolean applyFutureTransaction(FutureTransaction originalTransaction) {
        return repository.applyFutureTransaction(originalTransaction);
    }
}
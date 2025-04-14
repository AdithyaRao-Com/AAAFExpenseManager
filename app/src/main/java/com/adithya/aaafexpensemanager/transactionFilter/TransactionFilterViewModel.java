package com.adithya.aaafexpensemanager.transactionFilter;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class TransactionFilterViewModel extends AndroidViewModel {
    private final TransactionFilterRepository repository;
    private final LiveData<List<TransactionFilter>> transactionFilters = new MutableLiveData<>();

    public TransactionFilterViewModel(@NonNull Application application) {
        super(application);
        this.repository = new TransactionFilterRepository(application);
        loadTransactionFilters();
    }

    private void loadTransactionFilters() {
        List<TransactionFilter> transactionFilters = repository.getAllTransactionFilters();
        ((MutableLiveData<List<TransactionFilter>>) this.transactionFilters).setValue(transactionFilters);
    }

    public LiveData<List<TransactionFilter>> getTransactionFilters() {
        return transactionFilters;
    }

    public void addTransactionFilter(TransactionFilter transactionFilter, TransactionFilter previousTransactionFilter) {
        repository.addTransactionFilter(transactionFilter, previousTransactionFilter);
        loadTransactionFilters();
    }

    public void deleteTransactionFilter(TransactionFilter transactionFilter) {
        repository.deleteTransactionFilter(transactionFilter);
        loadTransactionFilters();
    }
}

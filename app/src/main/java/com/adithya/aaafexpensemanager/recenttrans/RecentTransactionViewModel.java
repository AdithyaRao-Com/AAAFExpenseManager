package com.adithya.aaafexpensemanager.recenttrans;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class RecentTransactionViewModel extends AndroidViewModel {
    private final RecentTransactionRepository repository;
    private final MutableLiveData<List<RecentTransaction>> recentTransactions = new MutableLiveData<>();
    private final LiveData<List<RecentTransaction>> recentTransactionsLiveData = recentTransactions;

    public RecentTransactionViewModel(Application application) {
        super(application);
        repository = new RecentTransactionRepository(application);
        loadTransactions();
    }

    public void updateRecentTransaction(RecentTransaction transaction) {
        repository.updateRecentTransaction(transaction);
        loadTransactions();
        Log.d("RecentTransactionViewModel", "Recent transaction updated. Reloading transactions.");
    }

    public LiveData<List<RecentTransaction>> getRecentTransactions() {
        loadTransactions();
        return recentTransactionsLiveData;
    }

    private void loadTransactions() {
        List<RecentTransaction> transactionList = repository.getAllRecentTransactions();
        recentTransactions.setValue(transactionList);
        Log.d("RecentTransactionViewModel", "Transactions loaded. Size: " + (transactionList == null ? 0 : transactionList.size()));
    }

    public RecentTransaction getTransactionByName(String selectedRecentTransString) {
        return repository.getRecentTransactionByName(selectedRecentTransString);
    }
}
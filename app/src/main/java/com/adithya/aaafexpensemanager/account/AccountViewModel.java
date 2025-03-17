package com.adithya.aaafexpensemanager.account;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.adithya.aaafexpensemanager.transaction.Transaction;
import com.adithya.aaafexpensemanager.transaction.TransactionRepository;

import java.util.List;

public class AccountViewModel extends AndroidViewModel { // Extend AndroidViewModel

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final MutableLiveData<List<Account>> accounts = new MutableLiveData<>();

    public AccountViewModel(Application application) { // Constructor takes Application
        super(application); // Call super constructor
        accountRepository = new AccountRepository(application); // Pass application context
        transactionRepository = new TransactionRepository(application);
        loadAccountNames();
    }
    public Account getAccountByName(String accountName) {
        return accountRepository.getAccountByName(accountName);
    }

    public void updateAccount(Account account) {
        accountRepository.updateAccount(account);
    }
    public void deleteAccount(String accountName) {
        accountRepository.deleteAccount(accountName);
        loadAccountNames(); // Refresh the list after deleting
    }

    public void createAccount(Account account) {
        accountRepository.createAccount(account);
    }

    public LiveData<List<Account>> getAccounts() {
        return accounts;
    }

    public void filterAccounts(String searchText) {
        List<Account> filteredAccounts = accountRepository.filterAccounts(searchText); // From repository
        accounts.setValue(filteredAccounts);
    }

    public void loadAccountNames() {
        List<Account> accounts = accountRepository.getAccounts(); // From repository
        this.accounts.setValue(accounts);
    }
    public void addTransaction(Transaction transaction) {
        // Use your TransactionRepository to insert the transaction into the database
        transactionRepository.addTransaction(transaction);
    }
}
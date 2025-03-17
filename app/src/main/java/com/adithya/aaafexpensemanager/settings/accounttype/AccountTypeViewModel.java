package com.adithya.aaafexpensemanager.settings.accounttype;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

import java.util.ArrayList;
import java.util.List;

public class AccountTypeViewModel extends AndroidViewModel {
    private List<AccountType> accountTypes = new ArrayList<>();
    private final AccountTypeRepository accountTypeRepository;

    public AccountTypeViewModel(Application application) {
        super(application);
        accountTypeRepository = new AccountTypeRepository(application);
        loadAccountTypes();
    }

    private void loadAccountTypes() {
        accountTypes = accountTypeRepository.getAccountTypes();
    }

    public List<AccountType> getAccountTypes() {
        return accountTypes;
    }
    public List<AccountType> filterAccountTypes(String searchText) {
        return accountTypeRepository.filterAccountTypes(searchText);
    }
    public void createAccountType(AccountType accountType) {
        if(accountTypeRepository.getAccountTypeFromName(accountType.accountType)==null){
            accountTypeRepository.createAccountType(accountType);
        }
        else{
            accountTypeRepository.updateAccountType(accountType);
        }
        loadAccountTypes();
    }
    public void deleteAccountType(String accountType) {
        accountTypeRepository.deleteAccountType(accountType);
        loadAccountTypes();
    }
}
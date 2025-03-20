package com.adithya.aaafexpensemanager.settings.currency;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.adithya.aaafexpensemanager.settings.currency.exception.CurrencyExistsException;

import java.util.List;

public class CurrencyViewModel extends AndroidViewModel {
    // TODO - Create fragment for display currencies
    // TODO - Create fragment for adding currencies
    // TODO - Create fragment for changing primary currency
    private final CurrencyRepository repository;
    private final LiveData<List<Currency>> currencies;

    public CurrencyViewModel(Application application) {
        super(application);
        repository = new CurrencyRepository(application);
        currencies = new MutableLiveData<>();
        loadCurrencies();
    }

    public LiveData<List<Currency>> getCurrencies() {
        return currencies;
    }

    public void addCurrency(Currency currency) throws CurrencyExistsException {
        repository.addCurrency(currency);
        loadCurrencies();
    }

    public void deleteCurrency(Currency currency) {
        repository.deleteCurrency(currency);
        loadCurrencies(); // Reload after deleting
    }

    private void loadCurrencies() {
        List<Currency> currencyList = repository.getAllCurrencies();
        ((MutableLiveData<List<Currency>>) currencies).setValue(currencyList);
    }
    public void filterCurrencies(String searchText) {
        List<Currency> filteredCurrencies = repository.filterCurrencies(searchText);
        ((MutableLiveData<List<Currency>>) currencies).setValue(filteredCurrencies);
    }
    public void setPrimaryCurrency(String currencyName) {
        repository.setPrimaryAccount(currencyName);
        loadCurrencies();
    }
}
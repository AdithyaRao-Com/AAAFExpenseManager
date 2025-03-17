package com.adithya.aaafexpensemanager.recurring;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.adithya.aaafexpensemanager.transactionFilter.TransactionFilter;

import java.util.List;
import java.util.UUID;

public class RecurringViewModel extends AndroidViewModel {
    private final RecurringRepository repository;
    private final MutableLiveData<List<RecurringSchedule>> recurringSchedules = new MutableLiveData<>();
    private TransactionFilter transactionFilter;

    public RecurringViewModel(Application application) {
        super(application);
        repository = new RecurringRepository(application);
    }

    public boolean addRecurringSchedule(RecurringSchedule transaction) {
        return repository.addRecurringSchedule(transaction);
    }

    public LiveData<List<RecurringSchedule>> getRecurringSchedules(TransactionFilter transactionFilter,int pageNumber) {
        this.transactionFilter = transactionFilter;
        loadTransactions(pageNumber);
        return recurringSchedules;
    }

    private void loadTransactions(int pageNumber) {
        if(transactionFilter==null){
            transactionFilter =  new TransactionFilter();
        }
        List<RecurringSchedule> recurringScheduleList =
                repository.getAllRecurringSchedules(transactionFilter,pageNumber);
        recurringSchedules.setValue(recurringScheduleList);
        Log.d("RecurringViewModel", "Recurring schedules loaded. Size: " + (recurringScheduleList == null ? 0 : recurringScheduleList.size()));
    }
    public void updateRecurringSchedule(RecurringSchedule recurringSchedule) {
        repository.updateRecurringSchedule(recurringSchedule);
        Log.d("RecurringViewModel", "Recurring schedule updated. Reloading data.");
    }
    public RecurringSchedule getRecurringScheduleById(UUID recurringScheduleUUID) {
        return repository.getRecurringScheduleById(recurringScheduleUUID);
    }
    public void deleteRecurringSchedule(RecurringSchedule recurringSchedule) {
        repository.deleteRecurringSchedule(recurringSchedule);
    }
}
package com.adithya.aaafexpensemanager.util;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.adithya.aaafexpensemanager.importdata.ImportCSVParser;
import com.adithya.aaafexpensemanager.recurring.RecurringRepository;
import com.adithya.aaafexpensemanager.recurring.RecurringSchedule;
import com.adithya.aaafexpensemanager.futureTransaction.FutureTransactionRepository;
import com.adithya.aaafexpensemanager.transaction.Transaction;
import com.adithya.aaafexpensemanager.transaction.TransactionRepository;
import com.adithya.aaafexpensemanager.transactionFilter.TransactionFilter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class SetupTestData {
    private final RecurringRepository recurringRepository;
    private final TransactionRepository transactionRepository;
    private final FutureTransactionRepository futureTransactionRepository;
    private final Application application;
    public  SetupTestData(Application application){
        super();
        this.application = application;
        recurringRepository = new RecurringRepository(application);
        transactionRepository = new TransactionRepository(application);
        futureTransactionRepository =new FutureTransactionRepository(application);
    }
    public void setUpTestData(){
        SetupTestData.updateIsSetupData(application,true);
        if(SetupTestData.getIsSetupData(application)) {
            Log.d("SetupTestData", "Running setup data again");
            recurringRepository.deleteAll();
            futureTransactionRepository.deleteAll();
            ImportCSVParser.parseTransactions(application);
            SetupTestData.updateIsSetupData(application,false);
            setupDummyRecurringSchedulesAndTransactions();
        }
    }

    private void setupDummyRecurringSchedulesAndTransactions() {
        List<Transaction> trans = transactionRepository.getAllTransactions(new TransactionFilter(),1)
                .stream()
                .limit(10)
                .collect(Collectors.toList());
        List<RecurringSchedule> recurringSchedules = new ArrayList<>();
        for(Transaction tran:trans){
            RecurringSchedule recurringSchedule = new RecurringSchedule(tran);
            Random rand = new Random();
            int randomIndex = rand.nextInt(AppConstants.RECURRING_SCHEDULES.size());
            String scheduleName = AppConstants.RECURRING_SCHEDULES.get(randomIndex);
            recurringSchedule.recurringScheduleName = scheduleName;
            recurringSchedule.recurringStartDate = Integer.parseInt(LocalDate.now().plusDays(rand.nextInt(1)).format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            if(scheduleName.equals("Custom")){
                recurringSchedule.repeatIntervalDays = rand.nextInt(10) + 1;
            }
            recurringSchedules.add(recurringSchedule);
        }
        recurringRepository.addRecurringSchedules(recurringSchedules);
    }

    public static void updateIsSetupData(Application application, boolean isSetUpData){
        SharedPreferences sharedPreferences = application.getSharedPreferences("Test_Data_Prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isSetUpData", isSetUpData);
        editor.apply();
    }
    public static boolean getIsSetupData(Application application){
        SharedPreferences sharedPreferences = application.getSharedPreferences("Test_Data_Prefs", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("isSetUpData",false);
    }
}

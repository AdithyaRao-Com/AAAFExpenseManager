package com.adithya.aaafexpensemanager.settings.importExportCSV;

import android.app.Application;
import android.content.Context;
import android.net.Uri;

import com.adithya.aaafexpensemanager.account.AccountRepository;
import com.adithya.aaafexpensemanager.recenttrans.RecentTransactionRepository;
import com.adithya.aaafexpensemanager.settings.accounttype.AccountTypeRepository;
import com.adithya.aaafexpensemanager.settings.category.Category;
import com.adithya.aaafexpensemanager.settings.category.CategoryRepository;
import com.adithya.aaafexpensemanager.settings.currency.Currency;
import com.adithya.aaafexpensemanager.settings.currency.CurrencyRepository;
import com.adithya.aaafexpensemanager.settings.importExportCSV.exception.CSVVersionNotOneException;
import com.adithya.aaafexpensemanager.transaction.Transaction;
import com.adithya.aaafexpensemanager.transaction.TransactionRepository;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @noinspection CallToPrintStackTrace
 */
public class ImportCSVParser {
    public static void parseTransactions(Context context,Uri fileUri){
        int fileVersionType = 1;
        try{
            parseTransactionsV1(context, fileUri);
        }
        catch (CSVVersionNotOneException e){
            fileVersionType = 2;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        try {
            if (fileVersionType == 2) {
                parseTransactionsV2(context, fileUri);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void parseTransactionsV1(Context context,
                                         Uri fileUri) {
        CategoryRepository categoryRepository = new CategoryRepository((Application) context.getApplicationContext());
        AccountRepository accountRepository = new AccountRepository((Application) context.getApplicationContext());
        TransactionRepository transactionRepository = new TransactionRepository((Application) context.getApplicationContext());
        RecentTransactionRepository recentTransactionRepository = new RecentTransactionRepository((Application) context.getApplicationContext());
        AccountTypeRepository accountTypeRepository = new AccountTypeRepository((Application) context.getApplicationContext());
        CurrencyRepository currencyRepository = new CurrencyRepository((Application) context.getApplicationContext());
        cleanUpExistingData(accountRepository, categoryRepository, transactionRepository, recentTransactionRepository);
        setupPrimaryCurrency(currencyRepository);
        String defaultCurrency = currencyRepository.getPrimaryCurrency();
        transactionRepository.recordCount = 0;
        //noinspection deprecation
        try (InputStream inputStream = context.getContentResolver().openInputStream(fileUri);
             InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             CSVParser csvParser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader)) {
            List<String> headersList = csvParser.getHeaderNames();
            if(!headersList.get(0).equals("Type")){
                throw new CSVVersionNotOneException();
            }
            List<CSVRecord> records = csvParser.getRecords();
            records.stream()
                    .map(value -> new ImportExportCSVRecord(value, ImportExportCSVRecord.CSV_VERSION.V1))
                    .map(importRecord -> importRecord.toAccount(defaultCurrency))
                    .distinct()
                    .forEach(accountRepository::createAccount);
            Set<Category> uniqueValues = new HashSet<>();
            for (CSVRecord record : records) {
                ImportExportCSVRecord importExportCSVRecord = new ImportExportCSVRecord(record, ImportExportCSVRecord.CSV_VERSION.V1);
                Category category = importExportCSVRecord.toCategory();
                if (uniqueValues.add(category)) {
                    try {
                        categoryRepository.addCategory(category);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            List<Transaction> transactions = records.stream()
                    .map(value -> new ImportExportCSVRecord(value, ImportExportCSVRecord.CSV_VERSION.V1))
                    .map(ImportExportCSVRecord::toTransaction)
                    .collect(Collectors.toList());
            transactionRepository.addTransactionsRaw(transactions);
            accountRepository.updateAccountBalances(transactionRepository);
            recentTransactionRepository.updateAllRecentTransactions();
            accountTypeRepository.insertDefaultAccountTypes();
        }
        catch (CSVVersionNotOneException e) {
            throw new CSVVersionNotOneException(e);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void parseTransactionsV2(Context context,
                                           Uri fileUri) {
        // TODO - Version 2 needs to be recoded. The data format needs to be established
        CategoryRepository categoryRepository = new CategoryRepository((Application) context.getApplicationContext());
        AccountRepository accountRepository = new AccountRepository((Application) context.getApplicationContext());
        TransactionRepository transactionRepository = new TransactionRepository((Application) context.getApplicationContext());
        RecentTransactionRepository recentTransactionRepository = new RecentTransactionRepository((Application) context.getApplicationContext());
        AccountTypeRepository accountTypeRepository = new AccountTypeRepository((Application) context.getApplicationContext());
        CurrencyRepository currencyRepository = new CurrencyRepository((Application) context.getApplicationContext());
        cleanUpExistingData(accountRepository, categoryRepository, transactionRepository, recentTransactionRepository);
        setupPrimaryCurrency(currencyRepository);
        String defaultCurrency = currencyRepository.getPrimaryCurrency();
        transactionRepository.recordCount = 0;
        //noinspection deprecation
        try (InputStream inputStream = context.getContentResolver().openInputStream(fileUri);
             InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             CSVParser csvParser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader)) {
            List<String> headersList = csvParser.getHeaderNames();
            List<CSVRecord> records = csvParser.getRecords();
            records.stream()
                    .map(value -> new ImportExportCSVRecord(value, ImportExportCSVRecord.CSV_VERSION.V2))
                    .map(importRecord -> importRecord.toAccount(defaultCurrency))
                    .distinct()
                    .forEach(accountRepository::createAccount);
            Set<Category> uniqueValues = new HashSet<>();
            for (CSVRecord record : records) {
                ImportExportCSVRecord importExportCSVRecord = new ImportExportCSVRecord(record, ImportExportCSVRecord.CSV_VERSION.V2);
                Category category = importExportCSVRecord.toCategory();
                if (uniqueValues.add(category)) {
                    try {
                        categoryRepository.addCategory(category);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            List<Transaction> transactions = records.stream()
                    .map(value -> new ImportExportCSVRecord(value, ImportExportCSVRecord.CSV_VERSION.V2))
                    .map(ImportExportCSVRecord::toTransaction)
                    .collect(Collectors.toList());
            transactionRepository.addTransactionsRaw(transactions);
            accountRepository.updateAccountBalances(transactionRepository);
            recentTransactionRepository.updateAllRecentTransactions();
            accountTypeRepository.insertDefaultAccountTypes();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setupPrimaryCurrency(CurrencyRepository currencyRepository) {
        if (!currencyRepository.checkPrimaryCurrencyExists()) {
            try {
                currencyRepository.addCurrency(new Currency("INR", 1.0d));
                currencyRepository.setPrimaryCurrency("INR");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void cleanUpExistingData(AccountRepository accountRepository, CategoryRepository categoryRepository, TransactionRepository transactionRepository, RecentTransactionRepository recentTransactionRepository) {
        accountRepository.deleteAll();
        categoryRepository.deleteAll();
        transactionRepository.deleteAll();
        recentTransactionRepository.deleteAll();
    }
}
package com.adithya.aaafexpensemanager.importdata;

import android.app.Application;
import android.content.Context;
import android.net.Uri;

import com.adithya.aaafexpensemanager.account.AccountRepository;
import com.adithya.aaafexpensemanager.settings.category.Category;
import com.adithya.aaafexpensemanager.settings.category.CategoryRepository;
import com.adithya.aaafexpensemanager.recenttrans.RecentTransactionRepository;
import com.adithya.aaafexpensemanager.settings.accounttype.AccountTypeRepository;
import com.adithya.aaafexpensemanager.settings.currency.Currency;
import com.adithya.aaafexpensemanager.settings.currency.CurrencyRepository;
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

/** @noinspection CallToPrintStackTrace*/
public class ImportCSVParser {
    public static void parseTransactions(Context context,
                                         Uri fileUri) {
        CategoryRepository categoryRepository = new CategoryRepository((Application) context.getApplicationContext());
        AccountRepository accountRepository = new AccountRepository((Application) context.getApplicationContext());
        TransactionRepository transactionRepository = new TransactionRepository((Application) context.getApplicationContext());
        RecentTransactionRepository recentTransactionRepository = new RecentTransactionRepository((Application) context.getApplicationContext());
        AccountTypeRepository accountTypeRepository = new AccountTypeRepository((Application) context.getApplicationContext());
        CurrencyRepository currencyRepository = new CurrencyRepository((Application) context.getApplicationContext());
        accountRepository.deleteAll();
        categoryRepository.deleteAll();
        transactionRepository.deleteAll();
        recentTransactionRepository.deleteAll();
        if(!currencyRepository.checkPrimaryCurrencyExists()){
            try{
                currencyRepository.addCurrency(new Currency("INR",1.0d));
                currencyRepository.setPrimaryCurrency("INR");
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        String defaultCurrency = currencyRepository.getPrimaryCurrency();
        transactionRepository.recordCount = 0;
        //noinspection deprecation
        try(InputStream inputStream = context.getContentResolver().openInputStream(fileUri);
            InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            CSVParser csvParser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader)){
            List<CSVRecord> records = csvParser.getRecords();
            records.stream()
                    .map(ImportDataRecord::new)
                    .map(importRecord -> importRecord.toAccount(defaultCurrency))
                    .distinct()
                    .forEach(accountRepository::createAccount);
            Set<Category> uniqueValues = new HashSet<>();
            for (CSVRecord record : records) {
                ImportDataRecord importDataRecord = new ImportDataRecord(record);
                Category category = importDataRecord.toCategory();
                if (uniqueValues.add(category)) {
                    try {
                        categoryRepository.addCategory(category);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            List<Transaction> transactions = records.stream()
                    .map(ImportDataRecord::new)
                    .map(ImportDataRecord::toTransaction)
                    .collect(Collectors.toList());
            transactionRepository.addTransactionsRaw(transactions);
            accountRepository.updateAccountBalances(transactionRepository);
            recentTransactionRepository.updateAllRecentTransactions();
            accountTypeRepository.insertDefaultAccountTypes();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
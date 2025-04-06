package com.adithya.aaafexpensemanager.settings.importExportSchedules;

import android.app.Application;
import android.content.Context;
import android.net.Uri;

import com.adithya.aaafexpensemanager.account.AccountRepository;
import com.adithya.aaafexpensemanager.recurring.RecurringRepository;
import com.adithya.aaafexpensemanager.recurring.RecurringSchedule;
import com.adithya.aaafexpensemanager.settings.accounttype.AccountTypeRepository;
import com.adithya.aaafexpensemanager.settings.category.CategoryRepository;
import com.adithya.aaafexpensemanager.settings.currency.Currency;
import com.adithya.aaafexpensemanager.settings.currency.CurrencyRepository;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @noinspection CallToPrintStackTrace
 */
public class ImportScheduleCSVParser {
    public static void parseTransactions(Context context,
                                         Uri fileUri) {
        CategoryRepository categoryRepository = new CategoryRepository((Application) context.getApplicationContext());
        AccountRepository accountRepository = new AccountRepository((Application) context.getApplicationContext());
        RecurringRepository recurringRepository = new RecurringRepository((Application) context.getApplicationContext());
        AccountTypeRepository accountTypeRepository = new AccountTypeRepository((Application) context.getApplicationContext());
        CurrencyRepository currencyRepository = new CurrencyRepository((Application) context.getApplicationContext());
        cleanUpExistingData(recurringRepository);
        setupPrimaryCurrency(currencyRepository);
        String defaultCurrency = currencyRepository.getPrimaryCurrency();
        //noinspection deprecation
        try (InputStream inputStream = context.getContentResolver().openInputStream(fileUri);
             InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             CSVParser csvParser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader)) {
            List<CSVRecord> records = csvParser.getRecords();
            List<RecurringSchedule> transactions = records.stream()
                    .map(ImportExportScheduleCSVRecord::new)
                    .map(ImportExportScheduleCSVRecord::toRecurringSchedule)
                    .collect(Collectors.toList());
            for (RecurringSchedule transaction : transactions) {
                transaction.currencyCode = defaultCurrency;
                transaction.primaryCurrencyCode = defaultCurrency;
                recurringRepository.addRecurringSchedule(transaction);
            }
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

    private static void cleanUpExistingData(RecurringRepository recurringRepository) {
        recurringRepository.deleteAll();
    }
}
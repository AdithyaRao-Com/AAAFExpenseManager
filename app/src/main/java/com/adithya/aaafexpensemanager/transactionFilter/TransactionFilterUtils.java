package com.adithya.aaafexpensemanager.transactionFilter;

import android.app.Application;

import androidx.annotation.NonNull;

import com.adithya.aaafexpensemanager.account.AccountRepository;
import com.adithya.aaafexpensemanager.recurring.RecurringSchedule;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class TransactionFilterUtils {
    public static void addTaggedAccountsToFilter(TransactionFilter transactionFilters, Application application) {
        AccountRepository accountRepository = new AccountRepository(application);
        try {
            if (!transactionFilters.accountTags.isEmpty()) {
                List<String> taggedAccounts = accountRepository.getTaggedAccountNames(transactionFilters.accountTags);
                transactionFilters.addAccountNames(taggedAccounts);
            }
        } catch (Exception ignored) {
        }
    }

    public static HashMap<String, Object> generateTransactionFilterQuery(TransactionFilter transactionFilter, Application application) {
        return generateTransactionFilterQuery(transactionFilter, null, "", application);
    }

    public static HashMap<String, Object> generateTransactionFilterFutureQuery(TransactionFilter transactionFilter, Application application) {
        return generateTransactionFilterQuery(
                transactionFilter,
                null,
                "Future",
                application);
    }

    public static HashMap<String, Object> generateTransactionFilterQuery(TransactionFilter transactionFilterInput,
                                                                         RecurringSchedule recurringSchedule,
                                                                         String queryGenerationType,
                                                                         Application application) {
        TransactionFilter transactionFilter = transactionFilterInput.clone();
        HashMap<String, Object> opHashMap = new HashMap<>();
        StringBuilder queryBuilder = new StringBuilder();
        ArrayList<String> opArgsList = new ArrayList<>();
        addTaggedAccountsToFilter(transactionFilter, application);
        queryBuilder.append(" 1=1 ");
        String ACCOUNT_NAME_QUERY = " AND account_name IN (<<account_name>>) ";
        if (transactionFilter.accountNames != null && !transactionFilter.accountNames.isEmpty()) {
            StringBuilder tempAccountName = new StringBuilder();
            buildValuesToQueryInClause(tempAccountName, transactionFilter.accountNames, opArgsList);
            queryBuilder.append(ACCOUNT_NAME_QUERY.replace("<<account_name>>", tempAccountName.toString()));
        }
        String CATEGORY_QUERY = " AND category IN (<<category>>) ";
        if (transactionFilter.categories != null && !transactionFilter.categories.isEmpty()) {
            StringBuilder tempCategory = new StringBuilder();
            buildValuesToQueryInClause(tempCategory, transactionFilter.categories, opArgsList);
            queryBuilder.append(CATEGORY_QUERY.replace("<<category>>", tempCategory.toString()));
        }
        String ACCOUNT_TYPE_QUERY = getAccountTypeQuery(recurringSchedule);
        if (transactionFilter.accountTypes != null && !transactionFilter.accountTypes.isEmpty()) {
            StringBuilder tempAccountType = new StringBuilder();
            buildValuesToQueryInClause(tempAccountType, transactionFilter.accountTypes, opArgsList);
            queryBuilder.append(ACCOUNT_TYPE_QUERY.replace("<<account_type>>", tempAccountType.toString()));
        }
        String TRANSACTION_NAME_QUERY = " AND transaction_name IN (<<transaction_name>>) ";
        if (transactionFilter.transactionNames != null && !transactionFilter.transactionNames.isEmpty()) {
            StringBuilder tempTransactionName = new StringBuilder();
            buildValuesToQueryInClause(tempTransactionName, transactionFilter.transactionNames, opArgsList);
            queryBuilder.append(TRANSACTION_NAME_QUERY.replace("<<transaction_name>>", tempTransactionName.toString()));
        }

        if (!queryGenerationType.equals("Future"))
            generateDateFilterAll(transactionFilter, queryBuilder, opArgsList);
        else generateDateFilterFuture(transactionFilter, queryBuilder, opArgsList);

        String TRANSACTION_TO_ACCOUNT_NAME_QUERY = " AND IN account_name IN (<<account_name>>) AND transfer_ind = 'Transfer'";
        if (transactionFilter.toAccountNames != null && !transactionFilter.toAccountNames.isEmpty()) {
            StringBuilder tempAccountName = new StringBuilder();
            buildValuesToQueryInClause(tempAccountName, transactionFilter.toAccountNames, opArgsList);
            queryBuilder.append(TRANSACTION_TO_ACCOUNT_NAME_QUERY.replace("<<account_name>>", tempAccountName.toString()));
        }
        String TRANSACTION_TYPE_QUERY = " AND transfer_ind IN (<<transaction_type>>) ";
        if (transactionFilter.transactionTypes != null && !transactionFilter.transactionTypes.isEmpty()) {
            StringBuilder tempTransactionType = new StringBuilder();
            buildValuesToQueryInClause(tempTransactionType, transactionFilter.transactionTypes, opArgsList);
            queryBuilder.append(TRANSACTION_TYPE_QUERY.replace("<<transaction_type>>", tempTransactionType.toString()));
        }
        String SEARCH_FILTER_QUERY = " AND (transaction_name LIKE ? OR account_name LIKE ? OR CAST(amount AS TEXT) LIKE ? OR notes LIKE ?)";
        if (transactionFilter.searchText != null && !transactionFilter.searchText.isBlank()) {
            queryBuilder.append(SEARCH_FILTER_QUERY);
            opArgsList.add("%" + transactionFilter.searchText + "%");
            opArgsList.add("%" + transactionFilter.searchText + "%");
            opArgsList.add("%" + transactionFilter.searchText + "%");
            opArgsList.add("%" + transactionFilter.searchText + "%");
        }
        if (recurringSchedule != null) {
            queryBuilder.append(" AND recurring_schedule_uuid = ?");
            opArgsList.add(recurringSchedule.recurringScheduleUUID.toString());
        }
        opHashMap.put("QUERY", queryBuilder.toString());
        opHashMap.put("VALUES", opArgsList);
        return opHashMap;
    }

    @NonNull
    private static String getAccountTypeQuery(RecurringSchedule recurringSchedule) {
        String ACCOUNT_TYPE_QUERY;
        if (recurringSchedule == null) {
            ACCOUNT_TYPE_QUERY = " AND EXISTS(SELECT 1 FROM accounts ac1 LEFT JOIN account_types at1 ON ac1.account_type = at1.account_type WHERE ac1.account_name = SplitTransfers.account_name AND at1.account_type IN (<<account_type>>))";
        } else {
            ACCOUNT_TYPE_QUERY = " AND EXISTS(SELECT 1 FROM accounts ac1 LEFT JOIN account_types at1 ON ac1.account_type = at1.account_type WHERE ac1.account_name = recurring_transactions_view.account_name AND at1.account_type IN (<<account_type>>))";
        }
        return ACCOUNT_TYPE_QUERY;
    }

    private static void generateDateFilterFuture(TransactionFilter transactionFilter, StringBuilder queryBuilder, ArrayList<String> opArgsList) {
        String TRANSACTION_DATE_BETWEEN_FROM_AND_TO_QUERY = " AND transaction_date <= ? ";
        if (transactionFilter.toTransactionDate == 0) {
            transactionFilter.toTransactionDate = Integer.parseInt(LocalDate.now().plusYears(5).format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        }
        queryBuilder.append(TRANSACTION_DATE_BETWEEN_FROM_AND_TO_QUERY);
        opArgsList.add(String.valueOf(transactionFilter.toTransactionDate));
    }

    private static void generateDateFilterAll(TransactionFilter transactionFilter, StringBuilder queryBuilder, ArrayList<String> opArgsList) {
        String TRANSACTION_DATE_BETWEEN_FROM_AND_TO_QUERY = " AND transaction_date BETWEEN ? AND ? ";
        if (transactionFilter.fromTransactionDate != 0) {
            if (transactionFilter.toTransactionDate == 0) {
                transactionFilter.toTransactionDate = Integer.parseInt(LocalDate.now().plusYears(5).format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            }
            queryBuilder.append(TRANSACTION_DATE_BETWEEN_FROM_AND_TO_QUERY);
            opArgsList.add(String.valueOf(transactionFilter.fromTransactionDate));
            opArgsList.add(String.valueOf(transactionFilter.toTransactionDate));
        } else if (transactionFilter.toTransactionDate != 0) {
            transactionFilter.fromTransactionDate = Integer.parseInt(LocalDate.now().minusYears(5).format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            queryBuilder.append(TRANSACTION_DATE_BETWEEN_FROM_AND_TO_QUERY);
            opArgsList.add(String.valueOf(transactionFilter.fromTransactionDate));
            opArgsList.add(String.valueOf(transactionFilter.toTransactionDate));
        }
    }

    public static void buildValuesToQueryInClause(StringBuilder generateQueryString, ArrayList<String> argsList, ArrayList<String> opArgsList) {
        boolean flag1 = true;
        for (String arg : argsList) {
            if (flag1) {
                generateQueryString.append("?");
                flag1 = false;
            } else {
                generateQueryString.append(",").append("?");
            }
            opArgsList.add(arg);
        }
    }
}

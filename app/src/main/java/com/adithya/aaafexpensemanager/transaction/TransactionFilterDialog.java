// TransactionFilterDialog.java

package com.adithya.aaafexpensemanager.transaction;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.adithya.aaafexpensemanager.R;
import com.adithya.aaafexpensemanager.account.AccountViewModel;
import com.adithya.aaafexpensemanager.settings.category.CategoryViewModel;
import com.adithya.aaafexpensemanager.recenttrans.RecentTransactionViewModel;
import com.adithya.aaafexpensemanager.reusableComponents.multiSelectLookupEditText.MultiSelectLookupEditText;
import com.adithya.aaafexpensemanager.settings.accounttype.AccountTypeViewModel;
import com.adithya.aaafexpensemanager.transactionFilter.TransactionFilter;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/** @noinspection CallToPrintStackTrace*/
public class TransactionFilterDialog {

    private final Context context;
    private final CategoryViewModel categoryViewModel;
    private final AccountViewModel accountViewModel;
    private final AccountTypeViewModel accountTypeViewModel;
    private final RecentTransactionViewModel recentTransactionViewModel;
    private final TransactionFilter transactionFilter;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter dateFormatterToDBInt = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final OnFilterAppliedListener listener;
    private AlertDialog filterDialog;
    private MultiSelectLookupEditText transactionNameTextView;
    private TextInputEditText fromDateEditText;
    private TextInputEditText toDateEditText;
    private MultiSelectLookupEditText categoriesTextView;
    private MultiSelectLookupEditText accountsTextView;
    private MultiSelectLookupEditText accountTypeTextView;
    /** @noinspection FieldCanBeLocal*/
    private Button applyFilterButton;
    /** @noinspection FieldCanBeLocal*/
    private Button clearFilterButton;
    public interface OnFilterAppliedListener {
        void onFilterApplied(TransactionFilter filter);
    }

    public TransactionFilterDialog(Context context, ViewModelStoreOwner viewModelStoreOwner, TransactionFilter transactionFilter, OnFilterAppliedListener listener) {
        this.context = context;
        this.categoryViewModel = new ViewModelProvider(viewModelStoreOwner).get(CategoryViewModel.class);
        this.accountViewModel = new ViewModelProvider(viewModelStoreOwner).get(AccountViewModel.class);
        this.accountTypeViewModel = new ViewModelProvider(viewModelStoreOwner).get(AccountTypeViewModel.class);
        this.recentTransactionViewModel = new ViewModelProvider(viewModelStoreOwner).get(RecentTransactionViewModel.class);
        this.transactionFilter = transactionFilter;
        this.listener = listener;
    }

    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_transaction_filter, null);
        builder.setView(dialogView);

        transactionNameTextView = dialogView.findViewById(R.id.transactionNameEditText);
        fromDateEditText = dialogView.findViewById(R.id.fromDateEditText);
        toDateEditText = dialogView.findViewById(R.id.toDateEditText);
        categoriesTextView = dialogView.findViewById(R.id.categoriesTextView);
        accountsTextView = dialogView.findViewById(R.id.accountsDialogTextView);
        accountTypeTextView = dialogView.findViewById(R.id.accountTypesDialogTextView);
        applyFilterButton = dialogView.findViewById(R.id.applyFilterButton);
        clearFilterButton = dialogView.findViewById(R.id.clearFilterButton);
        setScreenElementsFromTransactionFilter();
        transactionNameTextView.setOnItemsSelectedListener(selectedItems -> {
            transactionFilter.transactionNames = (ArrayList<String>) selectedItems;
        });
        categoriesTextView.setOnItemsSelectedListener(selectedItems -> {
            transactionFilter.categories = (ArrayList<String>) selectedItems;
        });
        accountsTextView.setOnItemsSelectedListener(selectedItems -> {
            transactionFilter.accountNames = (ArrayList<String>) selectedItems;
        });
        accountTypeTextView.setOnItemsSelectedListener(selectedItems -> {
            transactionFilter.accountTypes = (ArrayList<String>) selectedItems;
        });
        fromDateEditText.setOnClickListener(v -> showDatePickerDialog(fromDateEditText));
        toDateEditText.setOnClickListener(v -> showDatePickerDialog(toDateEditText));
        filterDialog = builder.create();
        applyFilterButton.setOnClickListener(v -> {
            try {
                //noinspection DataFlowIssue
                if (!fromDateEditText.getText().toString().isEmpty()) {
                    LocalDate fromDate = LocalDate.parse(fromDateEditText.getText().toString(), dateFormatter);
                    transactionFilter.fromTransactionDate = Integer.parseInt(fromDate.format(dateFormatterToDBInt));
                } else {
                    transactionFilter.fromTransactionDate = 0;
                }

                //noinspection DataFlowIssue
                if (!toDateEditText.getText().toString().isEmpty()) {
                    LocalDate toDate = LocalDate.parse(toDateEditText.getText().toString(), dateFormatter);
                    transactionFilter.toTransactionDate = Integer.parseInt(toDate.format(dateFormatterToDBInt));
                } else {
                    transactionFilter.toTransactionDate = 0;
                }
            } catch (DateTimeParseException e) {
                e.printStackTrace();
            }
            listener.onFilterApplied(transactionFilter);
            filterDialog.dismiss();
        });

        clearFilterButton.setOnClickListener(v -> {
            transactionFilter.clear();
            setScreenElementsFromTransactionFilter();
            listener.onFilterApplied(transactionFilter);
            filterDialog.dismiss();
        });
        filterDialog.show();
    }

    private void setScreenElementsFromTransactionFilter() {
        setTransactionNameTextView(transactionNameTextView);
        if (transactionFilter.fromTransactionDate != 0)
            fromDateEditText.setText(dateFormatter.format(LocalDate.parse(String.valueOf(transactionFilter.fromTransactionDate), dateFormatterToDBInt)));
        else fromDateEditText.setText("");
        if (transactionFilter.toTransactionDate != 0)
            toDateEditText.setText(dateFormatter.format(LocalDate.parse(String.valueOf(transactionFilter.toTransactionDate), dateFormatterToDBInt)));
        else toDateEditText.setText("");
        setCategoriesTextView(categoriesTextView);
        setAccountsTextView(accountsTextView);
        setAccountTypeTextView(accountTypeTextView);
    }
    private void showDatePickerDialog(final TextInputEditText editText) {
        LocalDate currentDate = LocalDate.now();
        int year = currentDate.getYear();
        int month = currentDate.getMonthValue() - 1; // Month is 0-indexed
        int day = currentDate.getDayOfMonth();
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, year1, month1, dayOfMonth) -> {
            LocalDate selectedDate = LocalDate.of(year1, month1 + 1, dayOfMonth);
            editText.setText(dateFormatter.format(selectedDate));
        }, year, month, day);
        datePickerDialog.show();
    }
    private void setTransactionNameTextView(TextView selectedTransactionNamesTextView){
        List<String> transactionNamesList = Objects.requireNonNull(recentTransactionViewModel
                        .getRecentTransactions()
                        .getValue())
                .stream()
                .map(t -> t.transactionName)
                .collect(Collectors.toList());
        transactionNameTextView.setItems(transactionNamesList);
        transactionNameTextView.setSelectedItems(transactionFilter.transactionNames);
    }
    private void setAccountTypeTextView(TextView selectedAccountTypesTextView) {
        List<String> accountTypes = accountTypeViewModel
                .getAccountTypes()
                .stream()
                .map(a -> a.accountType)
                .collect(Collectors.toList());
        accountTypeTextView.setItems(accountTypes);
        accountTypeTextView.setSelectedItems(transactionFilter.accountTypes);
    }

    private void setAccountsTextView(TextView selectedAccountsTextView) {
        List<String> accountNames = Objects.requireNonNull(accountViewModel
                        .getAccounts()
                        .getValue())
                .stream()
                .map(t -> t.accountName)
                .collect(Collectors.toList());
        accountsTextView.setItems(accountNames);
        accountsTextView.setSelectedItems(transactionFilter.accountNames);
    }
    private void setCategoriesTextView(TextView selectedCategoriesTextView) {
        List<String> categoryNamesList = Objects.requireNonNull(categoryViewModel
                        .getCategories()
                        .getValue())
                .stream()
                .map(t -> t.categoryName)
                .collect(Collectors.toList());
        categoriesTextView.setItems(categoryNamesList);
        categoriesTextView.setSelectedItems(transactionFilter.categories);
    }
}
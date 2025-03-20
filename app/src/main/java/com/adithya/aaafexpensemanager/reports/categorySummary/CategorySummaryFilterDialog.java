package com.adithya.aaafexpensemanager.reports.categorySummary;
import android.app.AlertDialog;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/** */
public class CategorySummaryFilterDialog {
    private final Context context;
    private final CategoryViewModel categoryViewModel;
    private final RecentTransactionViewModel recentTransactionViewModel;
    private final AccountViewModel accountViewModel;
    private final AccountTypeViewModel accountTypeViewModel;
    private final TransactionFilter transactionFilter;
    private final CategorySummaryFilterDialog.OnFilterAppliedListener listener;
    private AlertDialog filterDialog;
    private MultiSelectLookupEditText transactionNameTextView;
    private MultiSelectLookupEditText categoriesTextView;
    private MultiSelectLookupEditText accountsTextView;
    private MultiSelectLookupEditText accountTypeTextView;
    /** @noinspection FieldCanBeLocal*/
    private Button applyFilterButton;
    /** @noinspection FieldCanBeLocal*/
    private Button clearFilterButton;
    /** @noinspection unused*/
    public interface OnFilterAppliedListener {
        void onFilterApplied(TransactionFilter filter);
    }

    public CategorySummaryFilterDialog(Context context, ViewModelStoreOwner viewModelStoreOwner, TransactionFilter transactionFilter, CategorySummaryFilterDialog.OnFilterAppliedListener listener) {
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
        View dialogView = inflater.inflate(R.layout.dialog_report_category_summary_filter, null);
        builder.setView(dialogView);

        transactionNameTextView = dialogView.findViewById(R.id.transactionNameTextView);
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
        filterDialog = builder.create();
        applyFilterButton.setOnClickListener(v -> {
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
        setCategoriesTextView(categoriesTextView);
        setAccountsTextView(accountsTextView);
        setAccountTypeTextView(accountTypeTextView);
    }
    private void setTransactionNameTextView(MultiSelectLookupEditText transactionNameTextView) {
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
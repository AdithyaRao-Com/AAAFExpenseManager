package com.adithya.aaafexpensemanager.transactionFilter;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.adithya.aaafexpensemanager.R;
import com.adithya.aaafexpensemanager.account.AccountViewModel;
import com.adithya.aaafexpensemanager.recenttrans.RecentTransactionViewModel;
import com.adithya.aaafexpensemanager.reports.categorySummary.CategorySummaryRecord;
import com.adithya.aaafexpensemanager.reports.forecastSummary.ForecastConstants;
import com.adithya.aaafexpensemanager.reusableComponents.lookupEditText.LookupEditText;
import com.adithya.aaafexpensemanager.reusableComponents.multiSelectLookupEditText.MultiSelectLookupEditText;
import com.adithya.aaafexpensemanager.settings.accounttype.AccountTypeViewModel;
import com.adithya.aaafexpensemanager.settings.category.CategoryViewModel;
import com.adithya.aaafexpensemanager.util.AppConstants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/** @noinspection DataFlowIssue, FieldCanBeLocal */
public class CreateTransactionFilterFragment extends Fragment {
    private Context context;
    private RecentTransactionViewModel recentTransactionViewModel;
    private AccountViewModel accountViewModel;
    private AccountTypeViewModel accountTypeViewModel;
    private CategoryViewModel categoryViewModel;
    private TransactionFilter transactionFilter;
    private MultiSelectLookupEditText transactionNameTextView;
    private MultiSelectLookupEditText categoriesTextView;
    private MultiSelectLookupEditText accountsTextView;
    private MultiSelectLookupEditText accountTypeTextView;
    private MultiSelectLookupEditText accountTagsTextView;
    private LookupEditText reportTypeTextView;
    private FloatingActionButton createTransactionFilterFab;
    private TextInputEditText reportNameEditText;
    private RadioGroup dateSelectionTypeRadioGroup;
    private RadioButton dateSelectionTypeFixed;
    private RadioButton dateSelectionTypeRelative;
    private LookupEditText periodNameLookup;
    private EditText dateFromEditText;
    private EditText dateToEditText;
    private LinearLayout dateFromToWrapper;
    private TextInputLayout periodNameLookupWrapper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getContext().getApplicationContext();
        recentTransactionViewModel = new ViewModelProvider(this).get(RecentTransactionViewModel.class);
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        accountTypeViewModel = new ViewModelProvider(this).get(AccountTypeViewModel.class);
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        View view = inflater.inflate(R.layout.fragment_create_transacton_filter, container, false);
        transactionNameTextView = view.findViewById(R.id.transactionNameTextView);
        categoriesTextView = view.findViewById(R.id.categoriesTextView);
        accountsTextView = view.findViewById(R.id.accountsDialogTextView);
        accountTypeTextView = view.findViewById(R.id.accountTypesDialogTextView);
        accountTagsTextView = view.findViewById(R.id.accountTagsDialogTextView);
        createTransactionFilterFab = view.findViewById(R.id.createTransactionFilterFab);
        reportNameEditText = view.findViewById(R.id.reportNameEditText);
        reportTypeTextView = view.findViewById(R.id.reportTypeLookup);
        dateSelectionTypeRadioGroup = view.findViewById(R.id.dateSelectionTypeRadioGroup);
        dateSelectionTypeFixed = view.findViewById(R.id.dateSelectionTypeFixed);
        dateSelectionTypeRelative = view.findViewById(R.id.dateSelectionTypeRelative);
        periodNameLookup = view.findViewById(R.id.periodNameLookup);
        dateFromEditText = view.findViewById(R.id.dateFromEditText);
        dateToEditText = view.findViewById(R.id.dateToEditText);
        dateFromToWrapper = view.findViewById(R.id.dateFromToWrapper);
        periodNameLookupWrapper = view.findViewById(R.id.periodNameLookupWrapper);
        setupArguments();
        setupReportTypeTextView();
        setupPeriodNameLookup();
        setupTransactionNameTextView();
        setupReportNameEditText();
        setupDateFromEditText();
        setupDateToEditText();
        setupCategoriesTextView();
        setupAccountsTextView();
        setupAccountTypeTextView();
        setupAccountTagsTextView();
        setDateSelectionRadioGroup();
        setupCreateTransactionFilterFab();
        return view;
    }

    private void setupDateToEditText() {
        dateToEditText.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    (view1, year1, monthOfYear, dayOfMonth) -> {
                        LocalDate transactionDate = LocalDate.of(year1, monthOfYear + 1, dayOfMonth);
                        dateToEditText.setText(transactionDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    }, year, month, day);
            datePickerDialog.show();
        });
    }

    private void setupDateFromEditText() {
        dateFromEditText.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    (view1, year1, monthOfYear, dayOfMonth) -> {
                        LocalDate transactionDate = LocalDate.of(year1, monthOfYear + 1, dayOfMonth);
                        dateFromEditText.setText(transactionDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    }, year, month, day);
            datePickerDialog.show();
        });
    }

    private void setupPeriodNameLookup() {
        Log.d("CreateTransactionFilterFragment", "setupPeriodNameLookup - running");
        String item = transactionFilter.reportType;
        if(item.equals(AppConstants.REPORT_TYPE_CATEGORY_SUMMARY)){
            List<LookupEditText.LookupEditTextItem> periodNames = List.of(CategorySummaryRecord.TimePeriod.values());
            periodNameLookup.setItems(periodNames);
        }
        else if(item.equals(AppConstants.REPORT_TYPE_BALANCE_FORECAST_SUMMARY)){
            List<LookupEditText.LookupEditTextItem> periodNames = List.of(ForecastConstants.ForecastTimePeriod.values());
            periodNameLookup.setItems(periodNames);
        }
        else{
            periodNameLookup.setItems(new ArrayList<>());
        }
    }

    private void setupReportNameEditText() {
        Log.d("CreateTransactionFilterFragment", "setupReportNameEditText - running");
    }

    private void setDateSelectionRadioGroup() {
        if(!transactionFilter.periodName.isBlank()){
            dateSelectionTypeRelative.setChecked(true);
            dateFromToWrapper.setVisibility(View.GONE);
            periodNameLookupWrapper.setVisibility(View.VISIBLE);
        }
        else{
            dateSelectionTypeFixed.setChecked(true);
            dateFromToWrapper.setVisibility(View.VISIBLE);
            periodNameLookupWrapper.setVisibility(View.GONE);
        }
        dateSelectionTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == dateSelectionTypeFixed.getId()) {
                dateFromToWrapper.setVisibility(View.VISIBLE);
                periodNameLookupWrapper.setVisibility(View.GONE);
                periodNameLookup.setText("");
                transactionFilter.periodName="";
            } else if (checkedId == dateSelectionTypeRelative.getId()) {
                dateFromToWrapper.setVisibility(View.GONE);
                periodNameLookupWrapper.setVisibility(View.VISIBLE);
                dateFromEditText.setText("");
                dateToEditText.setText("");
                transactionFilter.fromTransactionDate = 0;
                transactionFilter.toTransactionDate = 0;
            }
        });
    }

    private void setupCreateTransactionFilterFab() {
        createTransactionFilterFab.setOnClickListener(v -> {
            // TODO - Call Api for creating the filter
        });
    }
    /** @noinspection deprecation*/
    private void setupArguments() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            transactionFilter = bundle.getParcelable("transactionFilter");
            initializeFields();
        }
        else{
            transactionFilter = new TransactionFilter();
        }
    }
    private void setupReportTypeTextView() {
        reportTypeTextView.setItemStrings(new ArrayList<>(AppConstants.REPORT_TYPES));
        reportTypeTextView.setOnItemClickListener((item,position)
                -> {
            transactionFilter.reportType = item.toEditTextLookupString();
            if(item.toEditTextLookupString().equals(AppConstants.REPORT_TYPE_CATEGORY_SUMMARY)){
                List<LookupEditText.LookupEditTextItem> periodNames = List.of(CategorySummaryRecord.TimePeriod.values());
                periodNameLookup.setItems(periodNames);
            }
            else if(item.toEditTextLookupString().equals(AppConstants.REPORT_TYPE_BALANCE_FORECAST_SUMMARY)){
                List<LookupEditText.LookupEditTextItem> periodNames = List.of(ForecastConstants.ForecastTimePeriod.values());
                periodNameLookup.setItems(periodNames);
            }
            else{
                periodNameLookup.setItems(new ArrayList<>());
            }
            transactionFilter.fromTransactionDate = 0;
            transactionFilter.toTransactionDate = 0;
            dateFromEditText.setText("");
            dateToEditText.setText("");
            transactionFilter.periodName = "";
            periodNameLookup.setText("");
        });
    }
    private void initializeFields() {
        transactionNameTextView.setItems(transactionFilter.transactionNames);
        categoriesTextView.setSelectedItems(transactionFilter.categories);
        accountsTextView.setSelectedItems(transactionFilter.accountNames);
        accountTypeTextView.setSelectedItems(transactionFilter.accountTypes);
        accountTagsTextView.setSelectedItems(transactionFilter.accountTags);
        reportNameEditText.setText(transactionFilter.reportName);
        reportTypeTextView.setText(transactionFilter.reportType);
        periodNameLookup.setText(transactionFilter.periodName);
        dateFromEditText.setText(transactionFilter.fromTransactionDate != 0 ? transactionFilter.fromTransactionDateToLocalDate().toString() : "");
        dateToEditText.setText(transactionFilter.toTransactionDate != 0 ? transactionFilter.toTransactionDateToLocalDate().toString() : "");
    }

    private void setupAccountTagsTextView() {
        accountTagsTextView.setItems(accountViewModel.getAccountTags());
        accountTagsTextView.setOnItemsSelectedListener(selectedItems -> transactionFilter.accountTags = (ArrayList<String>) selectedItems);
    }

    private void setupAccountTypeTextView() {
        accountTypeTextView.setItems(accountTypeViewModel.getAccountTypes().stream().map(t -> t.accountType).toList());
        accountTypeTextView.setOnItemsSelectedListener(selectedItems -> transactionFilter.accountTypes = (ArrayList<String>) selectedItems);
    }

    private void setupAccountsTextView() {
        accountsTextView.setItems(accountViewModel.getAccounts().getValue().stream().map(t -> t.accountName).toList());
        accountsTextView.setOnItemsSelectedListener(selectedItems -> transactionFilter.accountNames = (ArrayList<String>) selectedItems);
    }

    private void setupCategoriesTextView() {
        categoriesTextView.setItems(categoryViewModel.getCategories().getValue().stream().map(t -> t.categoryName).toList());
        categoriesTextView.setOnItemsSelectedListener(selectedItems -> transactionFilter.categories = (ArrayList<String>) selectedItems);
    }

    private void setupTransactionNameTextView() {
        transactionNameTextView.setItems(recentTransactionViewModel.getRecentTransactions().getValue().stream().map(t -> t.transactionName).toList());
        transactionNameTextView.setOnItemsSelectedListener(selectedItems -> transactionFilter.transactionNames = (ArrayList<String>) selectedItems);
    }
}

package com.adithya.aaafexpensemanager.recurring;

import static java.lang.Math.min;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.adithya.aaafexpensemanager.R;
import com.adithya.aaafexpensemanager.account.AccountViewModel;
import com.adithya.aaafexpensemanager.recenttrans.RecentTransactionViewModel;
import com.adithya.aaafexpensemanager.reusableComponents.lookupEditText.LookupEditText;
import com.adithya.aaafexpensemanager.reusableComponents.reusableDialogs.ConfirmationDialog;
import com.adithya.aaafexpensemanager.settings.category.CategoryViewModel;
import com.adithya.aaafexpensemanager.transaction.exception.InterCurrencyTransferNotSupported;
import com.adithya.aaafexpensemanager.util.AppConstants;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/** @noinspection DataFlowIssue*/
public class CreateRecurringFragment extends Fragment {
    // TODO - Implement a method to show the currency in the amount field
    private RecurringViewModel viewModel;
    private AutoCompleteTextView transactionNameTextView;
    private LookupEditText recurringScheduleAutoCompleteTextView;
    private EditText repeatIntervalDaysEditText;
    private EditText recurringStartDateEditText;
    private EditText recurringEndDateEditText;
    private LookupEditText categoryAutoCompleteTextView;
    private List<String> categoryNames;
    private EditText notesEditText;
    private EditText amountEditText;
    private LookupEditText accountNameAutoComplete;
    private LookupEditText toAccountNameAutoComplete;
    private LinearLayout linearLayoutDisableable;
    private FloatingActionButton createTransactionButton;
    private LocalDate recurringStartDate;
    private LocalDate recurringEndDate;
    private RecurringSchedule originalRecurringSchedule;
    private AccountViewModel accountViewModel;
    private List<String> accountNames = new ArrayList<>();
    private CategoryViewModel categoryViewModel;
    private RecentTransactionViewModel recentTransactionViewModel;
    private ArrayAdapter<String> autoCompleteAdapterRecentTrans;
    private TextInputLayout repeatDaysWrapper;
    private MaterialButton transactionTypeButton;
    private int transactionTypePosition = 0;
    private TextView transactionTypeTextView;
    /** @noinspection FieldCanBeLocal, unused */
    private boolean isEditing;
    private MenuItem deleteMenuItem;
    private MenuItem showFutureTxnMenuItem;
    private final Map<Integer, String> transactionTypeIntKey =
            Map.of(0,"Expense",
                    1,"Income",
                    2,"Transfer");
    private final Map<String,Integer> transactionStringKey =
            Map.of("Expense",0,
                    "Income",1,
                    "Transfer",2);
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_recurring, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(RecurringViewModel.class);
        categoryViewModel = new ViewModelProvider(requireActivity()).get(CategoryViewModel.class);
        accountViewModel = new ViewModelProvider(requireActivity()).get(AccountViewModel.class);
        recentTransactionViewModel = new ViewModelProvider(requireActivity()).get(RecentTransactionViewModel.class);
        findViewByIdCreateRecurring(view);
        updateRecurringButton();
        setupTransactionTypeButton();
        setupTransactionNameAutoComplete();
        setupCategoryTypeAutocomplete();
        parseArgumentsAndSetFields();
        updateToAccountVisibility();
        setupAccountNameAndToAccountNameAutocomplete();
        setupRecurringStartDateField();
        setupRecurringEndDateField();
        setupCreateTransactionButton();
        setupRecurringScheduleAutoComplete();
        return view;
    }
    private void findViewByIdCreateRecurring(View view) {
        transactionTypeTextView = view.findViewById(R.id.transactionTypeTextView);
        transactionNameTextView = view.findViewById(R.id.transactionNameTextView);
        recurringScheduleAutoCompleteTextView = view.findViewById(R.id.recurringScheduleAutoCompleteTextView);
        repeatIntervalDaysEditText = view.findViewById(R.id.repeatIntervalDaysEditText);
        recurringStartDateEditText = view.findViewById(R.id.recurringStartDateEditText);
        recurringEndDateEditText = view.findViewById(R.id.recurringEndDateEditText);
        categoryAutoCompleteTextView = view.findViewById(R.id.categoryAutoCompleteTextView);
        notesEditText = view.findViewById(R.id.notesEditText);
        transactionTypeButton = view.findViewById(R.id.transactionTypeButton);
        amountEditText = view.findViewById(R.id.amountEditText);
        accountNameAutoComplete = view.findViewById(R.id.accountNameAutoComplete);
        toAccountNameAutoComplete = view.findViewById(R.id.toAccountNameAutoComplete);
        linearLayoutDisableable = view.findViewById(R.id.linearLayoutDisableable);
        createTransactionButton = view.findViewById(R.id.createRecurringFab);
        repeatDaysWrapper = view.findViewById(R.id.repeatDaysWrapper);
    }
    private void parseArgumentsAndSetFields() {
        if (getArguments() != null && getArguments().containsKey("recurringSchedule")) {
            //noinspection deprecation
            originalRecurringSchedule = getArguments().getParcelable("recurringSchedule");
            isEditing = false;
            if (originalRecurringSchedule != null) {
                if (getArguments().containsKey("isEditing"))
                    isEditing = getArguments().getBoolean("isEditing");
                else{
                    isEditing = true;
                }
                if(!isEditing){
                    originalRecurringSchedule.recurringScheduleUUID = null;
                }
                setRecurringScheduleFields(originalRecurringSchedule);
            } else {
                setDefaultValuesForFields();
            }
        } else {
            setDefaultValuesForFields();
        }
    }

    private void setDefaultValuesForFields() {
        setRecurringScheduleFields(new RecurringSchedule());
        //noinspection DataFlowIssue
        transactionTypePosition = transactionStringKey.get("Expense");
        setCurrentDate();
    }

    private void setRecurringScheduleFields(RecurringSchedule recurringSchedule) {
        transactionNameTextView.setText(recurringSchedule.transactionName);
        recurringScheduleAutoCompleteTextView.setText(recurringSchedule.recurringScheduleName);
        repeatIntervalDaysEditText.setText(String.valueOf(recurringSchedule.repeatIntervalDays));
        enableDisableRepeatDaysWrapper(String.valueOf(recurringSchedule.recurringScheduleName));
        recurringStartDate = recurringSchedule.getRecurringStartDateLocalDate();
        recurringStartDateEditText.setText(recurringSchedule.getRecurringStartDateString());
        recurringEndDate = recurringSchedule.getRecurringEndDateLocalDate();
        recurringEndDateEditText.setText(recurringSchedule.getRecurringEndDateString());
        categoryAutoCompleteTextView.setText(recurringSchedule.category);
        notesEditText.setText(recurringSchedule.notes);
        //noinspection DataFlowIssue
        transactionTypePosition = transactionStringKey.get(recurringSchedule.transactionType);
        updateRecurringButton();
        amountEditText.setText(String.valueOf(recurringSchedule.amount));
        accountNameAutoComplete.setText(recurringSchedule.accountName);
        toAccountNameAutoComplete.setText(recurringSchedule.toAccountName);
    }

    private void setCurrentDate() {
        recurringStartDate = LocalDate.now();
        recurringStartDateEditText.setText(recurringStartDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        recurringEndDate = recurringStartDate.plusYears(AppConstants.DEFAULT_RECURRING_END_INTERVAL);
        recurringEndDateEditText.setText(recurringEndDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }
    private void setupCreateTransactionButton() {
        createTransactionButton.setOnClickListener(v -> {
            try {
                String transactionName = transactionNameTextView.getText().toString();
                if (transactionName.isBlank()) {
                    transactionNameTextView.setError("Transaction Name cannot be blank");
                    return;
                }
                String recurringSchedule = recurringScheduleAutoCompleteTextView.getText().toString();
                int repeatIntervalDays;
                try {
                    repeatIntervalDays = Integer.parseInt(repeatIntervalDaysEditText.getText().toString());
                } catch (Exception e) {
                    repeatIntervalDays = 0;
                }
                String transactionType = transactionTypeIntKey.get(transactionTypePosition);
                String category = categoryAutoCompleteTextView.getText().toString();
                LocalDate recurringStartDate;
                LocalDate recurringEndDate;
                try {
                    recurringStartDate = LocalDate.parse(recurringStartDateEditText.getText().toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                } catch (Exception e) {
                    recurringStartDateEditText.setError("Invalid start date");
                    Snackbar.make(getView(), "Invalid start date", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                try {
                    recurringEndDate = LocalDate.parse(recurringEndDateEditText.getText().toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                } catch (Exception e) {
                    recurringEndDateEditText.setError("Invalid end date");
                    Snackbar.make(getView(), "Invalid end date", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (recurringStartDate.isAfter(recurringEndDate)) {
                    recurringEndDateEditText.setError("End date cannot be before start date");
                    Snackbar.make(getView(), "End date cannot be before start date", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (!categoryNames.contains(category)) {
                    categoryAutoCompleteTextView.setError("Select a valid category from the list");
                    return;
                }
                String notes = notesEditText.getText().toString();
                double amount;
                try {
                    amount = Double.parseDouble(amountEditText.getText().toString());
                } catch (NumberFormatException e) {
                    amountEditText.setError("Invalid amount");
                    Toast.makeText(requireContext(), "Invalid amount", Toast.LENGTH_SHORT).show();
                    return;
                }
                String selectedAccountName = accountNameAutoComplete.getText().toString();
                String toAccountName = toAccountNameAutoComplete.getText().toString();
                if (!accountNames.contains(selectedAccountName)) {
                    accountNameAutoComplete.setError("Select a valid account from the list");
                    return;
                }
                if ("Transfer".equals(transactionType) && (toAccountName.isEmpty() || !accountNames.contains(toAccountName))) {
                    toAccountNameAutoComplete.setError("Please select a valid 'To Account' for transfers");
                    return;
                }
                if (isEditing) {
                    originalRecurringSchedule.transactionName = transactionName;
                    originalRecurringSchedule.recurringScheduleName = recurringSchedule;
                    originalRecurringSchedule.repeatIntervalDays = repeatIntervalDays;
                    originalRecurringSchedule.recurringStartDate = Integer.parseInt(recurringStartDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
                    originalRecurringSchedule.recurringEndDate = Integer.parseInt(recurringEndDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
                    originalRecurringSchedule.transactionType = transactionType;
                    originalRecurringSchedule.category = category;
                    originalRecurringSchedule.notes = notes;
                    originalRecurringSchedule.amount = amount;
                    originalRecurringSchedule.accountName = selectedAccountName;
                    originalRecurringSchedule.toAccountName = toAccountName;
                    viewModel.updateRecurringSchedule(originalRecurringSchedule);
                    Toast.makeText(requireContext(), "Recurring schedule updated", Toast.LENGTH_SHORT).show();
                } else {
                    RecurringSchedule transaction = new RecurringSchedule(
                            transactionName,
                            recurringSchedule,
                            repeatIntervalDays,
                            recurringStartDate,
                            recurringEndDate,
                            category,
                            notes,
                            transactionType,
                            amount,
                            selectedAccountName,
                            toAccountName,
                            transactionType
                    );
                    boolean checkInserted = viewModel.addRecurringSchedule(transaction);
                    if (checkInserted) {
                        Toast.makeText(requireContext(), "Recurring schedule added", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Recurring schedule not added", Toast.LENGTH_SHORT).show();
                    }
                    setRecurringScheduleFields(new RecurringSchedule());
                }
                NavHostFragment.findNavController(this).navigate(R.id.action_createRecurringFragment_to_recurringFragment);
            }
            catch (InterCurrencyTransferNotSupported e){
                new AlertDialog.Builder(getContext())
                        .setTitle("Error")
                        .setMessage(e.getMessage())
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                (dialog, which) ->NavHostFragment.findNavController(this)
                                        .navigate(R.id.action_createRecurringFragment_to_recurringFragment))
                        .create()
                        .show();
            }
        });
    }
    private void setupTransactionTypeButton() {
        transactionTypeButton.setOnClickListener(v -> {
            transactionTypePosition = (transactionTypePosition + 1) % 3;
            updateRecurringButton();
            updateToAccountVisibility();
        });
    }

    private void setupRecurringStartDateField() {
        recurringStartDateEditText.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    (view1, year1, monthOfYear, dayOfMonth) -> {
                        recurringStartDate = LocalDate.of(year1, monthOfYear + 1, dayOfMonth);
                        recurringStartDateEditText.setText(recurringStartDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    }, year, month, day);
            Instant currentInstant = LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC);
            datePickerDialog.getDatePicker().setMinDate(currentInstant.toEpochMilli());
            datePickerDialog.show();
        });
    }
    private void setupRecurringEndDateField() {
        recurringEndDateEditText.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    (view1, year1, monthOfYear, dayOfMonth) -> {
                        recurringEndDate = LocalDate.of(year1, monthOfYear + 1, dayOfMonth);
                        recurringEndDateEditText.setText(recurringEndDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    }, year, month, day);
            try{
                LocalDate startDate = LocalDate.parse(recurringStartDateEditText.getText().toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                Instant currentInstant = startDate.atStartOfDay().toInstant(ZoneOffset.UTC);
                datePickerDialog.getDatePicker().setMinDate(currentInstant.toEpochMilli());
            }
            catch (Exception e){
                Instant currentInstant = LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC);
                datePickerDialog.getDatePicker().setMinDate(currentInstant.toEpochMilli());
            }
            datePickerDialog.show();
        });
    }
    private void setupAccountNameAndToAccountNameAutocomplete() {
        accountViewModel.getAccounts().observe(getViewLifecycleOwner(), accountNames -> {
            this.accountNames = accountNames.stream().map(account -> account.accountName).collect(Collectors.toList());
            this.accountNameAutoComplete.setItems(this.accountNames);
            this.toAccountNameAutoComplete.setItems(this.accountNames);
        });
    }
    private void setupCategoryTypeAutocomplete() {
        categoryViewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            // Efficiently extract category names:
            categoryNames = categories.stream()
                    .map(category -> category.categoryName)
                    .collect(Collectors.toList());
            categoryAutoCompleteTextView.setItems(categoryNames);
            if (originalRecurringSchedule != null) {
                categoryAutoCompleteTextView.setText(originalRecurringSchedule.category);
            }
        });
    }
    private void setupRecurringScheduleAutoComplete() {
        List<String> recurringSchedules = AppConstants.RECURRING_SCHEDULES;
        recurringScheduleAutoCompleteTextView.setItems(recurringSchedules);
        if (originalRecurringSchedule != null) { // Editing existing transaction
            recurringScheduleAutoCompleteTextView.setText(originalRecurringSchedule.recurringScheduleName);
        }
        recurringScheduleAutoCompleteTextView.setOnItemClickListener((selectedRecurringSchedule,int1)-> enableDisableRepeatDaysWrapper(selectedRecurringSchedule));
    }

    private void enableDisableRepeatDaysWrapper(String selectedRecurringSchedule) {
        if(selectedRecurringSchedule.equals("Custom"))
            repeatDaysWrapper.setVisibility(View.VISIBLE);
        else repeatDaysWrapper.setVisibility(View.GONE);
    }

    private void updateToAccountVisibility() {
        String transactionTypeString = transactionTypeIntKey.get(transactionTypePosition);
        if ("Transfer".equals(transactionTypeString))
            linearLayoutDisableable.setVisibility(View.VISIBLE);
        else linearLayoutDisableable.setVisibility(View.GONE);
    }
    private void updateRecurringButton() {
        switch (transactionTypePosition) {
            case 0: // Expense
                transactionTypeButton.setText("-");
                transactionTypeButton.setBackgroundColor(Color.RED);
                transactionTypeTextView.setText(R.string.expense_transaction);
                break;
            case 1: // Income
                transactionTypeButton.setText("+");
                transactionTypeButton.setBackgroundColor(Color.GREEN);
                transactionTypeTextView.setText(R.string.income_transaction);
                break;
            case 2: // Transfer
                //noinspection UnnecessaryUnicodeEscape
                transactionTypeButton.setText("\u2192");
                transactionTypeButton.setBackgroundColor(Color.BLUE);
                transactionTypeTextView.setText(R.string.transfer_transaction);
                break;
        }
        // Make the button square programmatically after layout is complete
        transactionTypeButton.getViewTreeObserver().addOnPreDrawListener(
                () -> {
                    int size = min(transactionTypeButton.getWidth(), transactionTypeButton.getHeight());
                    transactionTypeButton.getLayoutParams().width = size;
                    transactionTypeButton.getLayoutParams().height = size;
                    transactionTypeButton.requestLayout();
                    return true;
                }
        );
    }
    private void setupTransactionNameAutoComplete() {
        recentTransactionViewModel.getRecentTransactions().observe(getViewLifecycleOwner(), recentTransactions -> {
            List<String> recentTransactionNames = recentTransactions.stream()
                    .map(t->t.transactionName)
                    .collect(Collectors.toList());
            if (autoCompleteAdapterRecentTrans == null) {
                autoCompleteAdapterRecentTrans = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, recentTransactionNames);
                transactionNameTextView.setAdapter(autoCompleteAdapterRecentTrans);
                transactionNameTextView.setThreshold(0);
            } else {
                autoCompleteAdapterRecentTrans.clear();
                autoCompleteAdapterRecentTrans.addAll(recentTransactionNames);
                autoCompleteAdapterRecentTrans.notifyDataSetChanged();
            }
        });
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.create_recurring_menu, menu);
                deleteMenuItem = menu.findItem(R.id.action_delete_recurring_schedule);
                showFutureTxnMenuItem = menu.findItem(R.id.action_show_future_transactions);
                setOptions(isEditing);
            }
            /** @noinspection DataFlowIssue*/
            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if(menuItem.getItemId()==R.id.action_delete_recurring_schedule){
                    new ConfirmationDialog(getContext(),
                            "Delete Transaction",
                            "Are you sure you want to delete this Transaction?",
                            ()-> {
                                viewModel.deleteRecurringSchedule(originalRecurringSchedule);
                                originalRecurringSchedule=null;
                                NavHostFragment.findNavController(getParentFragment()).navigate(R.id.action_createRecurringFragment_to_recurringFragment);
                            },
                            ()->{},
                            "Delete",
                            "Cancel"
                    );
                    return true;
                }
                else if(menuItem.getItemId()==R.id.action_show_future_transactions){
                    Bundle args = new Bundle();
                    args.putParcelable("recurringSchedule",originalRecurringSchedule);
                    Navigation.findNavController(getView()).navigate(R.id.action_createRecurringFragment_to_recurringTransactionFragment,args);
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void setOptions(boolean isEditing) {
        try {
            deleteMenuItem.setVisible(isEditing);
            showFutureTxnMenuItem.setVisible(isEditing);
        }
        catch (Exception ignored){}
    }
}
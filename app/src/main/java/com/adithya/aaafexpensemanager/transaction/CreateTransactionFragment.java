package com.adithya.aaafexpensemanager.transaction;

import static java.lang.Math.min;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import com.adithya.aaafexpensemanager.category.CategoryViewModel;
import com.adithya.aaafexpensemanager.recenttrans.RecentTransaction;
import com.adithya.aaafexpensemanager.recenttrans.RecentTransactionViewModel;
import com.adithya.aaafexpensemanager.recurring.RecurringSchedule;
import com.adithya.aaafexpensemanager.reusableComponents.reusableDialogs.ConfirmationDialog;
import com.adithya.aaafexpensemanager.reusableComponents.lookupEditText.LookupEditText;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/** @noinspection CallToPrintStackTrace*/
public class CreateTransactionFragment extends Fragment {
    private TransactionViewModel viewModel;
    private AutoCompleteTextView transactionNameTextView;
    private EditText transactionDateEditText;
    private LookupEditText categoryAutoCompleteTextView;
    private List<String> categoryNames;
    private EditText notesEditText;
    private EditText amountEditText;
    private LookupEditText accountNameAutoComplete;
    private LookupEditText toAccountNameAutoComplete;
    private LinearLayout linearLayoutDisableable;
    private FloatingActionButton createTransactionButton;
    private LocalDate transactionDate;
    private Transaction originalTransaction;
    private AccountViewModel accountViewModel;
    private RecentTransactionViewModel recentTransactionViewModel;
    private List<String> accountNames = new ArrayList<>();
    private CategoryViewModel categoryViewModel;
    private ArrayAdapter<String> autoCompleteAdapterRecentTrans;
    private MaterialButton transactionTypeButton;
    private int transactionTypePosition = 0;
    private TextView transactionTypeTextView;
    private final Map<Integer, String> transactionTypeIntKey =
            Map.of(0,"Expense",
                    1,"Income",
                    2,"Transfer");
    private final Map<String,Integer> transactionStringKey =
            Map.of("Expense",0,
                    "Income",1,
                    "Transfer",2);
    private MenuItem deleteMenuItem;
    private MenuItem convertToRecurringMenuItem;
    private boolean isEditing;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_transaction, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);
        categoryViewModel = new ViewModelProvider(requireActivity()).get(CategoryViewModel.class);
        accountViewModel = new ViewModelProvider(requireActivity()).get(AccountViewModel.class);
        recentTransactionViewModel = new ViewModelProvider(requireActivity()).get(RecentTransactionViewModel.class);
        findViewByIdCreateTransaction(view);
        updateTransactionButton();
        setupTransactionTypeButton();
        setupTransactionNameAutoComplete();
        setupCategoryTypeAutocomplete();
        parseArgumentsAndSetFields();
        updateToAccountVisibility();
        setupAccountNameAndToAccountNameAutocomplete();
        setupTransactionDateField();
        setupCreateTransactionButton();
        return view;
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

            transactionNameTextView.setOnItemClickListener((parent, view1, position, id) -> {
                if(originalTransaction!=null){
                    return;
                }
                String selectedRecentTransString = parent.getItemAtPosition(position).toString();
                RecentTransaction selectedRecentTrans = recentTransactionViewModel.getTransactionByName(selectedRecentTransString);
                transactionNameTextView.setText(selectedRecentTrans.transactionName);
                amountEditText.setText(String.valueOf(selectedRecentTrans.amount));
                categoryAutoCompleteTextView.setText(selectedRecentTrans.category);
                notesEditText.setText(selectedRecentTrans.notes);
                accountNameAutoComplete.setText(selectedRecentTrans.accountName);
                toAccountNameAutoComplete.setText(selectedRecentTrans.toAccountName);
                //noinspection DataFlowIssue
                transactionTypePosition = transactionStringKey.get(selectedRecentTrans.transactionType);
                updateTransactionButton();
            });
        });
    }

    private void setupCreateTransactionButton() {
        createTransactionButton.setOnClickListener(v -> {
            String transactionName;
            try {
                transactionName = transactionNameTextView.getText().toString();
                if(transactionName.isBlank()) throw new Exception();
            }
            catch (Exception e){
                e.printStackTrace();
                transactionNameTextView.setError("Please enter not empty transaction name");
                return;
            }
            String transactionType;
            try {
                transactionType = transactionTypeIntKey.get(transactionTypePosition);
            }
            catch (Exception e){
                e.printStackTrace();
                Toast.makeText(requireContext(), "Please select a transaction type", Toast.LENGTH_SHORT).show();
                return;
            }
            String category;
            try{
                //noinspection DataFlowIssue
                category = categoryAutoCompleteTextView.getText().toString();
                if(category.isBlank()) throw new Exception();
                if(!categoryNames.contains(category)) throw new Exception();
            }
            catch (Exception e){
                e.printStackTrace();
                categoryAutoCompleteTextView.setError("Select a valid category from the list");
                return;
            }
            String notes = notesEditText.getText().toString();

            double amount;
            try {
                amount = Double.parseDouble(amountEditText.getText().toString());
            } catch (NumberFormatException e) {
                e.printStackTrace();
                amountEditText.setError("Set a valid value in the amount field");
                return;
            }
            String selectedAccountName;
            try{
                //noinspection DataFlowIssue
                selectedAccountName = accountNameAutoComplete.getText().toString();
                if(selectedAccountName.isBlank()) throw new Exception();
                if(!accountNames.contains(selectedAccountName)) throw new Exception();
            }
            catch (Exception e){
                e.printStackTrace();
                accountNameAutoComplete.setError("Select a valid account from the list");
                return;
            }
            String toAccountName;
            try{
                try {
                    //noinspection DataFlowIssue
                    toAccountName = toAccountNameAutoComplete.getText().toString();
                }
                catch (Exception e){
                    toAccountName = "";
                }
                if("Transfer".equals(transactionType)
                        && (toAccountName.isBlank()
                            || !accountNames.contains(toAccountName)))
                    throw new Exception();
            }
            catch (Exception e){
                e.printStackTrace();
                toAccountNameAutoComplete.setError("Select a valid to account from the list");
                return;
            }
            LocalDate transactionDate;
            try{
                transactionDate = LocalDate.parse(transactionDateEditText.getText().toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
            catch (Exception e){
                e.printStackTrace();
                transactionDateEditText.setError("Select a valid date");
                return;
            }
            if (originalTransaction != null) {
                originalTransaction.transactionName = transactionName;
                originalTransaction.transactionDate = Integer.parseInt(transactionDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
                originalTransaction.transactionType = transactionType;
                originalTransaction.category = category;
                originalTransaction.notes = notes;
                originalTransaction.amount = amount;
                originalTransaction.accountName = selectedAccountName;
                originalTransaction.toAccountName = toAccountName;
                viewModel.updateTransaction(originalTransaction);
                Toast.makeText(requireContext(), "Transaction updated", Toast.LENGTH_SHORT).show();

            } else {
                Transaction transaction = new Transaction(
                        transactionName,
                        transactionDate,
                        transactionType,
                        category,
                        notes,
                        amount,
                        selectedAccountName,
                        toAccountName,
                        transactionType,""
                );

                viewModel.addTransaction(transaction);

                Toast.makeText(requireContext(), "Transaction added", Toast.LENGTH_SHORT).show();

                transactionNameTextView.setText("");
                transactionDateEditText.setText("");
                categoryAutoCompleteTextView.setText("");
                notesEditText.setText("");
                amountEditText.setText("");
                accountNameAutoComplete.setText("");
                toAccountNameAutoComplete.setText("");
            }

            NavHostFragment.findNavController(this).navigate(R.id.action_createTransactionFragment_to_transactionFragment);
        });
    }

    private void setupTransactionTypeButton() {
        transactionTypeButton.setOnClickListener(v -> {
            transactionTypePosition = (transactionTypePosition + 1) % 3;
            updateTransactionButton();
            updateToAccountVisibility();
        });
    }

    private void setupTransactionDateField() {
        transactionDateEditText.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    (view1, year1, monthOfYear, dayOfMonth) -> {
                        transactionDate = LocalDate.of(year1, monthOfYear + 1, dayOfMonth);
                        transactionDateEditText.setText(transactionDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    }, year, month, day);
            datePickerDialog.show();
        });
    }
    /** @noinspection DataFlowIssue*/
    private void parseArgumentsAndSetFields() {
        isEditing = false;
        if (getArguments() != null && getArguments().containsKey("transaction")) {
            //noinspection deprecation
            originalTransaction = getArguments().getParcelable("transaction");
            if (originalTransaction != null) {
                isEditing = true;
                transactionNameTextView.setText(originalTransaction.transactionName);
                transactionDate = originalTransaction.getTransactionLocalDate();
                transactionDateEditText.setText(originalTransaction.getFormattedTransactionDateYYYY_MM_DD());
                amountEditText.setText(String.valueOf(originalTransaction.amount));
                notesEditText.setText(originalTransaction.notes);
                accountNameAutoComplete.setText(originalTransaction.accountName);
                toAccountNameAutoComplete.setText(originalTransaction.toAccountName);
                //noinspection DataFlowIssue
                transactionTypePosition = transactionStringKey.get(originalTransaction.transactionType);
                updateTransactionButton();
            } else {
                transactionTypePosition = transactionStringKey.get("Expense");
                setCurrentDate();
            }
        } else {
            setCurrentDate();
        }
    }

    private void setupAccountNameAndToAccountNameAutocomplete() {
        accountViewModel.getAccounts().observe(getViewLifecycleOwner(), accountNames -> {
            this.accountNames = accountNames.stream().map(account -> account.accountName).collect(Collectors.toList());
            accountNameAutoComplete.setItems(this.accountNames);
            toAccountNameAutoComplete.setItems(this.accountNames);
        });
    }
    private void setupCategoryTypeAutocomplete() {
        categoryViewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            // Efficiently extract category names:
            categoryNames = categories.stream()
                    .map(category -> category.categoryName)
                    .collect(Collectors.toList());
            categoryAutoCompleteTextView.setItems(categoryNames);
            if (originalTransaction != null) { // Editing existing transaction
                String originalCategory = originalTransaction.category;
                if (originalCategory != null && categoryNames != null && categoryNames.contains(originalCategory)) { // Check for nulls and if the list contains the category
                    categoryAutoCompleteTextView.setText(originalCategory); // Set the text
                } else {
                    Log.w("CreateTransactionFragment", "Original category not found in the list.");
                }
            }
        });
    }

    private void findViewByIdCreateTransaction(View view) {
        transactionNameTextView = view.findViewById(R.id.transactionNameTextView);
        transactionDateEditText = view.findViewById(R.id.transactionDateEditText);
        transactionTypeTextView = view.findViewById(R.id.transactionTypeTextView);
        transactionTypeButton = view.findViewById(R.id.transactionTypeButton);
        categoryAutoCompleteTextView = view.findViewById(R.id.categoryAutoCompleteTextView);
        notesEditText = view.findViewById(R.id.notesEditText);
        amountEditText = view.findViewById(R.id.amountEditText);
        accountNameAutoComplete = view.findViewById(R.id.accountNameAutoComplete);
        toAccountNameAutoComplete = view.findViewById(R.id.toAccountNameAutoComplete);
        linearLayoutDisableable = view.findViewById(R.id.linearLayoutDisableable);
        createTransactionButton = view.findViewById(R.id.createTransactionFab);
    }
    private void setCurrentDate() {
        transactionDate = LocalDate.now();
        transactionDateEditText.setText(transactionDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }

    private void updateToAccountVisibility() {
        String transactionTypeString = transactionTypeIntKey.get(transactionTypePosition);
        if ("Transfer".equals(transactionTypeString)) {
            toAccountNameAutoComplete.setVisibility(View.VISIBLE);
            linearLayoutDisableable.setVisibility(View.VISIBLE);
        } else {
            toAccountNameAutoComplete.setVisibility(View.GONE);
            linearLayoutDisableable.setVisibility(View.GONE);
        }
    }
    private void updateTransactionButton() {
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
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.create_transaction_menu, menu);
                deleteMenuItem = menu.findItem(R.id.action_delete_transaction);
                convertToRecurringMenuItem = menu.findItem(R.id.action_convert_to_recurring);
                setOptions(isEditing);
            }
            /** @noinspection DataFlowIssue*/
            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if(menuItem.getItemId()==R.id.action_delete_transaction){
                    new ConfirmationDialog(getContext(),
                            "Delete Transaction",
                            "Are you sure you want to delete this Transaction?",
                            ()-> {
                                viewModel.deleteTransaction(originalTransaction);
                                originalTransaction=null;
                                NavHostFragment.findNavController(getParentFragment()).navigate(R.id.action_createTransactionFragment_to_transactionFragment);
                            },
                            ()->{},
                            "Delete",
                            "Cancel"
                    );
                    return true;
                }
                else if(menuItem.getItemId()==R.id.action_convert_to_recurring){
                    Bundle args = new Bundle();
                    args.putParcelable("recurringSchedule",new RecurringSchedule(originalTransaction));
                    args.putBoolean("isEditing",false);
                    Navigation.findNavController(getView()).navigate(R.id.nav_create_recurring,args);
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void setOptions(boolean isEditing) {
        try {
            deleteMenuItem.setVisible(isEditing);
            convertToRecurringMenuItem.setVisible(isEditing);
        }
        catch (Exception ignored){}
    }
}
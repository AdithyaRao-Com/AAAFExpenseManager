package com.adithya.aaafexpensemanager.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.adithya.aaafexpensemanager.R;
import com.adithya.aaafexpensemanager.reusableComponents.calculatorEditText.CalculatorEditText;
import com.adithya.aaafexpensemanager.reusableComponents.lookupAutoCompleteList.LookupAutoCompleteList;
import com.adithya.aaafexpensemanager.reusableComponents.lookupEditText.LookupEditText;
import com.adithya.aaafexpensemanager.reusableComponents.reusableDialogs.ConfirmationDialog;
import com.adithya.aaafexpensemanager.settings.accounttype.AccountTypeViewModel;
import com.adithya.aaafexpensemanager.settings.currency.CurrencyViewModel;
import com.adithya.aaafexpensemanager.transaction.Transaction;
import com.adithya.aaafexpensemanager.transactionFilter.TransactionFilter;
import com.adithya.aaafexpensemanager.util.GsonListStringConversion;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class CreateAccountFragment extends Fragment {
    CheckBox updateBalanceCheckBox;
    private EditText accountNameEditText;
    private CalculatorEditText accountBalanceEditText;
    private LookupAutoCompleteList accountTagsEditText;
    private FloatingActionButton createAccountButton;
    private LookupEditText accountTypeSpinner;
    private EditText displayOrderEditText;
    private LookupEditText currencyCodeEditText;
    private CheckBox closeAccountCheckBox;
    private CheckBox doNotShowInDropdownCheckBox;
    private AccountViewModel viewModel;
    private boolean isEditing = false;
    private Account originalAccount;
    private MenuItem deleteMenuItem;
    private MenuItem showTransactionsMenuItem;

    /**
     * @noinspection DataFlowIssue
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_account, container, false);
        findViewByIdSetup(view);
        viewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        AccountTypeViewModel accountTypeViewModel = new ViewModelProvider(this).get(AccountTypeViewModel.class);
        List<String> accountTypes = accountTypeViewModel.getAccountTypes().stream().map(accountType -> accountType.accountType).collect(Collectors.toList());
        accountTypeSpinner.setItemStrings(accountTypes);
        CurrencyViewModel currencyViewModel = new ViewModelProvider(this).get(CurrencyViewModel.class);
        List<String> currencies = currencyViewModel
                .getCurrencies()
                .getValue()
                .stream()
                .map(curr -> curr.currencyName)
                .toList();
        currencyCodeEditText.setItemStrings(currencies);
        Bundle args = getArguments();
        getArgumentsAndSetFields(args);
        updateBalanceCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            accountBalanceEditText.setEnabled(isChecked);
            accountBalanceEditText.setFocusable(isChecked);
            accountBalanceEditText.setFocusableInTouchMode(isChecked);
        });
        List<String> accountTags = viewModel.getAccountTags();
        accountTagsEditText.setPromptList(accountTags);
        createAccountButton.setOnClickListener(v -> {
            String name = accountNameEditText.getText().toString();
            if (name.isBlank()) {
                accountNameEditText.setError("Account name is required");
                return;
            }
            String type;
            try {
                //noinspection DataFlowIssue
                type = accountTypeSpinner.getText().toString();
                if (type.isBlank()) throw new Exception();
            } catch (Exception e) {
                accountTypeSpinner.setError("Select a valid account type from the list");
                return;
            }
            String balanceStr = accountBalanceEditText.getText().toString();
            List<String> tagsList = accountTagsEditText.getSelectedItems();
            String tags = GsonListStringConversion.listToJson(tagsList);
            int displayOrder;
            try {
                displayOrder = Integer.parseInt(displayOrderEditText.getText().toString());
            } catch (Exception e) {
                displayOrderEditText.setError("Invalid display order");
                return;
            }
            String currencyCode = currencyCodeEditText.getText().toString();
            if (currencyCode.isBlank()) {
                currencyCode = currencyViewModel.getPrimaryCurrency();
            }
            double balance;
            try {
                balance = Double.parseDouble(balanceStr);
            } catch (NumberFormatException e) {
                accountBalanceEditText.setError("Invalid account balance");
                return;
            }
            boolean closeAccountInd = closeAccountCheckBox.isChecked();
            boolean doNotShowInDropdownInd = doNotShowInDropdownCheckBox.isChecked();
            Account account = new Account(name, type, balance, tags, displayOrder, currencyCode, closeAccountInd, doNotShowInDropdownInd);
            if (isEditing) {
                originalAccount = viewModel.getAccountByName(originalAccount.accountName);
                double difference = balance - originalAccount.accountBalance;

                if (difference != 0 && updateBalanceCheckBox.isChecked()) {
                    // Add transaction
                    String transactionType = (difference > 0) ? "Income" : "Expense";
                    Transaction transaction = new Transaction(
                            "Balance Adjustment",
                            LocalDate.now(),
                            transactionType,
                            "Adjustments",
                            null,
                            Math.abs(difference),
                            name,
                            null,
                            transactionType,
                            null
                    );
                    viewModel = new ViewModelProvider(this).get(AccountViewModel.class);
                    viewModel.addTransaction(transaction);
                }
                viewModel.updateAccount(account);
                Snackbar.make(this.getView(), "Account updated successfully", Snackbar.LENGTH_SHORT).show();
            } else {
                viewModel.createAccount(account);
                // Clear the input fields after creating the account
                accountNameEditText.setText("");
                accountTypeSpinner.setText("");
                accountBalanceEditText.setText("0");
                accountTagsEditText.setText("");
                displayOrderEditText.setText("0");
                currencyCodeEditText.setText("");
                Snackbar.make(this.getView(), "Account created successfully", Snackbar.LENGTH_SHORT).show();
            }
            Navigation.findNavController(v).navigate(R.id.nav_account);
        });
        return view;
    }

    /**
     * @noinspection deprecation
     */
    private void getArgumentsAndSetFields(Bundle args) {
        if (args != null && args.containsKey("account")) {
            isEditing = true;
            originalAccount = args.getParcelable("account");
            if (originalAccount == null) {
                throw new RuntimeException("Original Account cannot be null");
            }
            accountNameEditText.setText(originalAccount.accountName);
            accountBalanceEditText.setText(String.valueOf(originalAccount.accountBalance));
//            accountTagsEditText.setText(originalAccount.accountTags);
            accountTagsEditText.setSelectedItems(
                    GsonListStringConversion.jsonToList(
                            originalAccount.accountTags));
            accountTypeSpinner.setText(originalAccount.accountType);
            displayOrderEditText.setText(String.valueOf(originalAccount.displayOrder));
            closeAccountCheckBox.setChecked(originalAccount.closeAccountInd);
            doNotShowInDropdownCheckBox.setChecked(originalAccount.doNotShowInDropdownInd);
            currencyCodeEditText.setText(originalAccount.currencyCode);
            accountBalanceEditText.setEnabled(false);
        } else {
            isEditing = false;
            accountBalanceEditText.setText("0");
            accountBalanceEditText.setEnabled(false);
            updateBalanceCheckBox.setVisibility(View.GONE);
        }
        setOptions(isEditing);
    }

    private void findViewByIdSetup(View view) {
        updateBalanceCheckBox = view.findViewById(R.id.updateBalanceCheckBox);
        accountNameEditText = view.findViewById(R.id.accountNameEditText);
        accountBalanceEditText = view.findViewById(R.id.accountBalanceEditText);
        accountTagsEditText = view.findViewById(R.id.accountTagsEditText);
        createAccountButton = view.findViewById(R.id.createAccountFab);
        accountTypeSpinner = view.findViewById(R.id.accountTypeSpinner);
        displayOrderEditText = view.findViewById(R.id.displayOrder);
        currencyCodeEditText = view.findViewById(R.id.currencyCodeEditText);
        closeAccountCheckBox = view.findViewById(R.id.closeAccountCheckBox);
        doNotShowInDropdownCheckBox = view.findViewById(R.id.doNotShowInDropdownCheckBox);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.create_account_menu, menu);
                deleteMenuItem = menu.findItem(R.id.action_delete_account);
                showTransactionsMenuItem = menu.findItem(R.id.action_show_account_transactions);
                setOptions(isEditing);
            }

            /** @noinspection DataFlowIssue*/
            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_delete_account) {
                    new ConfirmationDialog(getContext(),
                            "Delete Account",
                            "Are you sure you want to delete this account?",
                            () -> {
                                viewModel.deleteAccount(originalAccount.accountName);
                                Snackbar.make(view, "Account deleted successfully", Snackbar.LENGTH_SHORT).show();
                                Navigation.findNavController(getView()).navigate(R.id.nav_account);
                            },
                            () -> {
                            },
                            "Delete",
                            "Cancel"
                    );
                    return true;
                } else if (menuItem.getItemId() == R.id.action_show_account_transactions) {
                    Bundle args = new Bundle();
                    TransactionFilter accountFilter = new TransactionFilter();
                    ArrayList<String> accountNames = new ArrayList<>();
                    accountNames.add(originalAccount.accountName);
                    accountFilter.accountNames = accountNames;
                    args.putParcelable("transaction_filter", accountFilter);
                    Navigation.findNavController(getView()).navigate(R.id.nav_transaction, args);
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void setOptions(boolean isEditing) {
        try {
            deleteMenuItem.setVisible(isEditing);
            showTransactionsMenuItem.setVisible(isEditing);
        } catch (Exception ignored) {
        }
    }
}
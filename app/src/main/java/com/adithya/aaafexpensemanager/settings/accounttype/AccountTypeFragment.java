package com.adithya.aaafexpensemanager.settings.accounttype;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adithya.aaafexpensemanager.R;
import com.adithya.aaafexpensemanager.transactionFilter.TransactionFilter;

import java.util.ArrayList;
import java.util.List;

public class AccountTypeFragment extends Fragment {

    private AccountTypeViewModel accountTypeViewModel;
    private AccountTypesAdapter adapter;
    private AccountType selectedAccountType;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_account_type, container, false);
        RecyclerView accountTypesRecyclerView = view.findViewById(R.id.accountTypesRecyclerView);
        accountTypesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        accountTypeViewModel = new ViewModelProvider(requireActivity()).get(AccountTypeViewModel.class);
        adapter = new AccountTypesAdapter(new ArrayList<>());
        accountTypesRecyclerView.setAdapter(adapter);
        adapter.setAccountTypes(accountTypeViewModel.getAccountTypes());
        adapter.notifyDataSetChanged();
        EditText searchEditText = view.findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.setAccountTypes(accountTypeViewModel.filterAccountTypes(s.toString()));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        return view;
    }

    /**
     * @noinspection DataFlowIssue
     */
    private void showAccountTypeOptionsDialog(AccountType accountType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(accountType.accountType);
        String[] options = {"Edit Details", "Delete Account Type", "Show Transactions"};
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                selectedAccountType = accountType;
                Bundle args = new Bundle();
                args.putParcelable("accountType", selectedAccountType);
                Navigation.findNavController(requireView()).navigate(R.id.action_accountTypeFragment_to_createAccountTypeFragment, args);
            } else if (which == 1) {
                showDeleteConfirmationDialog(accountType);
            } else if (which == 2) {
                Bundle args = new Bundle();
                TransactionFilter accountFilter = new TransactionFilter();
                ArrayList<String> accountTypes = new ArrayList<>();
                accountTypes.add(accountType.accountType);
                accountFilter.accountTypes = accountTypes;
                args.putParcelable("transaction_filter", accountFilter);
                Navigation.findNavController(this.getView()).navigate(R.id.nav_transaction, args);
            }
        });
        builder.show();
    }

    /**
     * @noinspection DataFlowIssue
     */
    @SuppressLint("NotifyDataSetChanged")
    private void showDeleteConfirmationDialog(AccountType accountType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete Category");
        builder.setMessage("Are you sure you want to delete this account type?");

        builder.setPositiveButton("Delete", (dialog, which) -> {
            accountTypeViewModel.deleteAccountType(accountType.accountType);
            Toast.makeText(getContext(), "Account Type deleted", Toast.LENGTH_SHORT).show();
            adapter.setAccountTypes(accountTypeViewModel.getAccountTypes());
            adapter.notifyDataSetChanged();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // Do nothing (close the dialog)
        });
        builder.show();
    }

    private class AccountTypesAdapter extends RecyclerView.Adapter<AccountTypesAdapter.AccountTypeViewHolder> {
        private List<AccountType> accountTypes;

        public AccountTypesAdapter(List<AccountType> accountTypes) {
            this.accountTypes = accountTypes;
        }

        public void setAccountTypes(List<AccountType> accountTypes) {
            this.accountTypes = accountTypes;
        }

        @NonNull
        @Override
        public AccountTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_account_type, parent, false);
            return new AccountTypeViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AccountTypeViewHolder holder, int position) {
            AccountType accountType = accountTypes.get(position);
            holder.accountTypeTextView.setText(accountType.accountType);
            holder.accountTypeDisplayOrderTextView.setText(String.valueOf(accountType.accountTypeDisplayOrder));
            holder.itemView.setOnClickListener(v -> {
                selectedAccountType = accountType;
                showAccountTypeOptionsDialog(selectedAccountType);
            });
        }

        @Override
        public int getItemCount() {
            return accountTypes.size();
        }

        public class AccountTypeViewHolder extends RecyclerView.ViewHolder {
            private final android.widget.TextView accountTypeTextView;
            private final android.widget.TextView accountTypeDisplayOrderTextView;

            public AccountTypeViewHolder(@NonNull View itemView) {
                super(itemView);
                accountTypeTextView = itemView.findViewById(R.id.accountTypeNameTextView);
                accountTypeDisplayOrderTextView = itemView.findViewById(R.id.accountTypeDisplayOrder);
            }
        }
    }
}


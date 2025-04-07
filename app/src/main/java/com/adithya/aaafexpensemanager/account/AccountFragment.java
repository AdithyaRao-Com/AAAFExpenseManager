package com.adithya.aaafexpensemanager.account;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.adithya.aaafexpensemanager.R;
import com.adithya.aaafexpensemanager.databinding.FragmentListAccountBinding;
import com.adithya.aaafexpensemanager.reusableComponents.reusableDialogs.ConfirmationDialog;

import java.util.ArrayList;

public class AccountFragment extends Fragment {
    private FragmentListAccountBinding binding;
    private AccountViewModel viewModel;
    private AccountsAdapter adapter;
    private boolean showClosedAccounts = false;
    private MenuItem showHideClosedAccountsMenuItem;
    private MenuItem createNewAccountMenuItem;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentListAccountBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        viewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AccountsAdapter(new ArrayList<>(),
                getContext(),
                this::changeNavigation);
        binding.recyclerView.setAdapter(adapter);

        // Observe LiveData for accounts from ViewModel
        viewModel.getAccounts().observe(getViewLifecycleOwner(), accountNames -> adapter.setAccounts(accountNames));

        binding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.filterAccounts(s.toString()); //Triggers transformation
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.setShowClosedAccounts(showClosedAccounts);
        viewModel.loadAccountNames();
    }

    public void changeNavigation(int idNavigate, Bundle args) {
        NavHostFragment.findNavController(this)
                .navigate(idNavigate, args);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.list_account_menu, menu);
                showHideClosedAccountsMenuItem = menu.findItem(R.id.actionShowHideClosedAccountsMenuItem);
                createNewAccountMenuItem = menu.findItem(R.id.createNewAccountMenuItem);
            }

            /** */
            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.actionShowHideClosedAccountsMenuItem) {
                    String title = showClosedAccounts ? "Hide Closed Accounts" : "Show Closed Accounts";
                    new ConfirmationDialog(getContext(),
                            title,
                            "Are you sure you want to " + title,
                            () -> {
                                showClosedAccounts = !showClosedAccounts;
                                viewModel.setShowClosedAccounts(showClosedAccounts);
                                viewModel.loadAccountNames();
                            },
                            () -> {
                            },
                            "Yes",
                            "No"
                    );
                    return true;
                }
                else if (menuItem.getItemId() == R.id.createNewAccountMenuItem) {
                    changeNavigation(R.id.nav_create_account, null);
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }
}
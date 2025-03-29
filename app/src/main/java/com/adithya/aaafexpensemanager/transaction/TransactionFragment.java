package com.adithya.aaafexpensemanager.transaction;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adithya.aaafexpensemanager.R;
import com.adithya.aaafexpensemanager.transactionFilter.TransactionFilter;
import com.adithya.aaafexpensemanager.transactionFilter.TransactionFilterDialog;

import java.util.ArrayList;
import java.util.List;


/**
 * @noinspection deprecation
 */
public class TransactionFragment extends Fragment implements TransactionFilterDialog.OnFilterAppliedListener {
    public TransactionFilter transactionFilter;
    public int currentPage = 1;
    private TransactionViewModel viewModel;
    private RecyclerView transactionsRecyclerView;
    private Button filterButton;
    private TransactionsAdapter adapter;
    private boolean isLoading = false;

    /**
     * @noinspection deprecation
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_transaction, container, false);
        transactionsRecyclerView = view.findViewById(R.id.transactionsRecyclerView);
        transactionsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        EditText searchEditText = view.findViewById(R.id.searchEditText);
        filterButton = view.findViewById(R.id.button);
        transactionFilter = new TransactionFilter();
        if (getArguments() != null && getArguments().containsKey("transaction_filter")) {
            if (getArguments().getParcelable("transaction_filter") != null) {
                transactionFilter = getArguments().getParcelable("transaction_filter");
            } else {
                transactionFilter = new TransactionFilter();
            }
        } else {
            transactionFilter = new TransactionFilter();
        }
        checkSetToggleFilter();
        viewModel = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);
        transactionsRecyclerView = view.findViewById(R.id.transactionsRecyclerView);
        transactionsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new TransactionsAdapter(new ArrayList<>(),
                this,
                viewModel);
        transactionsRecyclerView.setAdapter(adapter);
        updateRecyclerView(viewModel.getTransactions(transactionFilter, currentPage).getValue());
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = s.toString();
                if (searchText.isEmpty()) {
                    transactionFilter.searchText = "";
                    currentPage = 1;
                    viewModel.getTransactions(transactionFilter, currentPage).getValue();
                } else {
                    transactionFilter.searchText = searchText;
                    currentPage = 1;
                    updateRecyclerView(viewModel.getTransactions(transactionFilter, currentPage).getValue());
                }
                checkSetToggleFilter();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        transactionsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if (!isLoading && (firstVisibleItemPosition + visibleItemCount >= totalItemCount) && totalItemCount > 0) {
                        isLoading = true;
                        currentPage++;
                        updateRecyclerView(viewModel.getTransactions(transactionFilter, currentPage).getValue());
                        isLoading = false;
                    }
                }
            }
        });
        filterButton.setOnClickListener((view1) -> new TransactionFilterDialog(requireContext(), requireActivity(), transactionFilter, this, true).showDialog());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        currentPage = 1;
        updateRecyclerView(viewModel.getTransactions(transactionFilter, currentPage).getValue());
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateRecyclerView(List<Transaction> transactions) {
        if (adapter == null) {
            adapter = new TransactionsAdapter(transactions, this, viewModel);
            transactionsRecyclerView.setAdapter(adapter);
        } else if (currentPage == 1) {
            adapter.setTransactions(transactions);
        } else {
            adapter.addTransactions(transactions);
        }
    }

    @Override
    public void onFilterApplied(TransactionFilter filter) {
        this.transactionFilter = filter;
        currentPage = 1;
        updateRecyclerView(viewModel.getTransactions(transactionFilter, currentPage).getValue());
        checkSetToggleFilter();
    }

    public void reloadData() {
        currentPage = 1;
        updateRecyclerView(viewModel.getTransactions(transactionFilter, currentPage).getValue());
        checkSetToggleFilter();
    }

    private void checkSetToggleFilter() {
        if (filterButton == null) {
            //noinspection DataFlowIssue
            filterButton = getView().findViewById(R.id.button);
        }
        if (transactionFilter != null && !transactionFilter.isEmpty()) {
            filterButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.filter_enabled)));
        } else {
            filterButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.filter_disabled)));
        }
    }
}
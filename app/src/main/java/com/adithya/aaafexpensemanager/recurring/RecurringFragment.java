package com.adithya.aaafexpensemanager.recurring;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adithya.aaafexpensemanager.R;
import com.adithya.aaafexpensemanager.transactionFilter.TransactionFilter;

import java.util.ArrayList;
import java.util.List;


public class RecurringFragment extends Fragment {
    private RecurringViewModel viewModel;
    private final TransactionFilter transactionFilter = new TransactionFilter();
    private RecyclerView recurringRecyclerView;
    private RecurringAdapter adapter;
    public int currentPage = 1;
    private boolean isLoading = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_recurring, container, false);
        recurringRecyclerView = view.findViewById(R.id.recurringRecyclerView);
        recurringRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        EditText searchEditText = view.findViewById(R.id.searchEditText);
        viewModel = new ViewModelProvider(requireActivity()).get(RecurringViewModel.class);
        adapter = new RecurringAdapter(new ArrayList<>(),
                this,
                viewModel);
        recurringRecyclerView.setAdapter(adapter);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = s.toString();
                if (searchText.isEmpty()) {
                    transactionFilter.searchText = "";
                    currentPage=1;
                    viewModel.getRecurringSchedules(transactionFilter,currentPage).observe(getViewLifecycleOwner(), transactions -> updateRecyclerView(transactions));
                } else {
                    transactionFilter.searchText = searchText;
                    currentPage=1;
                    viewModel.getRecurringSchedules(transactionFilter,currentPage);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        recurringRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                        updateRecyclerView(viewModel.getRecurringSchedules(transactionFilter,currentPage).getValue());
                        isLoading = false;
                    }
                }
            }
        });
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        currentPage=1;
        viewModel.getRecurringSchedules(transactionFilter,currentPage).observe(getViewLifecycleOwner(), this::updateRecyclerView);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateRecyclerView(List<RecurringSchedule> recurringSchedules) {
        if (adapter == null || currentPage==1) {
            adapter = new RecurringAdapter(recurringSchedules,
                    this,
                    viewModel);
            recurringRecyclerView.setAdapter(adapter);
        } else {
            adapter.addRecurringSchedules(recurringSchedules);
            adapter.notifyDataSetChanged();
        }
    }
}
package com.adithya.aaafexpensemanager.transactionFilter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adithya.aaafexpensemanager.R;
import com.adithya.aaafexpensemanager.util.AppConstants;

import java.util.ArrayList;
import java.util.List;

public class TransactionFilterListFragment extends Fragment {
    private TransactionFilterViewModel viewModel;
    private TransactionFilterAdapter adapter;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_transaction_filter, container, false);
        RecyclerView transactionFilterRecyclerView = view.findViewById(R.id.transactionFilterRecyclerView);
        transactionFilterRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        viewModel = new ViewModelProvider(requireActivity()).get(TransactionFilterViewModel.class);
        adapter = new TransactionFilterAdapter(new ArrayList<>(),this.requireContext());
        transactionFilterRecyclerView.setAdapter(adapter);
        viewModel.getTransactionFilters().observe(getViewLifecycleOwner(), transactionFilters -> {
            adapter.setTransactionFilters(transactionFilters);
            adapter.notifyDataSetChanged();
        });
        return view;
    }

    private class TransactionFilterAdapter extends RecyclerView.Adapter<TransactionFilterAdapter.TransactionFilterViewHolder> {
        private List<TransactionFilter> transactionFilters;
        private final Context context;

        public TransactionFilterAdapter(List<TransactionFilter> transactionFilters, Context context) {
            this.transactionFilters = transactionFilters;
            this.context = context;
        }

        public void setTransactionFilters(List<TransactionFilter> categories) {
            this.transactionFilters = categories;
        }

        @NonNull
        @Override
        public TransactionFilterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_transaction_filter, parent, false);
            return new TransactionFilterViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TransactionFilterViewHolder holder, int position) {
            TransactionFilter transactionFilter = transactionFilters.get(position);
            holder.reportNameTextView.setText(transactionFilter.reportName);
            holder.reportTypeTextView.setText(transactionFilter.reportType);
            holder.itemView.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putParcelable("transactionFilter", transactionFilter);
                if (transactionFilter.reportType.equals(AppConstants.REPORT_TYPE_CATEGORY_SUMMARY)){
                    Navigation.findNavController(v)
                            .navigate(R.id.action_transactionFilterListFragment_to_categorySummaryFragment,bundle);
                }else{
                    Navigation.findNavController(v)
                            .navigate(R.id.action_transactionFilterListFragment_to_balanceForecastFragment,bundle);
                }
            });
            holder.itemView.setOnLongClickListener(v -> {
                List<String> options = List.of("Edit Item","Delete Item","Run Report");
                showSubItemsDialog(v, options,transactionFilter);
                return true;
            });
        }
        private void showSubItemsDialog(View anchorView, List<String> subItems,TransactionFilter transactionFilter) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
            builder.setTitle("Select an option");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this.context, android.R.layout.simple_list_item_1, subItems);
            builder.setAdapter(adapter, (dialog, which) -> {
                String selectedItem = subItems.get(which);
                switch (selectedItem) {
                    case "Edit Item": {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("transactionFilter", transactionFilter);
                        Navigation.findNavController(anchorView)
                                .navigate(R.id.action_transactionFilterListFragment_to_createTransactionFilterFragment, bundle);
                        break;
                    }
                    case "Run Report": {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("transactionFilter", transactionFilter);
                        if (transactionFilter.reportType.equals(AppConstants.REPORT_TYPE_CATEGORY_SUMMARY)) {
                            Navigation.findNavController(anchorView)
                                    .navigate(R.id.action_transactionFilterListFragment_to_categorySummaryFragment, bundle);
                        } else {
                            Navigation.findNavController(anchorView)
                                    .navigate(R.id.action_transactionFilterListFragment_to_balanceForecastFragment, bundle);
                        }
                        break;
                    }
                    case "Delete Item":
                        TransactionFilterViewModel viewModel = new ViewModelProvider(requireActivity()).get(TransactionFilterViewModel.class);
                        viewModel.deleteTransactionFilter(transactionFilter);
                        break;
                }
            });
            builder.create().show();
        }

        @Override
        public int getItemCount() {
            return transactionFilters.size();
        }
        public class TransactionFilterViewHolder extends RecyclerView.ViewHolder {
            public android.widget.TextView reportNameTextView;
            public android.widget.TextView reportTypeTextView;

            public TransactionFilterViewHolder(@NonNull View itemView) {
                super(itemView);
                reportNameTextView = itemView.findViewById(R.id.reportNameTextView);
                reportTypeTextView = itemView.findViewById(R.id.reportTypeTextView);
            }
        }
    }
}

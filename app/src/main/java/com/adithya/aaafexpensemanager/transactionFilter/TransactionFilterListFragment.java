package com.adithya.aaafexpensemanager.transactionFilter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adithya.aaafexpensemanager.R;

import java.util.ArrayList;
import java.util.List;

public class TransactionFilterListFragment extends Fragment {
    // TODO - Add onClickListener on the recycler view item to trigger report
    // TODO - Add long press to Modify/Delete report
    // TODO - Add delete/run report in the edit transaction filter dialog screen as well
    private TransactionFilterViewModel viewModel;
    private TransactionFilterAdapter adapter;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_transaction_filter, container, false);
        RecyclerView transactionFilterRecyclerView = view.findViewById(R.id.transactionFilterRecyclerView);
        transactionFilterRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        viewModel = new ViewModelProvider(requireActivity()).get(TransactionFilterViewModel.class);
        adapter = new TransactionFilterAdapter(new ArrayList<>());
        transactionFilterRecyclerView.setAdapter(adapter);
        viewModel.getTransactionFilters().observe(getViewLifecycleOwner(), transactionFilters -> {
            adapter.setTransactionFilters(transactionFilters);
            adapter.notifyDataSetChanged();
        });
        return view;
    }

    private static class TransactionFilterAdapter extends RecyclerView.Adapter<TransactionFilterAdapter.TransactionFilterViewHolder> {
        private List<TransactionFilter> transactionFilters;

        public TransactionFilterAdapter(List<TransactionFilter> transactionFilters) {
            this.transactionFilters = transactionFilters;
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
        }

        @Override
        public int getItemCount() {
            return transactionFilters.size();
        }

        public static class TransactionFilterViewHolder extends RecyclerView.ViewHolder {
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

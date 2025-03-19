package com.adithya.aaafexpensemanager.futureTransaction;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.adithya.aaafexpensemanager.R;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** @noinspection deprecation*/
public class FutureTransactionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_DATE = 0;
    private static final int TYPE_TRANSACTION = 1;
    private final List<Object> items;
    private final DateTimeFormatter dateFormatter;
    private ActionMode actionMode;
    private final FutureTransactionViewModel viewModel;
    private final FutureTransactionFragment transactionFragment;
    private final List<FutureTransaction> selectedTransactions = new ArrayList<>();
    public FutureTransactionsAdapter(List<FutureTransaction> transactions,
                               FutureTransactionFragment transactionFragment,
                               FutureTransactionViewModel viewModel) {
        this.items = new ArrayList<>();
        this.dateFormatter = DateTimeFormatter.ofPattern("E dd MMM yyyy");
        this.transactionFragment = transactionFragment;
        this.viewModel = viewModel;
        addSeparators(transactions);
    }
    @Override
    public int getItemViewType(int position) {
        return items.get(position) instanceof LocalDate ? TYPE_DATE : TYPE_TRANSACTION;
    }
    public void setTransactions(List<FutureTransaction> transactions) {
        items.clear();
        addTransactions(transactions);
    }
    @SuppressLint("NotifyDataSetChanged")
    public void addTransactions(List<FutureTransaction> transactions) {
        addSeparators(transactions);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_DATE) {
            View view = inflater.inflate(R.layout.list_date_separator, parent, false);
            return new DateViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_transaction, parent, false);
            return new TransactionViewHolder(view);
        }
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = items.get(position);
        if (holder instanceof DateViewHolder) {
            DateViewHolder dateViewHolder = (DateViewHolder) holder;
            LocalDate date = (LocalDate) item;
            dateViewHolder.dateTextView.setText(dateFormatter.format(date));
        } else if (holder instanceof TransactionViewHolder) {
            TransactionViewHolder transactionViewHolder = (TransactionViewHolder) holder;
            FutureTransaction transaction = (FutureTransaction) item;
            if (transaction != null) {
                setUpTransactionViewHolder(transactionViewHolder, position, transaction);
            }
        }
    }

    /** @noinspection deprecation*/
    @SuppressLint("UseCompatLoadingForDrawables")
    private void setUpTransactionViewHolder(@NonNull TransactionViewHolder holder, int position, FutureTransaction futureTransaction) {
        holder.transactionNameTextView.setText(futureTransaction.transactionName);
        holder.transactionDateTextView.setText(futureTransaction.getFormattedTransactionDate());
        holder.amountTextView.setText(futureTransaction.amountToIndianFormat());
        holder.accountNameTextView.setText(futureTransaction.accountName);
        holder.categoryNameTextView.setText(futureTransaction.category);
        String transactionType = futureTransaction.transactionType;
        int amountColor;
        if ("Income".equals(transactionType)) {
            amountColor = transactionFragment.getResources().getColor(android.R.color.holo_green_dark);
        } else if ("Expense".equals(transactionType)) {
            amountColor = transactionFragment.getResources().getColor(android.R.color.holo_red_dark);
        } else {
            amountColor = transactionFragment.getResources().getColor(android.R.color.black);
        }
        holder.amountTextView.setTextColor(amountColor);


        String transferInd = futureTransaction.transferInd;
        if ("Income".equals(transferInd)) {
            amountColor = transactionFragment.getResources().getColor(android.R.color.holo_green_dark);
        } else if ("Expense".equals(transferInd)) {
            amountColor = transactionFragment.getResources().getColor(android.R.color.holo_red_dark);
        } else if ("Transfer".equals(transferInd)) {
            amountColor = transactionFragment.getResources().getColor(android.R.color.holo_blue_dark);
        } else {
            amountColor = transactionFragment.getResources().getColor(android.R.color.black);
        }
        holder.transferIndImageView.setBackgroundColor(amountColor);

        holder.itemView.setOnClickListener(v -> {
            if (actionMode != null) {
                toggleSelection(holder, position);
            } else {
                Bundle args = new Bundle();
                args.putParcelable("futureTransaction", futureTransaction);
                NavHostFragment.findNavController(transactionFragment)
                        .navigate(R.id.action_recurringTransactionFragment_to_updateFutureTransactionFragment, args);
            }
        });
        holder.itemView.setOnLongClickListener(v -> {
            if (actionMode == null) {
                startActionMode();
                toggleSelection(holder, position);
            }
            return true;
        });
        holder.transactionItemContainer.setBackgroundColor(selectedTransactions.contains((FutureTransaction) items.get(position))
                ? this.transactionFragment.getResources().getColor(R.color.selected_item_color)
                : Color.TRANSPARENT);
    }

    private void addSeparators(List<FutureTransaction> transactions) {
        LocalDate lastDate = null;
        for (FutureTransaction transaction : transactions) {
            if (lastDate == null || !transaction.getTransactionLocalDate().equals(lastDate)) {
                items.add(transaction.getTransactionLocalDate());
                lastDate = transaction.getTransactionLocalDate();
            }
            items.add(transaction);
        }
    }

    /** @noinspection deprecation*/
    private void toggleSelection(TransactionViewHolder holder, int position) {
        FutureTransaction transaction = (FutureTransaction) items.get(position);
        if (selectedTransactions.contains(transaction)) {
            selectedTransactions.remove(transaction);
            holder.transactionItemContainer.setBackgroundColor(Color.TRANSPARENT);
        } else {
            selectedTransactions.add(transaction);
            holder.transactionItemContainer.setBackgroundColor(this.transactionFragment.getResources().getColor(R.color.selected_item_color));
        }

        if (selectedTransactions.isEmpty()) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(selectedTransactions.size()));
        }
    }

    /** @noinspection DataFlowIssue*/
    private void selectAll(List<Object> items) {
        RecyclerView recyclerView = transactionFragment.getView().findViewById(R.id.transactionsRecyclerView);
        selectedTransactions.clear();
        int itemPosition = 0;
        for (Object listObject:items) {
            if(listObject instanceof FutureTransaction){
                FutureTransaction transaction = (FutureTransaction) listObject;
                selectedTransactions.add(transaction);
                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(itemPosition);
                if (viewHolder != null) {
                    TransactionViewHolder myViewHolder = (TransactionViewHolder) viewHolder;
                    myViewHolder.transactionItemContainer.setBackgroundColor(this.transactionFragment.getResources().getColor(R.color.selected_item_color));
                }
            }
            itemPosition++;
        }
        if (selectedTransactions.isEmpty()) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(selectedTransactions.size()));
        }
    }
    private void deSelectAll() {
        selectedTransactions.clear();
        actionMode.finish();
    }
    private void deleteSelectedTransactions() {
        if (!selectedTransactions.isEmpty()) {
            for (FutureTransaction transaction : selectedTransactions) {
                viewModel.deleteFutureTransaction(transaction);
            }

            Toast.makeText(this.transactionFragment.getContext(), selectedTransactions.size() + " transactions deleted", Toast.LENGTH_SHORT).show();
            selectedTransactions.clear();
            this.transactionFragment.currentPage=1;
            viewModel.getFutureTransactions(this.transactionFragment.transactionFilter,
                            this.transactionFragment.currentPage)
                    .observe(this.transactionFragment.getViewLifecycleOwner(), this.transactionFragment::updateRecyclerView);
        }
    }
    private void startActionMode() {
        //noinspection DataFlowIssue
        actionMode = this.transactionFragment.getActivity().startActionMode(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.future_transaction_multiselect_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                if (item.getItemId() == R.id.action_delete) {
                    long selectionCount = selectedTransactions.size();
                    deleteSelectedTransactions();
                    mode.finish();
                    return true;
                }
                else if(item.getItemId() == R.id.action_select_all){
                    selectAll(items);
                }
                else if(item.getItemId() == R.id.action_deselect_all){
                    deSelectAll();
                }
                return false;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDestroyActionMode(ActionMode mode) {
                actionMode = null;
                selectedTransactions.clear();
                notifyDataSetChanged();
            }
        });
    }
    @Override
    public int getItemCount() {
        return items.size();
    }
    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView transactionNameTextView;
        TextView transactionDateTextView;
        TextView amountTextView;
        TextView accountNameTextView;
        LinearLayout transactionItemContainer;
        View transferIndImageView;
        TextView categoryNameTextView;
        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            transactionNameTextView = itemView.findViewById(R.id.transactionNameTextView);
            transactionDateTextView = itemView.findViewById(R.id.transactionDateTextView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            accountNameTextView = itemView.findViewById(R.id.accountNameTextView);
            transactionItemContainer = itemView.findViewById(R.id.transaction_item_container);
            transferIndImageView = itemView.findViewById(R.id.transaction_icon);
            categoryNameTextView = itemView.findViewById(R.id.categoryNameTextView);
        }
    }
    public static class DateViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.list_date_separator_text); // Replace with your date TextView ID
        }
    }
}
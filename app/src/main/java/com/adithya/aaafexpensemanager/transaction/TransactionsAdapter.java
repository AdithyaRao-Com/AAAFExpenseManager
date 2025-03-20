package com.adithya.aaafexpensemanager.transaction;

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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.adithya.aaafexpensemanager.R;
import com.adithya.aaafexpensemanager.account.AccountViewModel;
import com.adithya.aaafexpensemanager.settings.category.CategoryViewModel;
import com.adithya.aaafexpensemanager.reusableComponents.reusableDialogs.ConfirmationDialog;
import com.adithya.aaafexpensemanager.reusableComponents.reusableDialogs.EditTextDialog;
import com.adithya.aaafexpensemanager.reusableComponents.reusableDialogs.LookupDialog;
import com.adithya.aaafexpensemanager.transactionFilter.TransactionFilter;
import com.adithya.aaafexpensemanager.util.AppConstants;
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/** @noinspection deprecation*/
public class TransactionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_DATE = 0;
    private static final int TYPE_TRANSACTION = 1;
    private final List<Object> items;
    private final DateTimeFormatter dateFormatter;
    private ActionMode actionMode;
    private final TransactionViewModel viewModel;
    private final TransactionFragment transactionFragment;
    private final List<Transaction> selectedTransactions = new ArrayList<>();
    public TransactionsAdapter(List<Transaction> transactions,
                               TransactionFragment transactionFragment,
                               TransactionViewModel viewModel) {
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
    public void setTransactions(List<Transaction> transactions) {
        items.clear();
        addTransactions(transactions);
    }
    @SuppressLint("NotifyDataSetChanged")
    public void addTransactions(List<Transaction> transactions) {
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
            Transaction transaction = (Transaction) item;
            if (transaction != null) {
                setUpTransactionViewHolder(transactionViewHolder, position, transaction);
            }
        }
    }

    /** @noinspection deprecation*/
    @SuppressLint("UseCompatLoadingForDrawables")
    private void setUpTransactionViewHolder(@NonNull TransactionViewHolder holder, int position, Transaction transaction) {
        holder.transactionNameTextView.setText(transaction.transactionName);
        holder.transactionDateTextView.setText(transaction.getFormattedTransactionDate());
        holder.amountTextView.setText(transaction.amountToIndianFormat());
        holder.accountNameTextView.setText(transaction.accountName);
        holder.categoryNameTextView.setText(transaction.category);
        String transactionType = transaction.transactionType;
        int amountColor;
        if ("Income".equals(transactionType)) {
            amountColor = transactionFragment.getResources().getColor(android.R.color.holo_green_dark);
        } else if ("Expense".equals(transactionType)) {
            amountColor = transactionFragment.getResources().getColor(android.R.color.holo_red_dark);
        } else {
            amountColor = transactionFragment.getResources().getColor(android.R.color.black);
        }
        holder.amountTextView.setTextColor(amountColor);


        String transferInd = transaction.transferInd;
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
                UUID transactionUUID = transaction.transactionUUID;
                Transaction originalTransaction = viewModel.getTransactionById(transactionUUID);
                if (originalTransaction != null) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("transaction", originalTransaction);
                    NavHostFragment.findNavController(this.transactionFragment)
                            .navigate(R.id.action_transactionFragment_to_createTransactionFragment, bundle);
                } else {
                    Log.e("TransactionFragment", "Transaction not found with UUID: " + transactionUUID);
                    Toast.makeText(this.transactionFragment.getContext(), "Error: Transaction not found.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        holder.itemView.setOnLongClickListener(v -> {
            if (actionMode == null) {
                startActionMode();
                toggleSelection(holder, position);
            }
            return true;
        });
        holder.transactionItemContainer.setBackgroundColor(selectedTransactions.contains((Transaction) items.get(position))
                ? this.transactionFragment.getResources().getColor(R.color.selected_item_color)
                : Color.TRANSPARENT);
    }

    private void addSeparators(List<Transaction> transactions) {
        LocalDate lastDate = null;
        for (Transaction transaction : transactions) {
            if (lastDate == null || !transaction.getTransactionLocalDate().equals(lastDate)) {
                items.add(transaction.getTransactionLocalDate());
                lastDate = transaction.getTransactionLocalDate();
            }
            items.add(transaction);
        }
    }

    /** @noinspection deprecation*/
    private void toggleSelection(TransactionViewHolder holder, int position) {
        Transaction transaction = (Transaction) items.get(position);
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
            if(listObject instanceof Transaction){
                Transaction transaction = (Transaction) listObject;
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
            for (Transaction transaction : selectedTransactions) {
                viewModel.deleteTransaction(transaction);
            }

            Toast.makeText(this.transactionFragment.getContext(), selectedTransactions.size() + " transactions deleted", Toast.LENGTH_SHORT).show();
            selectedTransactions.clear();
            this.transactionFragment.currentPage=1;
            viewModel.getTransactions(this.transactionFragment.transactionFilter,
                    this.transactionFragment.currentPage)
                        .observe(this.transactionFragment.getViewLifecycleOwner(), this.transactionFragment::updateRecyclerView);
        }
    }
    private void startActionMode() {
        //noinspection DataFlowIssue
        actionMode = this.transactionFragment.getActivity().startActionMode(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.transaction_multiselect_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            /** @noinspection DataFlowIssue*/
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                if (item.getItemId() == R.id.action_delete) {
                    long selectionCount = selectedTransactions.size();
                    if(selectionCount%AppConstants.BATCH_SIZE==0
                    &&selectionCount!=0){
                        deleteAllFilteredTransactions(transactionFragment.transactionFilter);
                        return true;
                    }
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
                else if(item.getItemId() == R.id.action_copy_txn){
                    if(selectedTransactions.size() >= AppConstants.COPY_LIMIT){
                        Snackbar.make(transactionFragment.getView(),"Copy is not supported for more than "+AppConstants.COPY_LIMIT+" transactions",Snackbar.LENGTH_SHORT).show();
                    } else {
                        copyTransactions(selectedTransactions);
                    }
                }
                else if(item.getItemId() == R.id.action_update_account){
                    AccountViewModel accountViewModel = new ViewModelProvider(transactionFragment.requireActivity()).get(AccountViewModel.class);
                    List<String> lookupItems = accountViewModel
                            .getAccounts()
                            .getValue()
                            .stream()
                            .map(e-> e.accountName)
                            .toList();
                    new LookupDialog(transactionFragment.getContext(),
                            "Update Account",
                            lookupItems,
                            (selectedText)->{
                                viewModel.updateTransactionFields(selectedTransactions,"account_name",selectedText);
                                transactionFragment.reloadData();
                                Snackbar.make(transactionFragment.getView(),"Update Account is successful",Snackbar.LENGTH_SHORT).show();
                                deSelectAll();
                            },
                            (selectedText)->{
                                Snackbar.make(transactionFragment.getView(),"Update Account is failed",Snackbar.LENGTH_SHORT).show();
                            },
                            "Account Name");
                }
                else if(item.getItemId() == R.id.action_update_category){
                    CategoryViewModel categoryViewModel = new ViewModelProvider(transactionFragment.requireActivity()).get(CategoryViewModel.class);
                    List<String> lookupItems = categoryViewModel
                            .getCategories()
                            .getValue()
                            .stream()
                            .map(e-> e.categoryName)
                            .toList();
                    new LookupDialog(transactionFragment.getContext(),
                            "Update Category",
                            lookupItems,
                            (selectedText)->{
                                viewModel.updateTransactionFields(selectedTransactions,
                                        "category",
                                        selectedText);
                                transactionFragment.reloadData();
                                Snackbar.make(transactionFragment.getView(),"Update Category is successful",Snackbar.LENGTH_SHORT).show();
                                deSelectAll();
                            },
                            (selectedText)->{},
                            "Category Name");
                }
                else if(item.getItemId() == R.id.action_update_transaction_name){
                    new EditTextDialog(transactionFragment.getContext(),
                            "Update Transaction Name",
                            (inputText)->{
                                viewModel.updateTransactionFields(selectedTransactions,"transaction_name",inputText);
                                transactionFragment.reloadData();
                                Snackbar.make(transactionFragment.getView(),"Update Transaction Name is successful",Snackbar.LENGTH_SHORT).show();
                                deSelectAll();
                            },
                            (inputText)->{},
                            "Transaction Name");
                }
                else if(item.getItemId() == R.id.action_update_to_account){
                    AccountViewModel accountViewModel = new ViewModelProvider(transactionFragment.requireActivity()).get(AccountViewModel.class);
                    List<String> lookupItems = accountViewModel
                            .getAccounts()
                            .getValue()
                            .stream()
                            .map(e-> e.accountName)
                            .toList();
                    new LookupDialog(transactionFragment.getContext(),
                            "Update To Account",
                            lookupItems,
                            (selectedText)->{
                                viewModel.updateTransactionFields(selectedTransactions,"to_account_name",selectedText);
                                transactionFragment.reloadData();
                                Snackbar.make(transactionFragment.getView(),"Update To Account is successful",Snackbar.LENGTH_SHORT).show();
                                deSelectAll();
                            },
                            (selectedText)->{Snackbar.make(transactionFragment.getView(),"Update To Account is failed",Snackbar.LENGTH_SHORT).show();},
                            "To Account Name");
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

    private void copyTransactions(List<Transaction> selectedTransactions) {
        Map<String, Transaction> transactionMap =
                selectedTransactions.stream()
                        .collect(Collectors.toMap(e->e.transactionUUID.toString(),e->e));
        transactionMap.forEach((key,value) -> viewModel.copyTransaction(value));
        new ConfirmationDialog(transactionFragment.getContext(),
                "Copy Transactions",
                "Are you sure you want to copy these transactions?",
                ()-> transactionMap.forEach((key, value) -> viewModel.copyTransaction(value)),
                ()->{},
                "Copy",
                "Cancel"
                );
        deSelectAll();
        this.transactionFragment.reloadData();
    }

    private void deleteAllFilteredTransactions(TransactionFilter transactionFilter) {
        viewModel.deleteAllFilteredTransactions(transactionFilter);
        selectedTransactions.clear();
        this.transactionFragment.currentPage=1;
        viewModel.getTransactions(this.transactionFragment.transactionFilter,
                        this.transactionFragment.currentPage)
                .observe(this.transactionFragment.getViewLifecycleOwner(), this.transactionFragment::updateRecyclerView);
        Toast.makeText(this.transactionFragment.getContext(), "Delete all filtered transactions running", Toast.LENGTH_SHORT).show();
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
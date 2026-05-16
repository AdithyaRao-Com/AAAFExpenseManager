package com.adithya.aaafexpensemanagerv2.recurring;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.adithya.aaafexpensemanagerv2.R;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RecurringAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_DATE = 0;
    private static final int TYPE_TRANSACTION = 1;
    private final List<Object> items;
    private final DateTimeFormatter dateFormatter;
    private final RecurringViewModel viewModel;
    private final RecurringFragment recurringFragment;

    public RecurringAdapter(List<RecurringSchedule> recurringSchedules,
                            RecurringFragment recurringFragment,
                            RecurringViewModel viewModel) {
        this.items = new ArrayList<>();
        this.dateFormatter = DateTimeFormatter.ofPattern("E dd MMM yyyy");
        this.recurringFragment = recurringFragment;
        this.viewModel = viewModel;
        addSeparators(recurringSchedules);
    }

    private void addSeparators(List<RecurringSchedule> recurringSchedules) {
        LocalDate lastDate = null;
        for (RecurringSchedule recurringSchedule : recurringSchedules) {
            LocalDate nextRecurringDate = recurringSchedule.getNextRecurringDateLocalDate();
            if (!nextRecurringDate.equals(lastDate)) {
                items.add(nextRecurringDate);
                lastDate = nextRecurringDate;
            }
            items.add(recurringSchedule);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) instanceof LocalDate ? TYPE_DATE : TYPE_TRANSACTION;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addRecurringSchedules(List<RecurringSchedule> recurringSchedules) {
        addSeparators(recurringSchedules);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_DATE) {
            View view = inflater.inflate(R.layout.list_date_separator, parent, false);
            return new DateViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_recurring, parent, false);
            return new RecurringViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = items.get(position);
        if (holder instanceof DateViewHolder dateViewHolder) {
            LocalDate date = (LocalDate) item;
            dateViewHolder.dateTextView.setText(dateFormatter.format(date));
        } else if (holder instanceof RecurringViewHolder recurringViewHolder) {
            RecurringSchedule recurringSchedule = (RecurringSchedule) item;
            if (recurringSchedule != null) {
                setUpRecurringViewHolder(recurringViewHolder, position, recurringSchedule);
            }
        }
    }

    /**
     * @noinspection unused
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    private void setUpRecurringViewHolder(@NonNull RecurringAdapter.RecurringViewHolder holder, int position, RecurringSchedule recurringSchedule) {
        holder.transactionNameTextView.setText(recurringSchedule.transactionName);
        holder.amountTextView.setText(recurringSchedule.amountToIndianFormat());
        holder.accountNameTextView.setText(recurringSchedule.accountName);
        holder.categoryNameTextView.setText(recurringSchedule.category);
        String transactionType = recurringSchedule.transactionType;
        int amountColor;
        if ("Income".equals(transactionType)) {
            amountColor = ContextCompat.getColor(recurringFragment.requireContext(), R.color.balance_positive);
        } else if ("Expense".equals(transactionType)) {
            amountColor = ContextCompat.getColor(recurringFragment.requireContext(), R.color.balance_negative);
        } else {
            amountColor = ContextCompat.getColor(recurringFragment.requireContext(), android.R.color.white);
        }
        holder.amountTextView.setTextColor(amountColor);


        String transferInd = recurringSchedule.transferInd;
        int indicatorColor;
        if ("Income".equals(transferInd)) {
            indicatorColor = ContextCompat.getColor(recurringFragment.requireContext(), R.color.balance_positive);
        } else if ("Expense".equals(transferInd)) {
            indicatorColor = ContextCompat.getColor(recurringFragment.requireContext(), R.color.balance_negative);
        } else if ("Transfer".equals(transferInd)) {
            indicatorColor = ContextCompat.getColor(recurringFragment.requireContext(), android.R.color.holo_blue_dark);
        } else {
            indicatorColor = ContextCompat.getColor(recurringFragment.requireContext(), android.R.color.white);
        }
        holder.transferIndImageView.setBackgroundColor(indicatorColor);

        holder.itemView.setOnClickListener(v -> {
            UUID recurringTransactionUUID = recurringSchedule.recurringScheduleUUID;
            RecurringSchedule originalRecurringSchedule = viewModel.getRecurringScheduleById(recurringTransactionUUID);
            if (originalRecurringSchedule != null) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("recurringSchedule", recurringSchedule);
                bundle.putBoolean("isEditing", true);
                NavHostFragment.findNavController(this.recurringFragment)
                        .navigate(R.id.action_recurringFragment_to_createRecurringFragment, bundle);
            } else {
                Log.e("TransactionFragment", "Transaction not found with UUID: " + recurringTransactionUUID);
                Toast.makeText(this.recurringFragment.getContext(), "Error: Transaction not found.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class RecurringViewHolder extends RecyclerView.ViewHolder {
        TextView transactionNameTextView;
        TextView amountTextView;
        TextView accountNameTextView;
        com.google.android.material.card.MaterialCardView transactionItemContainer;
        View transferIndImageView;
        TextView categoryNameTextView;

        public RecurringViewHolder(@NonNull View itemView) {
            super(itemView);
            transactionNameTextView = itemView.findViewById(R.id.transactionNameTextView);
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

package com.adithya.aaafexpensemanagerv2.reports.categorySummary;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.adithya.aaafexpensemanagerv2.R;
import com.adithya.aaafexpensemanagerv2.util.CurrencyFormatter;

import java.util.List;

/**
 *
 */
public class CategorySummaryAdapter extends RecyclerView.Adapter<CategorySummaryAdapter.ViewHolder> {
    private List<CategorySummaryRecord> records;

    public CategorySummaryAdapter(List<CategorySummaryRecord> categorySummaryRecords) {
        this.records = categorySummaryRecords;
    }

    @NonNull
    @Override
    public CategorySummaryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_report_category_summary, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CategorySummaryAdapter.ViewHolder holder, int position) {
        CategorySummaryRecord record = this.records.get(position);
        holder.categoryNameTextView.setText(record.category);
        holder.amountTextView.setText(CurrencyFormatter.formatIndianStyle(record.amount, "INR"));
        if (record.amount < 0) {
            holder.amountTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.balance_negative));
        } else {
            holder.amountTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.balance_positive));
        }
    }

    @Override
    public int getItemCount() {
        return this.records.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setRecords(List<CategorySummaryRecord> records) {
        this.records = records;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoryNameTextView;
        TextView amountTextView;
        TextView pctTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryNameTextView = itemView.findViewById(R.id.categoryNameTextView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            pctTextView = itemView.findViewById(R.id.pctTextView);
        }
    }
}

package com.adithya.aaafexpensemanager.reports.categorySummary;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adithya.aaafexpensemanager.R;
import com.adithya.aaafexpensemanager.util.CurrencyFormatter;

import java.util.List;

/**
 * @noinspection deprecation
 */
public class CategorySummaryAdapter extends RecyclerView.Adapter<CategorySummaryAdapter.ViewHolder> {
    private List<CategorySummaryRecord> records;

    public CategorySummaryAdapter(List<CategorySummaryRecord> categorySummaryRecords) {
        this.records = categorySummaryRecords;
    }

    @NonNull
    @Override
    public CategorySummaryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.list_item_report_category_summary, null);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CategorySummaryAdapter.ViewHolder holder, int position) {
        CategorySummaryRecord record = this.records.get(position);
        holder.categoryNameTextView.setText(record.category);
        holder.amountTextView.setText(CurrencyFormatter.formatIndianStyle(record.amount, "INR"));
        if (record.amount < 0) {
            holder.amountTextView.setTextColor(holder.itemView.getResources().getColor(android.R.color.holo_red_dark));
        } else {
            holder.amountTextView.setTextColor(holder.itemView.getResources().getColor(android.R.color.holo_green_dark));
        }
//        holder.pctTextView.setText(record.pct+"%");
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

package com.adithya.aaafexpensemanager.reports.forecastSummary;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adithya.aaafexpensemanager.R;

import java.util.List;

public class ForecastReportAdapter extends RecyclerView.Adapter<ForecastReportAdapter.ViewHolder> {
    private List<ForecastReportRecord> items;

    public ForecastReportAdapter(List<ForecastReportRecord> items) {
        this.items = items;
    }

    public void setItems(List<ForecastReportRecord> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.list_item_report_forecast_summary, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastReportAdapter.ViewHolder holder, int position) {
        ForecastReportRecord item = items.get(position);
        holder.dateTextView.setText(item.getDateText_DD_MMM_YYYY());
        holder.amountTextView.setText(item.getAmountText());
        if(item.amount<=0){
            holder.amountTextView.setTextColor(holder.itemView.getResources().getColor(android.R.color.holo_red_dark));
        }
        else {
            holder.amountTextView.setTextColor(holder.itemView.getResources().getColor(android.R.color.holo_green_dark));
        }
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        TextView amountTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
        }
    }
}

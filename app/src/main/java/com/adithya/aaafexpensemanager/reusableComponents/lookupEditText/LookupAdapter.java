package com.adithya.aaafexpensemanager.reusableComponents.lookupEditText;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adithya.aaafexpensemanager.reusableComponents.lookupEditText.LookupEditText.LookupEditTextItem;

import java.util.ArrayList;
import java.util.List;

/** @noinspection unchecked*/
public class LookupAdapter extends RecyclerView.Adapter<LookupAdapter.ViewHolder> implements Filterable {

    private final List<LookupEditTextItem> originalItems;
    private List<LookupEditTextItem> filteredItems;
    private final OnItemClickListener onItemClickListener;
    private final AlertDialog alertDialog;

    public interface OnItemClickListener {
        void onItemClick(LookupEditTextItem item);
    }

    public LookupAdapter(List<LookupEditText.LookupEditTextItem> items, OnItemClickListener listener, AlertDialog alertDialog) {
        this.originalItems = items;
        this.filteredItems = new ArrayList<>(items);
        this.onItemClickListener = listener;
        this.alertDialog = alertDialog;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LookupEditTextItem item = filteredItems.get(position);
        holder.textView.setText(item.toEditTextLookupString());
        holder.itemView.setOnClickListener(v -> {
            onItemClickListener.onItemClick(item);
            this.alertDialog.dismiss();
        });
    }

    @Override
    public int getItemCount() {
        return filteredItems.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String filterString = constraint.toString().toLowerCase();
                FilterResults results = new FilterResults();

                if (filterString.isEmpty()) {
                    results.values = originalItems;
                } else {
                    List<LookupEditTextItem> filteredList = new ArrayList<>();
                    for (LookupEditTextItem item : originalItems) {
                        if (item.toEditTextLookupString().toLowerCase().contains(filterString)) {
                            filteredList.add(item);
                        }
                    }
                    results.values = filteredList;
                }

                return results;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredItems = (List<LookupEditTextItem>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}
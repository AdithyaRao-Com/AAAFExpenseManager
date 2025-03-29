package com.adithya.aaafexpensemanager.reusableComponents.multiSelectLookupEditText;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adithya.aaafexpensemanager.R;

import java.util.ArrayList;
import java.util.List;

public class MultiSelectLookupAdapter extends RecyclerView.Adapter<MultiSelectLookupAdapter.ViewHolder> implements Filterable {

    private final List<String> originalItems;
    private final List<String> selectedItems;
    private final OnItemCheckedChangeListener onItemCheckedChangeListener;
    private List<String> filteredItems;

    public MultiSelectLookupAdapter(List<String> items, List<String> selectedItems, OnItemCheckedChangeListener listener) {
        this.originalItems = items;
        this.filteredItems = new ArrayList<>(items);
        this.selectedItems = selectedItems;
        this.onItemCheckedChangeListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reuse_item_multiselect_lookup, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = filteredItems.get(position);
        holder.checkBox.setText(item);
        holder.checkBox.setChecked(selectedItems.contains(item));
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> onItemCheckedChangeListener.onItemCheckedChange(item, isChecked));
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
                    List<String> filteredList = new ArrayList<>();
                    for (String item : originalItems) {
                        if (item.toLowerCase().contains(filterString)) {
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
                //noinspection unchecked
                filteredItems = (List<String>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface OnItemCheckedChangeListener {
        void onItemCheckedChange(String item, boolean isChecked);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox_item);
        }
    }
}
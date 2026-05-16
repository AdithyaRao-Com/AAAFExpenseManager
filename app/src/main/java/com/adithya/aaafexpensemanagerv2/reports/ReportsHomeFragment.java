package com.adithya.aaafexpensemanagerv2.reports;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adithya.aaafexpensemanagerv2.R;

import java.util.ArrayList;
import java.util.List;

public class ReportsHomeFragment extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reports_home, container, false);
        
        RecyclerView recyclerView = view.findViewById(R.id.reportsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        List<ReportItem> items = new ArrayList<>();
        items.add(new ReportItem(getString(R.string.category_summary), v -> {
            NavHostFragment
                    .findNavController(getParentFragment())
                    .navigate(R.id.action_reportsHomeFragment_to_categorySummaryFragment);
        }));
        items.add(new ReportItem(getString(R.string.balance_forecast_summary), v -> {
            NavHostFragment
                    .findNavController(getParentFragment())
                    .navigate(R.id.action_reportsHomeFragment_to_balanceForecastFragment);
        }));

        recyclerView.setAdapter(new ReportAdapter(items));
        
        return view;
    }

    private static class ReportItem {
        String title;
        View.OnClickListener listener;

        ReportItem(String title, View.OnClickListener listener) {
            this.title = title;
            this.listener = listener;
        }
    }

    private static class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {
        private final List<ReportItem> items;

        ReportAdapter(List<ReportItem> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_menu, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ReportItem item = items.get(position);
            holder.titleTextView.setText(item.title);
            holder.itemView.setOnClickListener(item.listener);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView titleTextView;

            ViewHolder(View itemView) {
                super(itemView);
                titleTextView = itemView.findViewById(R.id.menuItemTitle);
            }
        }
    }
}
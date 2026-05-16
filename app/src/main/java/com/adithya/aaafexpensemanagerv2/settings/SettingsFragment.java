package com.adithya.aaafexpensemanagerv2.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adithya.aaafexpensemanagerv2.R;
import com.adithya.aaafexpensemanagerv2.recenttrans.RecentTransactionViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_home, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.settingsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        List<SettingsItem> items = new ArrayList<>();
        items.add(new SettingsItem(getString(R.string.manage_account_types), v -> 
            Navigation.findNavController(v).navigate(R.id.action_settingsFragment_to_accountTypeFragment)));
        items.add(new SettingsItem(getString(R.string.manage_categories), v -> 
            Navigation.findNavController(v).navigate(R.id.action_settingsFragment_to_categoryFragment)));
        items.add(new SettingsItem(getString(R.string.manage_currencies), v -> 
            Navigation.findNavController(v).navigate(R.id.action_settingsFragment_to_currencyHomeFragment)));
        items.add(new SettingsItem(getString(R.string.import_export), v -> 
            Navigation.findNavController(v).navigate(R.id.action_settingsFragment_to_importExportHomeFragment)));
        items.add(new SettingsItem(getString(R.string.future_transactions), v -> 
            Navigation.findNavController(v).navigate(R.id.action_settingsFragment_to_futureTransactionsFragment)));
        items.add(new SettingsItem(getString(R.string.about), v -> 
            Navigation.findNavController(v).navigate(R.id.action_settingsFragment_to_aboutAppFragment)));
        
        RecentTransactionViewModel recentTransactionViewModel = new ViewModelProvider(this).get(RecentTransactionViewModel.class);
        items.add(new SettingsItem(getString(R.string.refresh_recent_transactions), v -> {
            recentTransactionViewModel.refreshRecentTransactions();
            Snackbar.make(view, "Recent Transactions Refreshed", Snackbar.LENGTH_SHORT).show();
        }));

        recyclerView.setAdapter(new SettingsAdapter(items));

        return view;
    }

    private static class SettingsItem {
        String title;
        View.OnClickListener listener;

        SettingsItem(String title, View.OnClickListener listener) {
            this.title = title;
            this.listener = listener;
        }
    }

    private static class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.ViewHolder> {
        private final List<SettingsItem> items;

        SettingsAdapter(List<SettingsItem> items) {
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
            SettingsItem item = items.get(position);
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
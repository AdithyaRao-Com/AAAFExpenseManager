package com.adithya.aaafexpensemanagerv2.settings.aboutApp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adithya.aaafexpensemanagerv2.R;
import com.adithya.aaafexpensemanagerv2.util.AppConstants;
import com.adithya.aaafexpensemanagerv2.util.DBHelperSharedPrefs;

import java.util.ArrayList;
import java.util.List;

public class AboutAppFragment extends Fragment {

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_app, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.aboutAppRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        DBHelperSharedPrefs dbHelperSharedPrefs = new DBHelperSharedPrefs(requireContext());
        
        List<AboutItem> items = new ArrayList<>();
        items.add(new AboutItem(getString(R.string.application_version), AppConstants.APPLICATION_VERSION));
        items.add(new AboutItem(getString(R.string.database_version), String.valueOf(dbHelperSharedPrefs.getCurrentDataBaseVersion(0))));
        items.add(new AboutItem(getString(R.string.default_password), "123456"));

        recyclerView.setAdapter(new AboutAdapter(items));

        return view;
    }

    private static class AboutItem {
        String label;
        String value;

        AboutItem(String label, String value) {
            this.label = label;
            this.value = value;
        }
    }

    private static class AboutAdapter extends RecyclerView.Adapter<AboutAdapter.ViewHolder> {
        private final List<AboutItem> items;

        AboutAdapter(List<AboutItem> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_menu, parent, false);
            return new ViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            AboutItem item = items.get(position);
            holder.titleTextView.setText(item.label + ": " + item.value);
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

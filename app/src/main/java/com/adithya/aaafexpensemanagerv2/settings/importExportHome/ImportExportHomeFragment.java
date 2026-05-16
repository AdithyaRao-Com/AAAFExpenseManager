package com.adithya.aaafexpensemanagerv2.settings.importExportHome;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adithya.aaafexpensemanagerv2.R;

import java.util.ArrayList;
import java.util.List;

public class ImportExportHomeFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_import_export_home, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.importExportHomeRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        List<ImportExportMenuItem> items = new ArrayList<>();
        items.add(new ImportExportMenuItem(getString(R.string.export_database), v -> 
            Navigation.findNavController(v).navigate(R.id.action_importExportHomeFragment_to_exportDatabaseFragment)));
        items.add(new ImportExportMenuItem(getString(R.string.import_database), v -> 
            Navigation.findNavController(v).navigate(R.id.action_importExportHomeFragment_to_importDatabaseFragment)));
        items.add(new ImportExportMenuItem(getString(R.string.import_csv), v -> 
            Navigation.findNavController(v).navigate(R.id.action_importExportHomeFragment_to_importCSVFragment)));
        items.add(new ImportExportMenuItem(getString(R.string.export_csv), v -> 
            Navigation.findNavController(v).navigate(R.id.action_importExportHomeFragment_to_exportCSVFragment)));
        items.add(new ImportExportMenuItem(getString(R.string.import_qif), v -> 
            Navigation.findNavController(v).navigate(R.id.action_importExportHomeFragment_to_importQIFFragment)));
        items.add(new ImportExportMenuItem(getString(R.string.export_qif), v -> 
            Navigation.findNavController(v).navigate(R.id.action_importExportHomeFragment_to_exportQIFFragment)));
        items.add(new ImportExportMenuItem(getString(R.string.export_schedules_csv), v -> 
            Navigation.findNavController(v).navigate(R.id.action_importExportHomeFragment_to_exportSchedulesCSVFragment)));
        items.add(new ImportExportMenuItem(getString(R.string.import_schedules_csv), v -> 
            Navigation.findNavController(v).navigate(R.id.action_importExportHomeFragment_to_importSchedulesCSVFragment)));
        items.add(new ImportExportMenuItem(getString(R.string.auto_backup), v -> 
            Navigation.findNavController(v).navigate(R.id.action_importExportHomeFragment_to_autoBackupFragment)));

        recyclerView.setAdapter(new ImportExportMenuAdapter(items));

        return view;
    }

    private static class ImportExportMenuItem {
        String title;
        View.OnClickListener listener;

        ImportExportMenuItem(String title, View.OnClickListener listener) {
            this.title = title;
            this.listener = listener;
        }
    }

    private static class ImportExportMenuAdapter extends RecyclerView.Adapter<ImportExportMenuAdapter.ViewHolder> {
        private final List<ImportExportMenuItem> items;

        ImportExportMenuAdapter(List<ImportExportMenuItem> items) {
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
            ImportExportMenuItem item = items.get(position);
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

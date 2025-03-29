package com.adithya.aaafexpensemanager.settings.importExportHome;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.adithya.aaafexpensemanager.R;

/**
 * @noinspection FieldCanBeLocal
 */
public class ImportExportHomeFragment extends Fragment {
    private TextView exportDatabaseTextView;
    private TextView importDatabaseTextView;
    private TextView exportCSVTextView;
    private TextView importCSVTextView;
    private TextView importQIFTextView;
    private TextView exportQIFTextView;
    private TextView autoBackupTextView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_import_export_home, container, false);
        exportDatabaseTextView = view.findViewById(R.id.export_database_text_view);
        exportDatabaseTextView.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_settingsFragment_to_exportDatabaseFragment));
        importDatabaseTextView = view.findViewById(R.id.import_database_text_view);
        importDatabaseTextView.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_settingsFragment_to_importDatabaseFragment));
        exportCSVTextView = view.findViewById(R.id.export_csv_text_view);
//        exportCSVTextView.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_settingsFragment_to_exportCSVFragment));
        importCSVTextView = view.findViewById(R.id.import_csv_text_view);
        importCSVTextView.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_settingsFragment_to_importCSVFragment));
        importQIFTextView = view.findViewById(R.id.import_qif_text_view);
//        importQIFTextView.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_settingsFragment_to_importQIFFragment));
        exportQIFTextView = view.findViewById(R.id.export_qif_text_view);
//        exportQIFTextView.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_settingsFragment_to_exportQIFFragment));
        autoBackupTextView = view.findViewById(R.id.autobackup_text_view);
        autoBackupTextView.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_settingsFragment_to_autoBackupFragment));
        return view;
    }
}

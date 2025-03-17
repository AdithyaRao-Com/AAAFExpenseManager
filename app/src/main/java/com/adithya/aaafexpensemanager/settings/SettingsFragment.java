package com.adithya.aaafexpensemanager.settings;

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

/** @noinspection FieldCanBeLocal*/
public class SettingsFragment extends Fragment {
    //TODO - Export CSV Feature
    //TODO - Import CSV Feature
    private TextView accountTypeTextView;
    private TextView exportDatabaseTextView;
    private TextView importDatabaseTextView;
    private TextView futureTransactionsTextView;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_home, container, false);
        accountTypeTextView = view.findViewById(R.id.account_type_text_view);
        accountTypeTextView.setOnClickListener(v -> {
            // Navigate to AccountTypeFragment
            Navigation.findNavController(v).navigate(R.id.action_settingsFragment_to_accountTypeFragment);
        });
        exportDatabaseTextView = view.findViewById(R.id.export_database_text_view);
        exportDatabaseTextView.setOnClickListener(v -> {
            // Navigate to ExportDatabaseFragment
            Navigation.findNavController(v).navigate(R.id.action_settingsFragment_to_exportDatabaseFragment);
        });
        importDatabaseTextView = view.findViewById(R.id.import_database_text_view);
        importDatabaseTextView.setOnClickListener(v -> {
            // Navigate to ImportDatabaseFragment
            Navigation.findNavController(v).navigate(R.id.action_settingsFragment_to_importDatabaseFragment);
        });
        futureTransactionsTextView = view.findViewById(R.id.future_transactions_text_view);
        futureTransactionsTextView.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_settingsFragment_to_futureTransactionsFragment));
        return view;
    }

}
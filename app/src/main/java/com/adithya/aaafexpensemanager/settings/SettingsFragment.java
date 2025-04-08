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

/**
 * @noinspection FieldCanBeLocal
 */
public class SettingsFragment extends Fragment {
    private TextView accountTypeTextView;
    private TextView categoriesTextView;
    private TextView futureTransactionsTextView;
    private TextView currenciesTextView;
    private TextView importExportHomeTextView;
    private TextView aboutAppTextView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_home, container, false);
        accountTypeTextView = view.findViewById(R.id.account_type_text_view);
        accountTypeTextView.setOnClickListener(v -> {
            // Navigate to AccountTypeFragment
            Navigation.findNavController(v).navigate(R.id.action_settingsFragment_to_accountTypeFragment);
        });
        categoriesTextView = view.findViewById(R.id.categories_text_view);
        categoriesTextView.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_settingsFragment_to_categoryFragment));
        futureTransactionsTextView = view.findViewById(R.id.future_transactions_text_view);
        futureTransactionsTextView.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_settingsFragment_to_futureTransactionsFragment));
        currenciesTextView = view.findViewById(R.id.currencies_text_view);
        currenciesTextView.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_settingsFragment_to_currencyHomeFragment));
        importExportHomeTextView = view.findViewById(R.id.import_export_home_text_view);
        importExportHomeTextView.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_settingsFragment_to_importExportHomeFragment));
        aboutAppTextView = view.findViewById(R.id.about_app);
        aboutAppTextView.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_settingsFragment_to_aboutAppFragment));
        return view;
    }

}
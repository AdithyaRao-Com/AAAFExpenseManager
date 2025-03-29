package com.adithya.aaafexpensemanager.settings.currency;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.adithya.aaafexpensemanager.R;
import com.adithya.aaafexpensemanager.reusableComponents.lookupEditText.LookupEditText;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @noinspection DataFlowIssue, CallToPrintStackTrace
 */
public class CreatePrimaryCurrencyFragment extends Fragment {
    private CurrencyViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_create_primary_currency, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(CurrencyViewModel.class);
        LookupEditText primaryCurrencyCodeEditText = view.findViewById(R.id.primaryCurrencyCodeEditText);
        primaryCurrencyCodeEditText.setText(viewModel.getPrimaryCurrency());
        List<String> currenciesList = viewModel.getCurrencies().getValue().stream()
                .map(currency -> (currency.currencyName)).collect(Collectors.toList());
        primaryCurrencyCodeEditText.setItemStrings(currenciesList);
        FloatingActionButton createPrimaryCurrencyFAB = view.findViewById(R.id.createPrimaryCurrencyFAB);
        createPrimaryCurrencyFAB.setOnClickListener(v -> {
            try {
                String primaryCurrencyCode = primaryCurrencyCodeEditText.getText().toString();
                if (primaryCurrencyCode.isBlank() || primaryCurrencyCode.equals("N/A")) {
                    primaryCurrencyCodeEditText.setError("Please enter a currency code");
                    throw new RuntimeException("");
                }
                viewModel.setPrimaryCurrency(primaryCurrencyCode);
                Snackbar.make(view, "Primary currency code set to " + primaryCurrencyCode, Snackbar.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).navigate(R.id.action_createPrimaryCurrencyFragment_to_currencyHomeFragment);
            } catch (Exception e) {
                e.printStackTrace();
                //noinspection DataFlowIssue
                if (!e.getMessage().isBlank()) {
                    Snackbar.make(view, "Error while setting primary currency code "
                            + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }
}

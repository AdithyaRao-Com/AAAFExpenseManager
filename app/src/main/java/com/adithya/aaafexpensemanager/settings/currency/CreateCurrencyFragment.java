package com.adithya.aaafexpensemanager.settings.currency;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.adithya.aaafexpensemanager.R;
import com.adithya.aaafexpensemanager.reusableComponents.decimalLimitEditText.DecimalLimitEditText;
import com.adithya.aaafexpensemanager.reusableComponents.reusableDialogs.ConfirmationDialog;
import com.adithya.aaafexpensemanager.settings.currency.exception.CurrencyExistsException;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** @noinspection FieldCanBeLocal*/
public class CreateCurrencyFragment extends Fragment {
    private CurrencyViewModel viewModel;
    private EditText currencyNameEditText;
    private DecimalLimitEditText conversionFactorEditText;
    private FloatingActionButton createCurrencyButton;
    private Currency originalCurrency;
    private MenuItem deleteMenuItem;
    private boolean isEditing = false;
    /** @noinspection deprecation*/
    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_currency, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(CurrencyViewModel.class);
        currencyNameEditText = view.findViewById(R.id.currencyNameEditText);
        setParametersForCurrencyName(currencyNameEditText);
        conversionFactorEditText = view.findViewById(R.id.conversionFactorTextView);
        createCurrencyButton = view.findViewById(R.id.createCurrencyFAB);
        if (getArguments() != null && getArguments().containsKey("currency")) {
            originalCurrency = getArguments().getParcelable("currency");
            isEditing = false;
            if (originalCurrency != null) {
                isEditing = true;
                currencyNameEditText.setText(originalCurrency.currencyName);
                conversionFactorEditText.setText(String.valueOf(originalCurrency.conversionFactor));
            }
        }
        createCurrencyButton.setOnClickListener(v -> {
            try {
                String currencyName = currencyNameEditText.getText().toString().trim();
                if (currencyName.isEmpty()) {
                    currencyNameEditText.setError("Currency name cannot be empty");
                    return;
                }
                //noinspection DataFlowIssue
                String conversionFactorString = conversionFactorEditText.getText().toString().trim();
                double conversionFactor;
                if (conversionFactorString.isEmpty()) {
                    conversionFactorEditText.setError("Conversion factor cannot be empty");
                    return;
                }
                try{
                    conversionFactor = (double) Math.round(Double.parseDouble(conversionFactorString) * 1000000) /1000000;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                if (originalCurrency != null) {
                    originalCurrency.currencyName = currencyName;
                    originalCurrency.conversionFactor = conversionFactor;
                    viewModel.updateCurrency(originalCurrency);
                } else {
                    Currency newCategory = new Currency(currencyName, conversionFactor);
                    viewModel.addCurrency(newCategory);
                }
                Navigation.findNavController(requireView()).navigate(R.id.action_createCurrencyFragment_to_currencyFragment);
            }
            catch (CurrencyExistsException e) {
                //noinspection DataFlowIssue
                Snackbar.make(view, e.getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    private void setParametersForCurrencyName(EditText currencyNameEditText) {
        InputFilter[] existingFilters = currencyNameEditText.getFilters();
        List<InputFilter> filterList = new ArrayList<>(Arrays.asList(existingFilters));
        filterList.add(new InputFilter.AllCaps());
        InputFilter[] newFilters = filterList.toArray(new InputFilter[0]);
        currencyNameEditText.setFilters(newFilters);
    }

    /** @noinspection SameParameterValue*/
    private void setEditTextEnabled(EditText editTextField, boolean enabledFlag) {
        editTextField.setEnabled(enabledFlag);
        editTextField.setFocusable(enabledFlag);
        editTextField.setFocusableInTouchMode(enabledFlag);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.create_currency_menu, menu);
                deleteMenuItem = menu.findItem(R.id.action_delete_currency);
                setOptions(isEditing);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if(menuItem.getItemId()==R.id.action_delete_currency){
                    new ConfirmationDialog(getContext(),
                            "Delete Currency",
                            "Are you sure you want to delete this currency? \n",
                            ()-> {
                                viewModel.deleteCurrency(originalCurrency);
                                Navigation.findNavController(getView()).navigate(R.id.nav_currency);},
                            ()->{},
                            "Delete",
                            "Cancel"
                    );
                    return true;
                }
                return false;
            }
        },getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }
    private void setOptions(boolean isEditing) {
        try {
            deleteMenuItem.setVisible(isEditing);
        }
        catch (Exception ignored){}
    }
}

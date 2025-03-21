package com.adithya.aaafexpensemanager.settings.currency;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.adithya.aaafexpensemanager.R;

public class CurrencyHomeFragment extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_currency_home,container,false);
        TextView primaryAccountTextView = view.findViewById(R.id.primary_account_text_view);
        TextView addUpdateCurrencyTextView = view.findViewById(R.id.add_update_currency_text_view);
//        TextView clearAllCurrencyData = view.findViewById(R.id.clear_all_currency_data);
//        clearAllCurrencyData.setOnClickListener(v ->{
//            CurrencyViewModel viewModel = new ViewModelProvider(getActivity()).get(CurrencyViewModel.class);
//            viewModel.deleteAll();
//            Snackbar.make(view, "All currency data has been deleted", Snackbar.LENGTH_SHORT).show();
//        });
        primaryAccountTextView.setOnClickListener(v-> Navigation.findNavController(requireView()).navigate(R.id.action_currencyHomeFragment_to_createPrimaryCurrencyFragment));
        addUpdateCurrencyTextView.setOnClickListener(v-> Navigation.findNavController(requireView()).navigate(R.id.action_currencyHomeFragment_to_currencyFragment));
        return view;
    }
}

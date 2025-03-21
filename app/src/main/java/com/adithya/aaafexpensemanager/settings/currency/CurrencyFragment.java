package com.adithya.aaafexpensemanager.settings.currency;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adithya.aaafexpensemanager.R;

import java.util.ArrayList;
import java.util.List;

public class CurrencyFragment extends Fragment {
    private CurrencyViewModel viewModel;
    private CurrenciesAdapter adapter;
    private Currency selectedCurrency;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_currency, container, false);
        RecyclerView currenciesRecyclerView = view.findViewById(R.id.currenciesRecyclerView);
        currenciesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        viewModel = new ViewModelProvider(requireActivity()).get(CurrencyViewModel.class);
        adapter = new CurrenciesAdapter(new ArrayList<>());
        currenciesRecyclerView.setAdapter(adapter);

        viewModel.getCurrencies().observe(getViewLifecycleOwner(), currencies -> {
            adapter.setCurrencies(currencies);
            adapter.notifyDataSetChanged();
        });
        EditText searchEditText = view.findViewById(R.id.searchCurrencyEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.filterCurrencies(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        return view;
    }
    private class CurrenciesAdapter extends RecyclerView.Adapter<CurrenciesAdapter.CurrencyViewHolder> {
        private List<Currency> currencies;

        public CurrenciesAdapter(List<Currency> currencies) {
            this.currencies = currencies;
        }

        public void setCurrencies(List<Currency> categories) {
            this.currencies = categories;
        }

        @NonNull
        @Override
        public CurrencyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_currency, parent, false);
            return new CurrencyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CurrencyViewHolder holder, int position) {
            Currency currency = currencies.get(position);
            holder.currencyNameTextView.setText(currency.currencyName);
            holder.primaryCurrencyTextView.setText(currency.isPrimary ? "Primary" : "");
            holder.itemView.setOnClickListener(v -> {
                selectedCurrency = currency;
                Bundle args = new Bundle();
                args.putParcelable("currency",selectedCurrency);
                Navigation.findNavController(requireView()).navigate(R.id.action_currencyFragment_to_createCurrencyFragment, args);
            });
        }

        @Override
        public int getItemCount() {
            return currencies.size();
        }

        public class CurrencyViewHolder extends RecyclerView.ViewHolder {
            public android.widget.TextView currencyNameTextView;
            public android.widget.TextView primaryCurrencyTextView;

            public CurrencyViewHolder(@NonNull View itemView) {
                super(itemView);
                currencyNameTextView = itemView.findViewById(R.id.currencyNameTextView);
                primaryCurrencyTextView = itemView.findViewById(R.id.primaryCurrencyTextView);
            }
        }
    }
}
package com.adithya.aaafexpensemanager.account;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.adithya.aaafexpensemanager.databinding.FragmentListAccountBinding;

import java.util.ArrayList;

public class AccountFragment extends Fragment {
    private FragmentListAccountBinding binding;
    private AccountViewModel viewModel;
    private AccountsAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentListAccountBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        viewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AccountsAdapter(new ArrayList<>(),
                getContext(),
                this::changeNavigation);
        binding.recyclerView.setAdapter(adapter);

        // Observe LiveData for accounts from ViewModel
        viewModel.getAccounts().observe(getViewLifecycleOwner(), accountNames -> adapter.setAccounts(accountNames));

        binding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.filterAccounts(s.toString()); //Triggers transformation
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume(){
        super.onResume();
        viewModel.loadAccountNames();
    }
    public void changeNavigation(int idNavigate,Bundle args){
        NavHostFragment.findNavController(this)
                .navigate(idNavigate,args);
    }
}
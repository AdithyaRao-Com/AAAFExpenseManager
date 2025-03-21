package com.adithya.aaafexpensemanager.settings.category;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.adithya.aaafexpensemanager.reusableComponents.reusableDialogs.ConfirmationDialog;
import com.adithya.aaafexpensemanager.settings.category.exception.CategoryExistsException;
import com.adithya.aaafexpensemanager.transactionFilter.TransactionFilter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

/** @noinspection FieldCanBeLocal*/
public class CreateCategoryFragment extends Fragment {
    private CategoryViewModel viewModel;
    private EditText categoryNameEditText;
    private AutoCompleteTextView parentCategoryTextView;
    private FloatingActionButton createCategoryButton;
    private List<String> parentCategories;
    private ArrayAdapter<String> autoCompleteAdapter;
    private Category originalCategory;
    private MenuItem deleteMenuItem;
    private MenuItem showTransactionsMenuItem;
    private boolean isEditing = false;
    /** @noinspection deprecation*/
    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_category, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(CategoryViewModel.class);
        categoryNameEditText = view.findViewById(R.id.categoryNameEditText);
        parentCategoryTextView = view.findViewById(R.id.parentCategoryTextView);
        createCategoryButton = view.findViewById(R.id.createCategoryFAB);
        parentCategories = viewModel.getDistinctParentCategories();
        if (autoCompleteAdapter == null) {
            autoCompleteAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, parentCategories);
            parentCategoryTextView.setAdapter(autoCompleteAdapter);
            parentCategoryTextView.setThreshold(0);
        } else {
            autoCompleteAdapter.clear();
            autoCompleteAdapter.addAll(parentCategories);
            autoCompleteAdapter.notifyDataSetChanged();
        }
        if (getArguments() != null && getArguments().containsKey("category")) {
            originalCategory = getArguments().getParcelable("category");
            isEditing = false;
            if (originalCategory != null) {
                isEditing = true;
                categoryNameEditText.setText(originalCategory.categoryName);
                parentCategoryTextView.setText(originalCategory.parentCategory);
                setEditTextEnabled(categoryNameEditText,true);
            } else {
                setEditTextEnabled(categoryNameEditText,true);
            }
        } else {
            setEditTextEnabled(categoryNameEditText,true);
        }
        createCategoryButton.setOnClickListener(v -> {
            try {
                String categoryName = categoryNameEditText.getText().toString().trim();
                String selectedParentCategoryName = parentCategoryTextView.getText().toString().trim();
                if (categoryName.isEmpty()) {
                    categoryNameEditText.setError("Category name cannot be empty");
                    return;
                }
                if (originalCategory != null) {
                    originalCategory.categoryName = categoryName;
                    originalCategory.parentCategory = selectedParentCategoryName;
                    viewModel.updateCategory(originalCategory);
                } else {
                    Category newCategory = new Category(categoryName, selectedParentCategoryName);
                    viewModel.addCategory(newCategory);
                }
                Navigation.findNavController(requireView()).navigate(R.id.nav_category);
            }
            catch (CategoryExistsException e) {
                //noinspection DataFlowIssue
                Snackbar.make(view, e.getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        });
        return view;
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
                menuInflater.inflate(R.menu.create_category_menu, menu);
                deleteMenuItem = menu.findItem(R.id.action_delete_category);
                showTransactionsMenuItem = menu.findItem(R.id.action_show_category_transactions);
                setOptions(isEditing);
            }
            /** @noinspection DataFlowIssue*/
            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if(menuItem.getItemId()==R.id.action_delete_category){
                    new ConfirmationDialog(getContext(),
                            "Delete Category",
                            "Are you sure you want to delete this category? \n" +
                                    "Impacted Transactions will be marked as uncategorized.",
                            ()-> {
                                viewModel.deleteCategory(originalCategory);
                                Navigation.findNavController(getView()).navigate(R.id.nav_category);},
                            ()->{},
                            "Delete",
                            "Cancel"
                    );
                    return true;
                }
                else if(menuItem.getItemId()==R.id.action_show_category_transactions){
                    Bundle args = new Bundle();
                    TransactionFilter categoryFilter = new TransactionFilter();
                    ArrayList<String> categoryNames = new ArrayList<>();
                    categoryNames.add(originalCategory.categoryName);
                    categoryFilter.categories = categoryNames;
                    args.putParcelable("transaction_filter",categoryFilter);
                    Navigation.findNavController(getView()).navigate(R.id.nav_transaction,args);
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void setOptions(boolean isEditing) {
        try {
            deleteMenuItem.setVisible(isEditing);
            showTransactionsMenuItem.setVisible(isEditing);
        }
        catch (Exception ignored){}
    }
}

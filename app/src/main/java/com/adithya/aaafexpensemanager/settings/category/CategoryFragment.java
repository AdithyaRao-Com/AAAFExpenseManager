package com.adithya.aaafexpensemanager.settings.category;

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

public class CategoryFragment extends Fragment {
    private CategoryViewModel viewModel;
    private CategoriesAdapter adapter;
    private Category selectedCategory;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_category, container, false);

        RecyclerView categoriesRecyclerView = view.findViewById(R.id.categoriesRecyclerView);
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        viewModel = new ViewModelProvider(requireActivity()).get(CategoryViewModel.class);

        adapter = new CategoriesAdapter(new ArrayList<>());
        categoriesRecyclerView.setAdapter(adapter);

        viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            adapter.setCategories(categories);
            adapter.notifyDataSetChanged();
        });
        EditText searchEditText = view.findViewById(R.id.searchCategoryEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.filterCategories(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        return view;
    }
    private class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder> {
        private List<Category> categories;

        public CategoriesAdapter(List<Category> categories) {
            this.categories = categories;
        }

        public void setCategories(List<Category> categories) {
            this.categories = categories;
        }

        @NonNull
        @Override
        public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_category, parent, false);
            return new CategoryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
            Category category = categories.get(position);
            holder.categoryNameTextView.setText(category.categoryName);
            holder.parentCategoryTextView.setText(category.parentCategory);
            holder.itemView.setOnClickListener(v -> {
                selectedCategory = category;
                Bundle args = new Bundle();
                args.putParcelable("category",selectedCategory);
                Navigation.findNavController(requireView()).navigate(R.id.action_categoryFragment_to_createCategoryFragment, args);
            });
        }

        @Override
        public int getItemCount() {
            return categories.size();
        }

        public class CategoryViewHolder extends RecyclerView.ViewHolder {
            public android.widget.TextView categoryNameTextView;
            public android.widget.TextView parentCategoryTextView;

            public CategoryViewHolder(@NonNull View itemView) {
                super(itemView);
                categoryNameTextView = itemView.findViewById(R.id.categoryNameTextView);
                parentCategoryTextView = itemView.findViewById(R.id.parentCategoryTextView);
            }
        }
    }
}
package com.adithya.aaafexpensemanagerv2.settings.category;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.adithya.aaafexpensemanagerv2.settings.category.exception.CategoryExistsException;

import java.util.List;

public class CategoryViewModel extends AndroidViewModel {
    private final CategoryRepository repository;
    private final MutableLiveData<List<Category>> categories = new MutableLiveData<>();
    private String currentSearchText = "";

    public CategoryViewModel(Application application) {
        super(application);
        repository = new CategoryRepository(application);
        loadCategories(); // Load categories initially
    }

    public LiveData<List<Category>> getCategories() {
        return categories;
    }

    public void addCategory(Category category) throws CategoryExistsException {
        repository.addCategory(category);
        loadCategories();
    }

    public void updateCategory(Category category) throws CategoryExistsException {
        repository.updateCategory(category);
        loadCategories(); // Reload after updating
    }

    public void deleteCategory(Category category) {
        repository.deleteCategory(category);
        loadCategories(); // Reload after deleting
    }

    public void loadCategories() {
        if (currentSearchText == null || currentSearchText.isEmpty()) {
            categories.setValue(repository.getAllCategories());
        } else {
            filterCategories(currentSearchText);
        }
    }

    public void resetSearch() {
        currentSearchText = "";
        loadCategories();
    }

    public void filterCategories(String searchText) {
        currentSearchText = searchText;
        List<Category> filteredCategories = repository.filterCategories(searchText); // From repository
        categories.setValue(filteredCategories);
    }

    public List<String> getDistinctParentCategories() {
        return repository.getDistinctParentCategories();
    }
}
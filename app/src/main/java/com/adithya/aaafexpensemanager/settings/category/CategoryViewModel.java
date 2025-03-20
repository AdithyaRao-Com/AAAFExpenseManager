package com.adithya.aaafexpensemanager.settings.category;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.adithya.aaafexpensemanager.settings.category.exception.CategoryExistsException;

import java.util.List;

public class CategoryViewModel extends AndroidViewModel {
    private final CategoryRepository repository;
    private final LiveData<List<Category>> categories;

    public CategoryViewModel(Application application) {
        super(application);
        repository = new CategoryRepository(application);
        categories = new MutableLiveData<>(); // Initialize LiveData
        loadCategories(); // Load categories initially
    }

    public LiveData<List<Category>> getCategories() {
        return categories;
    }

    public void addCategory(Category category) throws CategoryExistsException {
        repository.addCategory(category);
        loadCategories();
    }

    public void updateCategory(Category category) throws CategoryExistsException{
        repository.updateCategory(category);
        loadCategories(); // Reload after updating
    }

    public void deleteCategory(Category category) {
        repository.deleteCategory(category);
        loadCategories(); // Reload after deleting
    }

    private void loadCategories() {
        List<Category> categoryList = repository.getAllCategories();
        ((MutableLiveData<List<Category>>) categories).setValue(categoryList); // Cast and set value
    }
    public void filterCategories(String searchText) {
        List<Category> filteredCategories = repository.filterCategories(searchText); // From repository
        ((MutableLiveData<List<Category>>) categories).setValue(filteredCategories);
    }

    public List<String> getDistinctParentCategories(){
        return repository.getDistinctParentCategories();
    }
}
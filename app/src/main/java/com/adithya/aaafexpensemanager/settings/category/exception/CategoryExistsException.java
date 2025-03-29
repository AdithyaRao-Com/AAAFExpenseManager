package com.adithya.aaafexpensemanager.settings.category.exception;

/**
 * @noinspection unused
 */
public class CategoryExistsException extends Exception {
    public CategoryExistsException(String message) {
        super(String.format("Category name %s already exists", message));
    }

    public CategoryExistsException() {
        super("Category name already exists");
    }
}

package com.adithya.aaafexpensemanager.settings.category;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;
import java.util.UUID;

public class Category implements Parcelable {
    public UUID categoryUUID;
    public String categoryName;

    public String parentCategory;

    public Category(String categoryName,String parentCategory) {
        this.categoryUUID = UUID.randomUUID();
        this.categoryName = categoryName;
        this.parentCategory = parentCategory;
    }

    public Category(UUID categoryUUID, String categoryName, String parentCategory) {
        this.categoryUUID = categoryUUID;
        this.categoryName = categoryName;
        this.parentCategory = parentCategory;
    }

    // Parcelable implementation (add to writeToParcel and constructor)
    protected Category(Parcel in) {
        String uuidStr = in.readString();
        this.categoryUUID = uuidStr != null ? UUID.fromString(uuidStr) : null;
        this.categoryName = in.readString();
        this.parentCategory = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(categoryUUID != null ? categoryUUID.toString() : null);
        dest.writeString(categoryName);
        dest.writeString(parentCategory);
    }

    public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(categoryName, category.categoryName) && Objects.equals(parentCategory, category.parentCategory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryName, parentCategory);
    }
}
package com.example.eventplanner.data.model.solutions;

import androidx.annotation.NonNull;

import com.example.eventplanner.data.model.BaseEntityModel;

public class Category extends BaseEntityModel {
    private String name, description;

    //region Constructors

    public Category() { super(); }

    public Category(Integer id) { super(id); }

    public Category(Integer id, String name, String description) {
        super(id);
        this.name = name;
        this.description = description;
    }

    //endregion

    //region Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    //endregion

    @NonNull
    @Override
    public String toString() {
        return name;
    }

}

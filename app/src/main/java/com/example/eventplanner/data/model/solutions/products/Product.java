package com.example.eventplanner.data.model.solutions.products;

import com.example.eventplanner.data.model.BaseEntityModel;
import com.example.eventplanner.data.model.Category;
import com.example.eventplanner.data.model.users.ProviderModel;

public class Product extends BaseEntityModel {
    ProviderModel provider;
    Category category;
    String name, description, imageUrl;
    Double price, discount;
    Boolean available;


    //region Getters and Setters

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public ProviderModel getProvider() {
        return provider;
    }

    public void setProvider(ProviderModel provider) {
        this.provider = provider;
    }

    public int getProviderId() {
        return provider == null ? -1 : provider.getId();
    }

    //endregion

}

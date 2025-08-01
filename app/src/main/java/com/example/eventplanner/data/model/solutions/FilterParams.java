package com.example.eventplanner.data.model.solutions;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FilterParams {
    Integer provider;
    Set<Integer> category;
    Double price, minPrice, maxPrice;

    Integer page, size;
    String sort;

    public Map<String, String> asMap() {
        Map<String, String> params = new HashMap<>();

        if (provider != null)
            params.put("provider", provider.toString());

        if (category != null)
            params.put(
                    "category",
                    category.stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining(",")));

        if (price != null)
            params.put("price", price.toString());
        else {
            if (minPrice != null)
                params.put("minPrice", minPrice.toString());

            if (maxPrice != null)
                params.put("maxPrice", maxPrice.toString());
        }

        if (page != null)
            params.put("page", page.toString());

        if (size != null)
            params.put("size", size.toString());

        if (sort != null)
            params.put("sort", sort);

        return params;
    }

    //region Getters and Setters

    public Integer getProvider() {
        return provider;
    }

    public void setProvider(Integer provider) {
        this.provider = provider;
    }

    public Set<Integer> getCategory() {
        return category;
    }

    public void setCategory(Set<Integer> category) {
        this.category = category;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    //endregion
}

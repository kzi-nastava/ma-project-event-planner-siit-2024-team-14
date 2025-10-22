package com.example.eventplanner.data.model.solutions.products;

import java.util.List;

public class CreateProductRequest {
    public String name;
    public String description;
    public Double price;
    public Double discount;
    public Boolean visible;
    public Boolean available;
    public String status; // "PENDING" default
    public CategoryRef category;
    public List<Long> applicableEventTypeIds;

    public static class CategoryRef {
        public Long id;          // when selecting existing
        public String name;      // when proposing new
        public String description;
    }
}

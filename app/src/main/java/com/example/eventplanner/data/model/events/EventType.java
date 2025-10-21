package com.example.eventplanner.data.model.events;

import java.util.List;

public class EventType {
    private Long id;
    private String name;
    private String description;
    private boolean isActive;
    private List<CategoriesEtModel> categories;

    public EventType(Long id, String name, String description, boolean isActive, List<CategoriesEtModel> categories) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isActive = isActive;
        this.categories = categories;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public boolean isActive() { return isActive; }
    public List<CategoriesEtModel> getCategories() { return categories; }

    public void setActive(boolean active) { isActive = active; }
}

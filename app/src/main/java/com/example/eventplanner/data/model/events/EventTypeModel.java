package com.example.eventplanner.data.model.events;

import com.example.eventplanner.data.model.BaseEntityModel;

public class EventTypeModel extends BaseEntityModel {
    private String name;
    private String description;

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
}

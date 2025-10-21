package com.example.eventplanner.data.model.events;

import com.example.eventplanner.data.model.BaseEntityModel;

public class EventType extends BaseEntityModel {
    String name, description;

    //region Constructors

    public EventType() { super(); }

    public EventType(int id, String name, String description) {
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

}

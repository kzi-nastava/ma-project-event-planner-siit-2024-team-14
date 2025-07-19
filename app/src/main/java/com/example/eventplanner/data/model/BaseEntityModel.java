package com.example.eventplanner.data.model;

public abstract class BaseEntityModel {
    private Integer id;
    private Boolean isDeleted;

    //region Constructors

    protected BaseEntityModel() {}

    protected BaseEntityModel(int id) {
        this.id = id;
    }

    //endregion

    //region Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    //endregion
}

package com.example.eventplanner.data.model.events.budget;

import java.util.List;

public class BudgetModel {
    private double amount, spent;
    private List<BudgetItemModel> items;

    //region Getters and Setters

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getSpent() {
        return spent;
    }

    public void setSpent(double spent) {
        this.spent = spent;
    }

    public List<BudgetItemModel> getItems() {
        return items;
    }

    public void setItems(List<BudgetItemModel> items) {
        this.items = items;
    }

    //endregion

}

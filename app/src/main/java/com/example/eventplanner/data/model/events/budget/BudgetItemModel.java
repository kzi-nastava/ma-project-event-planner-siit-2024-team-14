package com.example.eventplanner.data.model.events.budget;

import com.example.eventplanner.data.model.solutions.Category;

import java.util.List;

public class BudgetItemModel {
    private Category category;
    private double amount, spent;
    private List<BudgetItemSolutionModel> items;

    //region Getters and Setters

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

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

    public List<BudgetItemSolutionModel> getItems() {
        return items;
    }

    public void setItems(List<BudgetItemSolutionModel> items) {
        this.items = items;
    }

    //endregion

}

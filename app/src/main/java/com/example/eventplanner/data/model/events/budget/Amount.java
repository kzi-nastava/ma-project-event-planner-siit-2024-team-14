package com.example.eventplanner.data.model.events.budget;


public class Amount {
    private Double amount;


    //region Constructors

    public Amount() {}

    public Amount(BudgetItemModel budgetItem) {
        this(budgetItem.getAmount());
    }

    public Amount(double amount) {
        this.amount = amount;
    }

    //endregion

    //region Getters and Setters

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    //endregion

}

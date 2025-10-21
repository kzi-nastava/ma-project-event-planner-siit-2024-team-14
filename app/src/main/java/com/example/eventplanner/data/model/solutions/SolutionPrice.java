package com.example.eventplanner.data.model.solutions;

import com.example.eventplanner.data.model.BaseEntityModel;

public class SolutionPrice extends BaseEntityModel {
    private String name, solutionType;
    private double price, discount;

    //region Getters and Setters

    public double getPriceWithDiscount() {
        return price * (discount > 1 ? discount / 100 : discount);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSolutionType() {
        return solutionType;
    }

    public void setSolutionType(String solutionType) {
        this.solutionType = solutionType;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    //endregion

}

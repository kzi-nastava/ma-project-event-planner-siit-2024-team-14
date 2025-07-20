package com.example.eventplanner.data.model.solutions.services;

import java.util.List;

public class CreateService {
    String
        name, description, specificities;
    Double price, discount;

    Category category;
    List<Integer> applicableEventTypeIds;

    Long
        durationMinutes, minDurationMinutes, maxDurationMinutes,
        reservationPeriodDays, cancellationPeriodDays;

    ReservationType reservationType;

    //region Constructors

    public CreateService() {}

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

    public String getSpecificities() {
        return specificities;
    }

    public void setSpecificities(String specificities) {
        this.specificities = specificities;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<Integer> getApplicableEventTypeIds() {
        return applicableEventTypeIds;
    }

    public void setApplicableEventTypeIds(List<Integer> applicableEventTypeIds) {
        this.applicableEventTypeIds = applicableEventTypeIds;
    }

    public Long getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Long durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Long getMinDurationMinutes() {
        return minDurationMinutes;
    }

    public void setMinDurationMinutes(Long minDurationMinutes) {
        this.minDurationMinutes = minDurationMinutes;
    }

    public Long getMaxDurationMinutes() {
        return maxDurationMinutes;
    }

    public void setMaxDurationMinutes(Long maxDurationMinutes) {
        this.maxDurationMinutes = maxDurationMinutes;
    }

    public Long getReservationPeriodDays() {
        return reservationPeriodDays;
    }

    public void setReservationPeriodDays(Long reservationPeriodDays) {
        this.reservationPeriodDays = reservationPeriodDays;
    }

    public Long getCancellationPeriodDays() {
        return cancellationPeriodDays;
    }

    public void setCancellationPeriodDays(Long cancellationPeriodDays) {
        this.cancellationPeriodDays = cancellationPeriodDays;
    }

    public ReservationType getReservationType() {
        return reservationType;
    }

    public void setReservationType(ReservationType reservationType) {
        this.reservationType = reservationType;
    }

    //endregion

    public static class Category {
        Integer id;
        String name, description;

        //region Constructors

        public Category(int id) {
            this.id = id;
        }

        public Category(String name, String description) {
            this.name = name;
            this.description = description;
        }

        //endregion

        //region Getters and Setters

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

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

}

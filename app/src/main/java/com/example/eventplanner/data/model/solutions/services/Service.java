package com.example.eventplanner.data.model.solutions.services;

import com.example.eventplanner.data.model.BaseEntityModel;
import com.example.eventplanner.data.model.Category;
import com.example.eventplanner.data.model.users.UserModel;
import com.example.eventplanner.data.model.events.EventType;
import com.example.eventplanner.data.model.solutions.Visibility;

import java.time.Duration;
import java.util.List;

public class Service extends BaseEntityModel {
    UserModel provider;

    String
            name, description, specificities, imageUrl;
    Double price, discount;

    Category category;
    List<EventType> applicableEventTypes;

    ReservationType reservationType;
    Visibility visibility;
    Boolean available, visible;

    Duration
        duration, minDuration, maxDuration,
        reservationPeriod, cancellationPeriod;
    Long
            durationMinutes, minDurationMinutes, maxDurationMinutes,
            reservationPeriodDays, cancellationPeriodDays;

    //region Constructors

    public Service() {}

    //endregion

    //region Getters and Setters

    public UserModel getProvider() {
        return provider;
    }

    public void setProvider(UserModel provider) {
        this.provider = provider;
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

    public String getSpecificities() {
        return specificities;
    }

    public void setSpecificities(String specificities) {
        this.specificities = specificities;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public List<EventType> getApplicableEventTypes() {
        return applicableEventTypes;
    }

    public void setApplicableEventTypes(List<EventType> applicableEventTypes) {
        this.applicableEventTypes = applicableEventTypes;
    }

    public ReservationType getReservationType() {
        return reservationType;
    }

    public void setReservationType(ReservationType reservationType) {
        this.reservationType = reservationType;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Duration getMinDuration() {
        return minDuration;
    }

    public void setMinDuration(Duration minDuration) {
        this.minDuration = minDuration;
    }

    public Duration getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(Duration maxDuration) {
        this.maxDuration = maxDuration;
    }

    public Duration getReservationPeriod() {
        return reservationPeriod;
    }

    public void setReservationPeriod(Duration reservationPeriod) {
        this.reservationPeriod = reservationPeriod;
    }

    public Duration getCancellationPeriod() {
        return cancellationPeriod;
    }

    public void setCancellationPeriod(Duration cancellationPeriod) {
        this.cancellationPeriod = cancellationPeriod;
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

    //endregion
}

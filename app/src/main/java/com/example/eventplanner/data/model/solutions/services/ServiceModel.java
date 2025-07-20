package com.example.eventplanner.data.model.solutions.services;

import com.example.eventplanner.data.model.Category;
import com.example.eventplanner.data.model.events.EventTypeModel;
import com.example.eventplanner.data.model.solutions.OfferingModel;
import com.example.eventplanner.data.model.users.ProviderModel;

import java.util.List;

public class ServiceModel extends OfferingModel {
    private int id;
    private Category category;
    private List<EventTypeModel> applicableEventTypes;
    private double price;
    private double discount;
    private List<String> imageURLs;

    private String visibility;
    private String reservationType;
    private boolean isAvailable;

    private String duration;
    private String minDuration;
    private String maxDuration;
    private String reservationPeriod;
    private String cancellationPeriod;

    private int durationInMinutes;
    private int minDurationInMinutes;
    private int maxDurationInMinutes;
    private int reservationPeriodInDays;
    private int cancellationPeriodInDays;

    private ProviderModel provider;
    private int providerId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<EventTypeModel> getApplicableEventTypes() {
        return applicableEventTypes;
    }

    public void setApplicableEventTypes(List<EventTypeModel> applicableEventTypes) {
        this.applicableEventTypes = applicableEventTypes;
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

    public List<String> getImageURLs() {
        return imageURLs;
    }

    public void setImageURLs(List<String> imageURLs) {
        this.imageURLs = imageURLs;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getReservationType() {
        return reservationType;
    }

    public void setReservationType(String reservationType) {
        this.reservationType = reservationType;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getMinDuration() {
        return minDuration;
    }

    public void setMinDuration(String minDuration) {
        this.minDuration = minDuration;
    }

    public String getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(String maxDuration) {
        this.maxDuration = maxDuration;
    }

    public String getReservationPeriod() {
        return reservationPeriod;
    }

    public void setReservationPeriod(String reservationPeriod) {
        this.reservationPeriod = reservationPeriod;
    }

    public String getCancellationPeriod() {
        return cancellationPeriod;
    }

    public void setCancellationPeriod(String cancellationPeriod) {
        this.cancellationPeriod = cancellationPeriod;
    }

    public int getDurationInMinutes() {
        return durationInMinutes;
    }

    public void setDurationInMinutes(int durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }

    public int getMinDurationInMinutes() {
        return minDurationInMinutes;
    }

    public void setMinDurationInMinutes(int minDurationInMinutes) {
        this.minDurationInMinutes = minDurationInMinutes;
    }

    public int getMaxDurationInMinutes() {
        return maxDurationInMinutes;
    }

    public void setMaxDurationInMinutes(int maxDurationInMinutes) {
        this.maxDurationInMinutes = maxDurationInMinutes;
    }

    public int getReservationPeriodInDays() {
        return reservationPeriodInDays;
    }

    public void setReservationPeriodInDays(int reservationPeriodInDays) {
        this.reservationPeriodInDays = reservationPeriodInDays;
    }

    public int getCancellationPeriodInDays() {
        return cancellationPeriodInDays;
    }

    public void setCancellationPeriodInDays(int cancellationPeriodInDays) {
        this.cancellationPeriodInDays = cancellationPeriodInDays;
    }

    public ProviderModel getProvider() {
        return provider;
    }

    public void setProvider(ProviderModel provider) {
        this.provider = provider;
    }

    public int getProviderId() {
        return providerId;
    }

    public void setProviderId(int providerId) {
        this.providerId = providerId;
    }
}

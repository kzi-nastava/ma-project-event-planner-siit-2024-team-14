package com.example.eventplanner.data.model.solutions.services;

import com.example.eventplanner.data.model.solutions.OfferingModel;

import java.util.List;

public class ServiceModel extends OfferingModel {


    private List<String> imageURLs;

    private String visibility;
    private String reservationType;
    private boolean isAvailable;

    private String duration;
    private String minDuration;
    private String maxDuration;
    private String reservationPeriod;
    private String cancellationPeriod;

    private int durationMinutes;
    private int minDurationMinutes;
    private int maxDurationMinutes;
    private int reservationPeriodDays;
    private int cancellationPeriodDays;

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

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public int getMinDurationMinutes() {
        return minDurationMinutes;
    }

    public void setMinDurationMinutes(int minDurationMinutes) {
        this.minDurationMinutes = minDurationMinutes;
    }

    public int getMaxDurationMinutes() {
        return maxDurationMinutes;
    }

    public void setMaxDurationMinutes(int maxDurationMinutes) {
        this.maxDurationMinutes = maxDurationMinutes;
    }

    public int getReservationPeriodDays() {
        return reservationPeriodDays;
    }

    public void setReservationPeriodDays(int reservationPeriodDays) {
        this.reservationPeriodDays = reservationPeriodDays;
    }

    public int getCancellationPeriodDays() {
        return cancellationPeriodDays;
    }

    public void setCancellationPeriodDays(int cancellationPeriodDays) {
        this.cancellationPeriodDays = cancellationPeriodDays;
    }

}

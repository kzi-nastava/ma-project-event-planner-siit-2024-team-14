package com.example.eventplanner.data.model;

public class BookingServiceRequestModel {
    private Long id;
    private String service;
    private String event;
    private String bookingDate;
    private String startTime;
    private int duration;
    private String confirmed;

    // Getters
    public Long getId() {
        return id;
    }

    public String getService() {
        return service;
    }

    public String getEvent() {
        return event;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public int getDuration() {
        return duration;
    }

    public String getConfirmed() {
        return confirmed;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setService(String service) {
        this.service = service;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setConfirmed(String confirmed) {
        this.confirmed = confirmed;
    }
}

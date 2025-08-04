package com.example.eventplanner.data.model.solutions.services;

public class BookingServiceModel {
    private int serviceId;
    private int eventId;
    private String bookingDate;
    private String startTime;
    private int duration;
    private String reservationType;

    public BookingServiceModel(int serviceId, int eventId, String bookingDate, String startTime, int duration, String reservationType) {
        this.serviceId = serviceId;
        this.eventId = eventId;
        this.bookingDate = bookingDate;
        this.startTime = startTime;
        this.duration = duration;
        this.reservationType = reservationType;
    }

    // Getteri ako zatrebaju
    public int getServiceId() { return serviceId; }
    public int getEventId() { return eventId; }
    public String getBookingDate() { return bookingDate; }
    public String getStartTime() { return startTime; }
    public int getDuration() { return duration; }
    public String getReservationType() { return reservationType; }

}

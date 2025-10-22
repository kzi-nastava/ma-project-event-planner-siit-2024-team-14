package com.example.eventplanner.data.model.event;

public class EventModel {
    public int id;
    public String name;
    public String description;
    public String location;
    public String startDate;

    public OrganizerLite organizer;
    public static class OrganizerLite {
        public String name;
    }
}

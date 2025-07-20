package com.example.eventplanner.data.model.events;

public class EventModel {
    private int id;
    private String organizerFirstName;
    private String organizerLastName;
    private int organizerId;
    private String name;
    private String description;
    private String location;
    private String startDate;
    private String endDate;
    private String imageUrl;
    private String organizerProfilePicture;
    private int maxParticipants;

    // Konstruktor bez parametara
    public EventModel() {
    }

    // Konstruktor sa svim parametrima (opciono)
    public EventModel(int id, String organizerFirstName, String organizerLastName, int organizerId,
                      String name, String description, String location, String startDate,
                      String endDate, String imageUrl, String organizerProfilePicture, int maxParticipants) {
        this.id = id;
        this.organizerFirstName = organizerFirstName;
        this.organizerLastName = organizerLastName;
        this.organizerId = organizerId;
        this.name = name;
        this.description = description;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
        this.imageUrl = imageUrl;
        this.organizerProfilePicture = organizerProfilePicture;
        this.maxParticipants = maxParticipants;
    }

    // Geteri i seteri

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrganizerFirstName() {
        return organizerFirstName;
    }

    public void setOrganizerFirstName(String organizerFirstName) {
        this.organizerFirstName = organizerFirstName;
    }

    public String getOrganizerLastName() {
        return organizerLastName;
    }

    public void setOrganizerLastName(String organizerLastName) {
        this.organizerLastName = organizerLastName;
    }

    public int getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(int organizerId) {
        this.organizerId = organizerId;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getOrganizerProfilePicture() {
        return organizerProfilePicture;
    }

    public void setOrganizerProfilePicture(String organizerProfilePicture) {
        this.organizerProfilePicture = organizerProfilePicture;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }
}

package com.example.eventplanner.data.model.users;

public class OrganizerModel {
    private int id;
    private String name;
    private String surname;
    private String email;
    private String address;
    private String city;
    private String profilePhoto;
    private String phoneNumber;

    // Getteri i setteri
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getProfilePhoto() { return profilePhoto; }
    public void setProfilePhoto(String profilePhoto) { this.profilePhoto = profilePhoto; }
}


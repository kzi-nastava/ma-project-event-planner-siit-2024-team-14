package com.example.eventplanner.data.model.profiles;

public class UpdateSppRequest {
    public int id;
    public String companyName;
    public String description;
    public String address;
    public String city;
    public String phoneNumber;

    public UpdateSppRequest(int id, String companyName, String description,
                                 String address, String city, String phoneNumber) {
        this.id = id;
        this.companyName = companyName;
        this.description = description;
        this.address = address;
        this.city = city;
        this.phoneNumber = phoneNumber;
    }
}

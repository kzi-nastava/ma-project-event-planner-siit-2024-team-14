package com.example.eventplanner.data.model.profiles;

public class UpdateEoRequest {
    private int id;
    private String name;
    private String surname;
    private String address;
    private String city;
    private String phoneNumber;

    public UpdateEoRequest(int id, String name, String surname, String address, String city, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.city = city;
        this.phoneNumber = phoneNumber;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getSurname() { return surname; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getPhoneNumber() { return phoneNumber; }
}

package com.example.eventplanner.data.model.registration;

public class RegistrationEoModel {
    private String email;
    private String password;
    private String confirmPassword;
    private String name;
    private String surname;
    private String address;
    private String city;
    private String phoneNumber;

    public RegistrationEoModel() {}

    public RegistrationEoModel(String email, String password, String confirmPassword,
                             String name, String surname, String address,
                             String city, String phoneNumber) {
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.city = city;
        this.phoneNumber = phoneNumber;
    }

    // getters/setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}

package com.example.eventplanner.data.model.registration;
public class RegistrationSppModel {
    public String email;
    public String password;
    public String confirmPassword;
    public String companyName;
    public String companyDescription;
    public String address;
    public String city;
    public String phoneNumber;

    public RegistrationSppModel(String email, String password, String confirmPassword,
                                String companyName, String companyDescription,
                                String address, String city, String phoneNumber) {
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.companyName = companyName;
        this.companyDescription = companyDescription;
        this.address = address;
        this.city = city;
        this.phoneNumber = phoneNumber;
    }
}

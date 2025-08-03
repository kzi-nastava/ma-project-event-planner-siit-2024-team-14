package com.example.eventplanner.data.model.users;

public class UpgradeProviderModel {
    private Integer id;
    private String email;
    private String password;
    private String confirmPassword;
    private String companyName;
    private String companyDescription;
    private String address;
    private String city;
    private String phoneNumber;

    public UpgradeProviderModel(Integer id, String email, String password, String confirmPassword,
                                String companyName, String companyDescription,
                                String address, String city, String phoneNumber) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.companyName = companyName;
        this.companyDescription = companyDescription;
        this.address = address;
        this.city = city;
        this.phoneNumber = phoneNumber;
    }

    // getters i setters ako su potrebni
}

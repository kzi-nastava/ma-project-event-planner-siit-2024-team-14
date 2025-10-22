package com.example.eventplanner.data.model.profiles;

public class ChangePasswordRequest {
    private String oldPassword;
    private String password;

    public ChangePasswordRequest(String oldPassword, String password) {
        this.oldPassword = oldPassword;
        this.password = password;
    }

    public String getOldPassword() { return oldPassword; }
    public String getPassword() { return password; }
}


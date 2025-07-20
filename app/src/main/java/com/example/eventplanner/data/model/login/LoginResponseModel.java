package com.example.eventplanner.data.model.login;

import com.example.eventplanner.data.model.users.UserModel;

public class LoginResponseModel {
    private String token;
    private UserModel user;
    private String message;
    private boolean success;

    public LoginResponseModel() {
    }

    public LoginResponseModel(String token, UserModel user, String message, boolean success) {
        this.token = token;
        this.user = user;
        this.message = message;
        this.success = success;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}

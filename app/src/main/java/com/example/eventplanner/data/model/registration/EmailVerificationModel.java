package com.example.eventplanner.data.model.registration;

public class EmailVerificationModel {
    private String token;
    public EmailVerificationModel(String token){ this.token = token; }
    public String getToken(){ return token; }
}


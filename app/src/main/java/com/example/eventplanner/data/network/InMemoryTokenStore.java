package com.example.eventplanner.data.network;

import androidx.annotation.Nullable;

import com.example.eventplanner.data.network.auth.TokenStore;

public class InMemoryTokenStore implements TokenStore {
    private String token;

    @Nullable
    @Override
    public String getToken() {
        return token;
    }

    @Override
    public void setToken(@Nullable String token) {
        this.token = token;
    }

}

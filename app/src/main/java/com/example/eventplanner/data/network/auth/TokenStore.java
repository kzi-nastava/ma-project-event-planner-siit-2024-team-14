package com.example.eventplanner.data.network.auth;

import androidx.annotation.Nullable;

public interface TokenStore extends TokenProvider {
    void setToken(@Nullable String token);
    default void clearToken() {
        setToken(null);
    }
}

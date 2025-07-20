package com.example.eventplanner.data.network.auth;

import androidx.annotation.Nullable;

@FunctionalInterface
public interface TokenProvider {
    @Nullable String getToken();
}

package com.example.eventplanner.data.network.auth;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private final TokenProvider tokenProvider;

    public AuthInterceptor(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }


    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        String jwt = tokenProvider.getToken();

        if (jwt == null)
            return chain.proceed(chain.request());

        Request requestWithAuth = chain.request().newBuilder()
                .header("Authorization", "Bearer " + jwt)
                .build();

        return chain.proceed(requestWithAuth);
    }

}

package com.example.eventplanner.data.network.auth;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.eventplanner.data.model.LoginDTO;
import com.example.eventplanner.data.model.LoginResponseDTO;
import com.example.eventplanner.data.model.UserDTO;

import java.time.Instant;
import java.util.Optional;

import com.auth0.jwt.JWT;
import com.example.eventplanner.data.network.services.user.UserService;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AuthService {
    private static final String
            TAG = AuthService.class.getSimpleName(),
            CLAIM_ID = "id",
            CLAIM_ROLE = "role";

    private final UserService authApi;
    private final TokenStore tokenStore;


    public AuthService(TokenStore tokenStore, UserService authApi) {
        this.authApi = authApi;
        this.tokenStore = tokenStore;
    }


    @Nullable
    public UserDTO getUser() {
        return Optional.ofNullable(getDecodedToken())
                .map(jwt -> {
                    UserDTO user = new UserDTO();
                    user.setId(jwt.getClaim(CLAIM_ID).asInt());
                    user.setEmail(jwt.getSubject());
                    user.setRole(jwt.getClaim(CLAIM_ROLE).asString());

                    return user;
                })
                .orElse(null);
    }


    public void login(LoginDTO loginDTO, @Nullable Callback<LoginResponseDTO> callback) {
        authApi.login(loginDTO).enqueue(new Callback<LoginResponseDTO>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponseDTO> call, @NonNull Response<LoginResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponseDTO responseDTO = response.body();
                    tokenStore.setToken(responseDTO.getToken());
                    Log.i(TAG, "Logged in as " + responseDTO.getUser().getEmail());
                } else {
                    Log.w(TAG, "Login failed: " + response.code() + " - " + response.message());
                }

                if (callback != null)
                    callback.onResponse(call, response);
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponseDTO> call, @NonNull Throwable t) {
                Log.w(TAG, "Login failed: " + t.getMessage());

                if (callback != null)
                    callback.onFailure(call, t);
            }
        });
    }

    public void login(String email, String password, @Nullable Callback<LoginResponseDTO> callback) {
        login(new LoginDTO(email, password), callback);
    }

    public void logout() {
        tokenStore.clearToken();
        Log.i(TAG, "Logged out");
    }


    @Nullable
    private DecodedJWT getDecodedToken() { // maybe move to token store
        String jwt = tokenStore.getToken();
        if (jwt == null || jwt.trim().isEmpty()) {
            return null;
        }

        try {
            DecodedJWT decodedJwt =  JWT.decode(jwt);
            // clear token if expired
            Instant expirationInstant = decodedJwt.getExpiresAtAsInstant();
            if (expirationInstant != null && expirationInstant.isBefore(Instant.now())) {
                Log.i(TAG, "Jwt expired");
                tokenStore.clearToken();
                return null;
            }

            return decodedJwt;
        } catch (JWTDecodeException e) {
            Log.e(TAG, "Invalid jwt", e);
            return null;
        }
    }

}

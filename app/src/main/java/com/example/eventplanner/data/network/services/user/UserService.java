package com.example.eventplanner.data.network.services.user;

import com.example.eventplanner.data.model.LoginDTO;
import com.example.eventplanner.data.model.LoginResponseDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserService {
    @POST("/api/users/login")
    Call<LoginResponseDTO> login(@Body LoginDTO loginDTO);
}

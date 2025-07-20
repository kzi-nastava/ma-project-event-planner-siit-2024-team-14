package com.example.eventplanner.data.network.services.user;

import com.example.eventplanner.data.model.login.LoginModel;
import com.example.eventplanner.data.model.login.LoginResponseModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserService {
    @POST("/api/users/login")
    Call<LoginResponseModel> login(@Body LoginModel loginModel);
}

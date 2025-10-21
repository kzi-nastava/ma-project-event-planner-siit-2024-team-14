package com.example.eventplanner.data.network.registration;

import com.example.eventplanner.data.model.registration.EmailVerificationModel;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ActivationApi {
    @POST("api/email-verification/verify")
    Call<ResponseBody> verifyPost(@Body EmailVerificationModel body);
}


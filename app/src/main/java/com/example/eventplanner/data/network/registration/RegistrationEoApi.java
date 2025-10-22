package com.example.eventplanner.data.network.registration;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RegistrationEoApi {
    @Multipart
    @POST("/api/organizers/register")
    Call<ResponseBody> registerEo(
            @Part MultipartBody.Part dto,
            @Part MultipartBody.Part photoOptional
    );
}


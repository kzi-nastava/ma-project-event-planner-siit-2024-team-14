package com.example.eventplanner.data.network.registration;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RegistrationSppApi {
    @Multipart
    @POST("api/providers/register") //
    Call<ResponseBody> register(
            @Part MultipartBody.Part dto,
            @Part List<MultipartBody.Part> photos
    );
}

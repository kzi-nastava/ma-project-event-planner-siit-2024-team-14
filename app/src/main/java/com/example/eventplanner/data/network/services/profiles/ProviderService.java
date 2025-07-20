package com.example.eventplanner.data.network.services.profiles;

import com.example.eventplanner.data.model.users.ProviderModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ProviderService {
    @GET("api/providers/{id}")
    Call<ProviderModel> getProviderById(@Path("id") int id);
}

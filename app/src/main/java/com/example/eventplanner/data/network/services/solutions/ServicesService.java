package com.example.eventplanner.data.network.services.solutions;

import com.example.eventplanner.data.model.ServiceModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ServicesService {
    @GET("api/services/{id}")
    Call<ServiceModel> getServiceById(@Path("id") int id);
}

package com.example.eventplanner.data.network.services.solutions;

import com.example.eventplanner.data.model.solutions.services.CreateService;
import com.example.eventplanner.data.model.solutions.services.Service;
import com.example.eventplanner.data.model.solutions.services.ServiceModel;
import com.example.eventplanner.data.model.solutions.services.UpdateService;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;


public interface ServiceService {
    String BASE_URL = "services";


    @GET(BASE_URL)
    Call<List<Service>> getAll();

    @GET("api/services/{id}")
    Call<ServiceModel> getServiceById(@Path("id") int id);

    @POST(BASE_URL)
    Call<Service> create(@Body CreateService service);

    @PUT(BASE_URL + "/{id}")
    Call<Service> update(@Path("id") int id, @Body UpdateService service);

    @DELETE(BASE_URL + "/{id}")
    Call<Void> deleteById(@Path("id") int id);

}

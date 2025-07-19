package com.example.eventplanner.data.network.services.solutions;

import com.example.eventplanner.data.model.Category;
import com.example.eventplanner.data.model.solutions.services.CreateService;
import com.example.eventplanner.data.model.solutions.services.Service;
import com.example.eventplanner.data.model.solutions.services.UpdateService;

import java.util.Collection;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CategoryService {
    String BASE_URL = "categories";

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("categories")
    @Deprecated
    Call<Collection<Category>> getAllCategories();

    @GET(BASE_URL)
    Call<List<Category>> getAll();

    @GET(BASE_URL + "/{id}")
    Call<Category> getById(@Path("id") int id);

    @POST(BASE_URL)
    Call<Category> create(@Body Category category);

    @PUT(BASE_URL + "/{id}")
    Call<Category> update(@Path("id") int id, @Body Category category);

    @DELETE(BASE_URL + "/{id}")
    Call<Void> deleteById(@Path("id") int id);

}

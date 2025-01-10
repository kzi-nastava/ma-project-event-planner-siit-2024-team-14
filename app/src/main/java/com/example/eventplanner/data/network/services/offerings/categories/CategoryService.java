package com.example.eventplanner.data.network.services.offerings.categories;

import com.example.eventplanner.data.model.Category;

import java.util.Collection;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CategoryService {

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type: application/json"
    })
    @GET("categories")
    Call<Collection<Category>> getAllCategories();

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type: application/json"
    })
    @GET("categories/{id}")
    Call<Category> getById(@Path("id") Integer id);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type: application/json"
    })
    @POST("categories")
    Call<Category> addCategory(@Body Category category);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type: application/json"
    })
    @PUT("categories")
    Call<Category> updateCategory(@Body Category category);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type: application/json"
    })
    @DELETE("categories/{id}")
    Call<Void> deleteCategory(@Path("id") Integer id);

    // TODO: See if an interceptor should add these headers
}

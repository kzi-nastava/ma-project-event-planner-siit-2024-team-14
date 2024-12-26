package com.example.eventplanner.data.network.api.offerings.categories;

import com.example.eventplanner.data.model.Category;

import java.util.Collection;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface CategoryService {

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("categories")
    Call<Collection<Category>> getAllCategories();

}

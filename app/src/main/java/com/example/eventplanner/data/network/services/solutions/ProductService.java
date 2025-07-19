package com.example.eventplanner.data.network.services.solutions;

import com.example.eventplanner.data.model.ProductModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ProductService {
    @GET("api/products/{id}")
    Call<ProductModel> getProductById(@Path("id") int id);
}

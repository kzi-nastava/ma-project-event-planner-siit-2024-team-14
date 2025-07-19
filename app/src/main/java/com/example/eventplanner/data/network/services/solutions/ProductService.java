package com.example.eventplanner.data.network.services.solutions;

import com.example.eventplanner.data.model.Page;
import com.example.eventplanner.data.model.solutions.products.Product;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface ProductService {
    String BASE_URL = "products";


    @GET(BASE_URL)
    Call<Page<Product>> getAll();

    @GET(BASE_URL)
    Call<Page<Product>> getAll(@QueryMap Map<String, String> params);

    @GET(BASE_URL + "/{id}")
    Call<Product> getById(@Path("id") int id);

    @DELETE(BASE_URL + "/{id}")
    Call<Void> deleteById(@Path("id") int id);

}

package com.example.eventplanner.data.network.services.solutions;

import com.example.eventplanner.data.model.Page;
import com.example.eventplanner.data.model.solutions.products.ProductModel;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface ProductService {
    String BASE_URL = "products";


    @GET(BASE_URL)
    Call<Page<ProductModel>> getAll();

    @GET(BASE_URL)
    Call<Page<ProductModel>> getAll(@QueryMap Map<String, String> params);

    @GET(BASE_URL + "/{id}")
    Call<ProductModel> getById(@Path("id") int id);

    @PUT(BASE_URL + "/{id}")
    Call<ProductModel> update(@Path("id") int id, @Body ProductModel product);

    @DELETE(BASE_URL + "/{id}")
    Call<Void> deleteById(@Path("id") int id);

}

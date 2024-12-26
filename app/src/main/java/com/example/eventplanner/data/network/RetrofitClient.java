package com.example.eventplanner.data.network;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.eventplanner.data.model.Category;
import com.example.eventplanner.data.network.api.offerings.categories.CategoryService;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitClient {
    public static final String SERVER_API_URL = "http://10.0.2.2:8080/api/";
    public static Retrofit retrofit;

    public static Supplier<OkHttpClient> clientProvider = RetrofitClient::testClient;




    static {
        OkHttpClient client = clientProvider.get();

        retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitClient.SERVER_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }


    @NonNull
    public static OkHttpClient testClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();

    }
}

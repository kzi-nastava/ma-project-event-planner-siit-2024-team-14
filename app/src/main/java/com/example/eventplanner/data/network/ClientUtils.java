package com.example.eventplanner.data.network;

import com.example.eventplanner.BuildConfig;

import androidx.annotation.NonNull;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ClientUtils {
    public static final String SERVER_API_URL = "http://" + BuildConfig.IP_ADDR + ":8080" + "/api/";
    public static Retrofit retrofit;



    static {
        OkHttpClient client = testClient();
        // TODO: Add interceptor for jwt
        retrofit = new Retrofit.Builder()
                .baseUrl(ClientUtils.SERVER_API_URL)
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

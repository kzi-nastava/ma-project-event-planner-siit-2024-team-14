// com/example/eventplanner/data/network/registration/ApiClient.java
package com.example.eventplanner.data.network.registration;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit;
    private static final String BASE_URL = "http://10.0.2.2:8080/";

    public static Retrofit get() {
        if (retrofit == null) {
            HttpLoggingInterceptor log = new HttpLoggingInterceptor(message ->
                    android.util.Log.d("HTTP", message));
            log.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient ok = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        // print the full URL to logcat
                        android.util.Log.d("HTTP",
                                "➡ " + chain.request().method() + " " + chain.request().url());
                        return chain.proceed(chain.request());
                    })
                    .addInterceptor(log)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(ok)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}

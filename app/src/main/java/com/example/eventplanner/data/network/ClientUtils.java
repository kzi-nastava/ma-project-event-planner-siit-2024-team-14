package com.example.eventplanner.data.network;

//import com.example.eventplanner.BuildConfig;

import com.example.eventplanner.data.network.auth.AuthInterceptor;
import com.example.eventplanner.data.network.auth.AuthService;
import com.example.eventplanner.data.network.auth.TokenStore;
import com.example.eventplanner.data.network.services.events.*;
import com.example.eventplanner.data.network.services.solutions.*;
import com.example.eventplanner.data.network.services.user.*;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ClientUtils {
    public static final String SERVER_API_URL = "http://" + "10.0.2.2" + ":8080" + "/api/"; // TODO: Move ip address to local properties


    public static OkHttpClient client() {
        Interceptor authInterceptor = new AuthInterceptor(Objects.requireNonNull(tokenStore));

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    Request newRequest = chain.request().newBuilder()
                            .header("User-Agent", "Mobile-Android")
                            .header("Content-Type", "application/json")
                            .build();

                    return chain.proceed(newRequest);
                })
                .addInterceptor(authInterceptor)
                .addInterceptor(loggingInterceptor)
                .build();
    }


    public static TokenStore tokenStore = new InMemoryTokenStore();

    public static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(SERVER_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client())
            .build();

    public static UserService userService = retrofit.create(UserService.class);
    public static AuthService authService = new AuthService(tokenStore, userService);
    public static EventService eventService = retrofit.create(EventService.class);
    public static CategoryService categoryService = retrofit.create(CategoryService.class);
    public static BookingServiceService bookingServiceService = retrofit.create(BookingServiceService.class);
    public static ServiceService serviceService = retrofit.create(ServiceService.class);
    public static ProductService productService = retrofit.create(ProductService.class);
    // ...

}

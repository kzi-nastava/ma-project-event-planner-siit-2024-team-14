package com.example.eventplanner.data.network.services.comments;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CommentApiClient {
    private static final String BASE_URL = "http://10.0.2.2:8080/";
    private static AdminCommentsService service;

    public static AdminCommentsService getInstance() {
        if (service == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            service = retrofit.create(AdminCommentsService.class);
        }
        return service;
    }
}

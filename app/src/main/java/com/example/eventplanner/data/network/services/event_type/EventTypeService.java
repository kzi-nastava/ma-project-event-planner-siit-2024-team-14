package com.example.eventplanner.data.network.services.event_type;

import com.example.eventplanner.data.model.events.CategoriesEtModel;
import com.example.eventplanner.data.model.events.EventType;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface EventTypeService {
    @GET("event-types/get-all-categories")
    Call<List<EventType>> getAllEventTypes();

    @GET("categories/get-all-et")
    Call<List<CategoriesEtModel>> getAllCategories();

    @PUT("event-types/update")
    Call<Void> updateEventType(@Body EventType event);

    @POST("event-types/create")
    Call<EventType> createEventType(@Body EventType event);

    @PUT("event-types/de-activate/{id}")
    Call<Void> toggleEventStatus(@Path("id") Long id);

    @GET("/api/event-types/get-all-categories")
    Call<List<EventType>> getAll();
}

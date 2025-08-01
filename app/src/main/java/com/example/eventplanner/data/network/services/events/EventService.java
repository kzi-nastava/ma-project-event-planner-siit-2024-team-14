package com.example.eventplanner.data.network.services.events;

import com.example.eventplanner.data.model.events.CategoriesEtModel;
import com.example.eventplanner.data.model.events.CreateEventModel;
import com.example.eventplanner.data.model.events.EventModel;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface EventService {
    @GET("events/by-organizer/{organizerId}")
    Call<List<EventModel>> getEventsByOrganizer(@Path("organizerId") int organizerId);

    @GET("events/{id}")
    Call<EventModel> getEventById(@Path("id") int id);
    @GET("categories/get-all-et")
    Call<List<CategoriesEtModel>> getAllCategories();

    @GET("event-types/get-all-event")
    Call<List<CategoriesEtModel>> getAllEventTypes();

    @GET("categories/get-by-event-type")
    Call<List<CategoriesEtModel>> getServicesAndProducts(@Query("eventType") String eventType);

    @Multipart
    @POST("events/create-event")
    Call<CreateEventModel> createEvent(
            @Part("dto") RequestBody dto,
            @Part MultipartBody.Part photo
    );

    @GET("events/joined/{id}")
    Call<List<EventModel>> getJoinedEvents(@Path("id") int id);
}

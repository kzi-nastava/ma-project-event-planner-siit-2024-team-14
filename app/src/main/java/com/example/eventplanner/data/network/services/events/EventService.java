package com.example.eventplanner.data.network.services.events;

import com.example.eventplanner.data.model.events.EventModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface EventService {
    @GET("api/events/by-organizer/{organizerId}")
    Call<List<EventModel>> getEventsByOrganizer(@Path("organizerId") int organizerId);

    @GET("api/events/{id}")
    Call<EventModel> getEventById(@Path("id") int id);
}

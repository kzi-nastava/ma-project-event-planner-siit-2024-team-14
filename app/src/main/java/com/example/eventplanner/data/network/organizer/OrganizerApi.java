package com.example.eventplanner.data.network.organizer;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import com.example.eventplanner.data.model.profiles.OrganizerModel;


public interface OrganizerApi {
    // uskladi rutu sa backendom (često: /api/organizers/{id})
    @GET("api/organizers/{id}")
    Call<OrganizerModel> getOrganizer(@Path("id") int id);
}

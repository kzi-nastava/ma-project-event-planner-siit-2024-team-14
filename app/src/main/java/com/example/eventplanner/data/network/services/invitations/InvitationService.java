package com.example.eventplanner.data.network.services.invitations;

import com.example.eventplanner.data.model.invitations.GroupedInvitationModel;
import com.example.eventplanner.data.model.invitations.InvitationModel;
import com.example.eventplanner.data.model.invitations.InvitationRequestModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface InvitationService {
    @POST("/api/invitations/bulk")
    Call<Void> sendInvitations(@Body InvitationRequestModel request);

    @GET("/api/invitations/by-organizer/{organizerId}")
    Call<List<GroupedInvitationModel>> getInvitationsForOrganizer(@Path("organizerId") int organizerId);

    @GET("/api/invitations/event/{eventId}")
    Call<List<InvitationModel>> getInvitationsForEvent(@Path("eventId") int eventId);

    @PUT("/api/invitations/{id}/status")
    Call<Void> updateInvitationStatus(@Path("id") int id, @Query("status") String status);
}

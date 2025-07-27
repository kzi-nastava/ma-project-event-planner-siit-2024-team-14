package com.example.eventplanner.data.network.services.invitations;

import com.example.eventplanner.data.model.invitations.InvitationRequestModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface InvitationService {
    @POST("/api/invitations/bulk")
    Call<Void> sendInvitations(@Body InvitationRequestModel request);
}

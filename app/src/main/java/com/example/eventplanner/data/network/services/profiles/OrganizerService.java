package com.example.eventplanner.data.network.services.profiles;
import com.example.eventplanner.data.model.events.EventModel;
import com.example.eventplanner.data.model.events.ToggleFavoriteResponse;
import com.example.eventplanner.data.model.profiles.ChangePasswordRequest;
import com.example.eventplanner.data.model.profiles.UpdateEoRequest;
import com.example.eventplanner.data.model.users.OrganizerModel;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface OrganizerService {
    @GET("organizers/{id}")
    Call<OrganizerModel> getOrganizerById(@Path("id") int id);

    @Multipart
    @POST("organizers/upgrade-to-organizer")
    Call<ResponseBody> upgradeToOrganizer(
            @Part("dto") RequestBody dto,
            @Part MultipartBody.Part photo
    );

    @PUT("organizers/update")
    Call<OrganizerModel> updateOrganizer(@Body UpdateEoRequest updateBody);

    @Multipart
    @PUT("organizers/update-photo/{id}")
    Call<Void> updatePhoto(@Path("id") int id, @Part MultipartBody.Part photo);

    @PUT("organizers/deactivate/{id}")
    Call<Void> deactivate(@Path("id") int id);

    @GET("organizers/{userId}/favorite-events")
    Call<List<EventModel>> getFavoriteEvents(@Path("userId") int userId);


    @POST("organizers/{userId}/favorite-events/{eventId}")
    Call<ToggleFavoriteResponse> toggleFavoriteEvent(@Path("userId") int userId,
                                                     @Path("eventId") int eventId);

}

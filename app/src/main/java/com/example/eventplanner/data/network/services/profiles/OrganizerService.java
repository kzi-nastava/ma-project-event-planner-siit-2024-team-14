package com.example.eventplanner.data.network.services.profiles;
import com.example.eventplanner.data.model.users.OrganizerModel;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
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

}

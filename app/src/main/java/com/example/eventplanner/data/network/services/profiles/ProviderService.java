package com.example.eventplanner.data.network.services.profiles;

import com.example.eventplanner.data.model.events.EventModel;
import com.example.eventplanner.data.model.profiles.UpdateSppRequest;
import com.example.eventplanner.data.model.users.ProviderModel;

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

public interface ProviderService {
    @GET("providers/{id}")
    Call<ProviderModel> getProviderById(@Path("id") int id);

    @Multipart
    @POST("providers/upgrade-to-provider")
    Call<ResponseBody> upgradeToProvider(
            @Part("dto") RequestBody dto,
            @Part List<MultipartBody.Part> photos
    );

    @GET("providers/{userId}/favorite-events")
    Call<List<EventModel>> getFavoriteEvents(@Path("userId") int userId);

    @Multipart
    @PUT("providers/update-photo/{id}")
    Call<Void> updatePhoto(@Path("id") int id, @Part MultipartBody.Part photo);

    @PUT("providers/update")
    Call<ProviderModel> updateProvider(@Body UpdateSppRequest body);

    @PUT("providers/deactivate/{id}")
    Call<Void> deactivate(@Path("id") int id);
}

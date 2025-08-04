package com.example.eventplanner.data.network.services.solutions;

import com.example.eventplanner.data.model.solutions.services.BookingServiceModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface BookingServiceService {
    @POST("bookings/reserve")
    Call<Void> reserveService(@Body BookingServiceModel request);

    @GET("bookings/available-start-times")
    Call<List<String>> getAvailableStartTimes(
            @Query("serviceId") int serviceId,
            @Query("date") String date,
            @Query("duration") int duration
    );
}

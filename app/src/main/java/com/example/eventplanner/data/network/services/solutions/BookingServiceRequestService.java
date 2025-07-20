package com.example.eventplanner.data.network.services.solutions;

import com.example.eventplanner.data.model.solutions.services.BookingServiceRequestModel;

import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;

public interface BookingServiceRequestService {

    @GET("api/bookings/requests")
    Call<List<BookingServiceRequestModel>> getAllRequests();

    @PUT("api/bookings/approve")
    Call<Void> approveRequest(@Body Map<String, Object> body);

    @PUT("api/bookings/delete")
    Call<Void> deleteRequest(@Body Map<String, Object> body);

    @GET("api/bookings/all")
    Call<List<BookingServiceRequestModel>> getAllBookings();

}

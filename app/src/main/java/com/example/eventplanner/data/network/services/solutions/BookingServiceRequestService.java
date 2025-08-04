package com.example.eventplanner.data.network.services.solutions;

import com.example.eventplanner.data.model.solutions.services.BookingServiceRequestModel;

import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;

public interface BookingServiceRequestService {

    @GET("bookings/requests")
    Call<List<BookingServiceRequestModel>> getAllRequests();

    @PUT("bookings/approve")
    Call<Void> approveRequest(@Body Map<String, Object> body);

    @PUT("bookings/delete")
    Call<Void> deleteRequest(@Body Map<String, Object> body);

    @GET("bookings/all")
    Call<List<BookingServiceRequestModel>> getAllBookings();

}

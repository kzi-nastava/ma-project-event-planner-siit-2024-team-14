package com.example.eventplanner.data.network.reports;

import com.example.eventplanner.data.model.ReportModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ReportUserService {
    @POST("api/reports")
    Call<Void> reportUser(@Body ReportModel report);
}

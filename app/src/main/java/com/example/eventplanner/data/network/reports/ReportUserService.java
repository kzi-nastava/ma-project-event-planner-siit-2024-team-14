package com.example.eventplanner.data.network.reports;

import com.example.eventplanner.data.model.reports.ReportModel;
import com.example.eventplanner.data.model.reports.ReportUserModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface ReportUserService {
    @POST("api/reports")
    Call<Void> reportUser(@Body ReportModel report);
    @GET("api/reports/pending")
    Call<List<ReportUserModel>> getAllReports();

    @PUT("api/reports/approve")
    Call<Void> approveReportStatus(@Body ReportActionBody body);

    @PUT("api/reports/delete")
    Call<Void> deleteReportStatus(@Body ReportActionBody body);

    class ReportActionBody {
        public int reportId;
        public String status;

        public ReportActionBody(int reportId, String status) {
            this.reportId = reportId;
            this.status = status;
        }
    }
}

package com.example.eventplanner.data.network.services.reports;

import com.example.eventplanner.data.model.reports.ReportModel;
import com.example.eventplanner.data.model.reports.ReportUserModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface ReportUserService {
    @POST("reports")
    Call<Void> reportUser(@Body ReportModel report);
    @GET("reports/pending")
    Call<List<ReportUserModel>> getAllReports();

    @PUT("reports/approve")
    Call<Void> approveReportStatus(@Body ReportActionBody body);

    @PUT("reports/delete")
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

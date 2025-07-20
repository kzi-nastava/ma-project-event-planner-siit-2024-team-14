package com.example.eventplanner.data.model;

public class ReportStatusUpdateModel {
    private Integer reportId;
    private String status;

    public ReportStatusUpdateModel(Integer reportId, String status) {
        this.reportId = reportId;
        this.status = status;
    }

    public Integer getReportId() {
        return reportId;
    }

    public String getStatus() {
        return status;
    }
}

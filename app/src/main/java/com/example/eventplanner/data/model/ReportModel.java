package com.example.eventplanner.data.model;

public class ReportModel {
    private Integer senderId;
    private Integer reportedUserId;
    private String reason;

    public ReportModel(Integer senderId, Integer reportedUserId, String reason) {
        this.senderId = senderId;
        this.reportedUserId = reportedUserId;
        this.reason = reason;
    }

}

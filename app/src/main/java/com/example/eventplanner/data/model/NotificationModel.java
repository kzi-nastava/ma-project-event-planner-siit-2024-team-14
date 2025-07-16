package com.example.eventplanner.data.model;

public class NotificationModel {
    private Integer id;
    private Integer userId;
    private String message;
    private boolean read;
    private String date;
    private Integer commentId;
    private Integer eventId;

    public Integer getId() { return id; }
    public Integer getUserId() { return userId; }
    public String getMessage() { return message; }
    public boolean getRead() { return read; }

    public String getDate() { return date; }
    public Integer getCommentId() { return commentId; }
    public Integer getEventId() { return eventId; }

    public void setId(Integer id) { this.id = id; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public void setMessage(String message) { this.message = message; }
    public void setRead(boolean read) { this.read = read; }
    public void setDate(String date) { this.date = date; }
    public void setCommentId(Integer commentId) { this.commentId = commentId; }
    public void setEventId(Integer eventId) { this.eventId = eventId; }
}

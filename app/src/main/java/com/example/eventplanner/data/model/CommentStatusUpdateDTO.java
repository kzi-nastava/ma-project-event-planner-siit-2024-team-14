package com.example.eventplanner.data.model;

public class CommentStatusUpdateDTO {
    private Long commentId;
    private String status;

    public CommentStatusUpdateDTO(Long commentId, String status) {
        this.commentId = commentId;
        this.status = status;
    }

    public Long getCommentId() {
        return commentId;
    }

    public String getStatus() {
        return status;
    }
}

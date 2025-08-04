package com.example.eventplanner.data.model.comments;

public class CommentStatusUpdateModel {
    private Long commentId;
    private String status;

    public CommentStatusUpdateModel(Long commentId, String status) {
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

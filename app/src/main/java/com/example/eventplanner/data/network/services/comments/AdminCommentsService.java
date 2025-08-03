package com.example.eventplanner.data.network.services.comments;

import com.example.eventplanner.data.model.comments.CommentModel;
import com.example.eventplanner.data.model.comments.CommentStatusUpdateModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;

public interface AdminCommentsService {
    @GET("comments/pending")
    Call<List<CommentModel>> getPendingComments();

    @PUT("comments/approve")
    Call<Void> approveComment(@Body CommentStatusUpdateModel update);

    @PUT("comments/delete")
    Call<Void> deleteComment(@Body CommentStatusUpdateModel update);
}

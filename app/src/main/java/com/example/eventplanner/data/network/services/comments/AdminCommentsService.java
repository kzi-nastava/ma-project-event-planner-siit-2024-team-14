package com.example.eventplanner.data.network.services.comments;

import com.example.eventplanner.data.model.CommentDTO;
import com.example.eventplanner.data.model.CommentStatusUpdateDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;

public interface AdminCommentsService {
    @GET("api/comments/pending")
    Call<List<CommentDTO>> getPendingComments();

    @PUT("api/comments/approve")
    Call<Void> approveComment(@Body CommentStatusUpdateDTO update);

    @PUT("api/comments/delete")
    Call<Void> deleteComment(@Body CommentStatusUpdateDTO update);
}

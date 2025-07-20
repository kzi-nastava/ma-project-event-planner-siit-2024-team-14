package com.example.eventplanner.data.network.services.chat;

import com.example.eventplanner.data.model.chat.MessageModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ChatService {
    @GET("api/messages/{senderId}/{receiverId}")
    Call<List<MessageModel>> getMessagesBetweenUsers(
            @Path("senderId") int senderId,
            @Path("receiverId") int receiverId
    );

    @POST("api/messages/send")
    Call<MessageModel> sendMessage(@Body MessageModel message);
}

package com.example.eventplanner.data.network.services.chat;

import com.example.eventplanner.data.model.chat.MessageModel;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ChatService {
    @GET("messages/{senderId}/{receiverId}")
    Call<List<MessageModel>> getMessagesBetweenUsers(
            @Path("senderId") int senderId,
            @Path("receiverId") int receiverId
    );

    @POST("messages/send")
    Call<MessageModel> sendMessage(@Body MessageModel message);

    @POST("chat/{senderId}/block/{receiverId}")
    Call<ResponseBody> blockUser(
            @Path("senderId") int senderId,
            @Path("receiverId") int receiverId
    );


}

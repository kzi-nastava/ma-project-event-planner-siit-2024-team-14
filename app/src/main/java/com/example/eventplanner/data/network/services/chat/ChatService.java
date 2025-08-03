package com.example.eventplanner.data.network.services.chat;

import com.example.eventplanner.data.model.chat.ChatModel;
import com.example.eventplanner.data.model.chat.MessageModel;
import com.example.eventplanner.data.model.chat.SendMessage;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ChatService {

    /// @deprecated Use {@link ChatService#getChat} instead.
    @GET("messages/{senderId}/{receiverId}")
    @Deprecated
    Call<List<MessageModel>> getMessagesBetweenUsers(
            @Path("senderId") int senderId,
            @Path("receiverId") int receiverId
    );

    @POST("messages/send")
    @Deprecated
    Call<MessageModel> sendMessage(@Body MessageModel message);

    @POST("chat/{senderId}/block/{receiverId}")
    Call<ResponseBody> blockUser(
            @Path("senderId") int senderId,
            @Path("receiverId") int receiverId
    );

    @GET("chat")
    Call<List<ChatModel>> getMyInbox();

    @GET("chat/{recipientId}")
    Call<ChatModel> getChat(@Path("recipientId") int recipientId);

    /// @deprecated Use only for debug, send message with stomp instead.
    @POST("chat/{recipientId}")
    @Deprecated
    Call<MessageModel> sendMessage(@Path("recipientId") int recipientId, @Body SendMessage message);

}

package com.example.eventplanner.ui.fragment.chat;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.data.model.chat.ChatModel;
import com.example.eventplanner.data.model.chat.MessageModel;
import com.example.eventplanner.data.model.chat.SendMessage;
import com.example.eventplanner.data.model.users.UserModel;
import com.example.eventplanner.data.network.services.chat.ChatService;
import com.example.eventplanner.data.network.stomp.StompService;
import com.google.gson.Gson;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class ChatViewModel extends ViewModel {
    private final int chatterId, userId;
    private String topic = "/queue/%d", destination = "/chat/%d";

    private final ChatService chatService;
    private final StompService stompService;

    public final MutableLiveData<MessageModel> message = new MutableLiveData<>();
    public final MutableLiveData<ChatModel> chat = new MutableLiveData<>();
    public final MutableLiveData<Boolean> blockedChatter = new MutableLiveData<>(false);
    public final MutableLiveData<String> error = new MutableLiveData<>();



    public ChatViewModel(int userId, int chatterId, ChatService chatService, StompService stompService) {
        this.userId = userId;
        this.chatterId = chatterId;
        this.chatService = chatService;
        this.stompService = stompService;

        topic = String.format(topic, userId);
        destination = String.format(destination, chatterId);
        subscribeToChat();
    }



    public void fetchMessages() {
        chatService.getChat(chatterId).enqueue(new Callback<ChatModel>() {
            @Override
            public void onResponse(@NonNull Call<ChatModel> call, @NonNull Response<ChatModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    chat.postValue(response.body());
                    // post blocked if user blocked chatter or vice versa
                } else {
                    error.postValue("Failed to get user inbox. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ChatModel> call, @NonNull Throwable t) {
                error.postValue(t.getMessage());
            }
        });
    }


    public void blockChatter() {
        chatService.blockUser(userId, chatterId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful())
                    blockedChatter.postValue(true);
                else
                    error.postValue("Failed to block chatter");
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                error.postValue(t.getMessage());
            }
        });
    }


    private final CompositeDisposable disposables = new CompositeDisposable();

    public void sendMessage(String content) {
        if (content == null || content.isBlank()) {
            // error.postValue("Cannot send blank message");
            return;
        }

        Disposable disposable = stompService.sendMessage(destination, new SendMessage(userId, chatterId, content.trim()))
                .subscribe(
                        () -> {},
                        throwable -> error.postValue("Failed to send message to " + destination + ": " + throwable.getMessage())
                );

        disposables.add(disposable);
    }


    public UserModel getChatter() {
        return Optional.ofNullable(chat.getValue())
                .map(chat -> // bad design, server should probably change the recipient/sender based on who asked for the chat, shouldn't even return user just recipient
                        Objects.equals(chat.getRecipient().getId(), chatterId) ? chat.getRecipient() : chat.getSender()
                )
                .orElse(null);
    }


    private void subscribeToChat() {
        // NOTE: Pretty bad setup for topics, but gotta deal with it

        Disposable disposable = stompService.subscribe(topic)
                // parse message
                .map(stompMessage ->
                        new Gson().fromJson(stompMessage.getPayload(), MessageModel.class)
                )
                // filter only messages from this chat
                .filter(this::isFromChat)
                .subscribe(
                        message::postValue,
                        throwable -> error.postValue("Error on topic " + topic + ": " + throwable.getMessage())
                );

        disposables.add(disposable);
    }

    private boolean isFromChat(MessageModel message) {
        // BUG: if we haven't loaded the chat, we may accept messages that user sends to other chatters

        return Optional.ofNullable(chat.getValue())
                .map(ChatModel::getId)
                .map(chatId -> chatId.equals(message.getChatId()))
                .orElse(Set.of(chatterId, userId).contains(message.getSenderId()));
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }

}

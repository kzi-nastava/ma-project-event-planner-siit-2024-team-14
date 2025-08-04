package com.example.eventplanner.ui.fragment.chat;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.data.model.chat.ChatModel;
import com.example.eventplanner.data.model.chat.MessageModel;
import com.example.eventplanner.data.model.users.UserModel;
import com.example.eventplanner.data.network.ClientUtils;
import com.example.eventplanner.data.network.services.chat.ChatService;
import com.example.eventplanner.data.network.stomp.StompService;
import com.google.gson.Gson;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import io.reactivex.disposables.Disposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InboxViewModel extends ViewModel {
    private static final ChatService chatService = ClientUtils.chatService;

    public final MutableLiveData<List<ChatModel>> inbox = new MutableLiveData<>();
    public final MutableLiveData<String> error = new MutableLiveData<>();


    public void fetchInbox() {
        chatService.getMyInbox().enqueue(new Callback<List<ChatModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<ChatModel>> call, @NonNull Response<List<ChatModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    inbox.postValue(response.body());
                } else if (response.code() == 401) {
                    error.postValue("Failed to get user inbox. Not logged in.");
                } else {
                    error.postValue("Failed to get user inbox. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ChatModel>> call, @NonNull Throwable t) {
                error.postValue(t.getMessage());
            }
        });
    }


    private Disposable disposable;

    {
        // reload inbox if user received a message from someone new

        Optional.ofNullable(ClientUtils.authService.getUser())
                .map(UserModel::getId)
                .ifPresent(userId -> {
                    StompService stomp = ClientUtils.stompService;
                    stomp.connect(); // just in case
                    disposable = stomp.subscribe("/queue/" + userId)
                            .map(stompMessage ->
                                    new Gson().fromJson(stompMessage.getPayload(), MessageModel.class)
                            )
                            .subscribe(
                                    message -> {
                                        // if message is from new chat
                                        if (inbox.getValue() == null || inbox.getValue().stream().noneMatch(chat -> Objects.equals(chat.getId(), message.getChatId()))) {
                                            fetchInbox();
                                        }
                                    },
                                    throwable -> error.postValue("Error on  topic /queue/" + userId + ": " + throwable.getMessage())
                            );
                });
    }


    @Override
    protected void onCleared() {
        super.onCleared();

        if (disposable != null && !disposable.isDisposed())
            disposable.dispose();
    }

}

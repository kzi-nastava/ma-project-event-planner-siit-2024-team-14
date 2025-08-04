package com.example.eventplanner.ui.fragment.chat;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventplanner.data.network.ClientUtils;


public class ChatViewModelFactory implements ViewModelProvider.Factory {
    private final int userId, chatterId;


    public ChatViewModelFactory(int userId, int chatterId) {
        this.userId = userId;
        this.chatterId = chatterId;
    }


    @SuppressLint("DefaultLocale")
    public String getKey() {
        return String.format("chatWith-%d", chatterId);
    }


    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ChatViewModel.class))
            return (T) new ChatViewModel(userId, chatterId, ClientUtils.chatService, ClientUtils.stompService);

        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }

}

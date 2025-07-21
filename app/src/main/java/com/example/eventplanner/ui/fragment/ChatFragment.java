package com.example.eventplanner.ui.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.data.model.chat.MessageModel;
import com.example.eventplanner.data.network.ClientUtils;
import com.example.eventplanner.data.network.services.chat.ChatService;
import com.example.eventplanner.ui.adapter.MessageAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private EditText messageInput;
    private ImageButton sendButton;
    private MessageAdapter messageAdapter;
    private ChatService messagingService;

    private int currentUserId;
    private int receiverId;

    public ChatFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            receiverId = getArguments().getInt("receiverId", -1);
        }
    }

    public static ChatFragment newInstance(int receiverId) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putInt("receiverId", receiverId);
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", 0);
        currentUserId = prefs.getInt("userId", -1);
        recyclerView = view.findViewById(R.id.recyclerViewMessages);
        messageInput = view.findViewById(R.id.editTextMessage);
        sendButton = view.findViewById(R.id.buttonSend);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        messageAdapter = new MessageAdapter(getContext());
        recyclerView.setAdapter(messageAdapter);

        messagingService = ClientUtils.chatService;

        loadMessages();

        sendButton.setOnClickListener(v -> sendMessage());

        return view;
    }

    private void loadMessages() {
        messagingService.getMessagesBetweenUsers(currentUserId, receiverId)
                .enqueue(new Callback<List<MessageModel>>() {
                    @Override
                    public void onResponse(Call<List<MessageModel>> call, Response<List<MessageModel>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            messageAdapter.setMessages(response.body());
                            recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
                        } else {
                            Toast.makeText(getContext(), "Failed to load messages", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<MessageModel>> call, Throwable t) {
                        Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendMessage() {
        String text = messageInput.getText().toString().trim();
        if (TextUtils.isEmpty(text)) return;

        MessageModel message = new MessageModel(currentUserId, receiverId, text);

        messagingService.sendMessage(message).enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(Call<MessageModel> call, Response<MessageModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    messageAdapter.addMessage(response.body());
                    recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
                    messageInput.setText("");
                } else {
                    Toast.makeText(getContext(), "Failed to send message", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MessageModel> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}



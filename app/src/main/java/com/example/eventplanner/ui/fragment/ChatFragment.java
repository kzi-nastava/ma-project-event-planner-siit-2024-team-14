package com.example.eventplanner.ui.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;
import com.example.eventplanner.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.data.model.chat.MessageModel;
import com.example.eventplanner.data.network.ClientUtils;
import com.example.eventplanner.data.network.services.chat.ChatService;
import com.example.eventplanner.ui.adapter.MessageAdapter;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private EditText messageInput;
    private ImageButton sendButton;
    private MessageAdapter messageAdapter;
    private ChatService chatService;

    private int currentUserId;
    private int receiverId;

    public ChatFragment() {}

    public static ChatFragment newInstance(int receiverId) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putInt("receiverId", receiverId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", 0);
        currentUserId = prefs.getInt("userId", -1);

        if (getArguments() != null) {
            receiverId = getArguments().getInt("receiverId", -1);
        }

        chatService = ClientUtils.chatService;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewMessages);
        messageInput = view.findViewById(R.id.editTextMessage);
        sendButton = view.findViewById(R.id.buttonSend);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        messageAdapter = new MessageAdapter(getContext(), currentUserId);
        recyclerView.setAdapter(messageAdapter);

        loadMessages();

        sendButton.setOnClickListener(v -> sendMessage());

        ImageButton optionsButton = view.findViewById(R.id.buttonOptions);
        optionsButton.setOnClickListener(this::showOptionsMenu);

        return view;
    }

    private void loadMessages() {
        chatService.getMessagesBetweenUsers(currentUserId, receiverId)
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
        sendButton.setEnabled(false);

        chatService.sendMessage(message).enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(Call<MessageModel> call, Response<MessageModel> response) {
                sendButton.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    messageAdapter.addMessage(response.body());
                    recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
                    messageInput.setText("");
                } else {
                    Toast.makeText(getContext(), "Sending message failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MessageModel> call, Throwable t) {
                sendButton.setEnabled(true);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showOptionsMenu(View anchor) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), anchor);
        popupMenu.inflate(R.menu.popup_menu_chat);
        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_block) {
                blockUser();
                return true;
            } else if (id == R.id.menu_view_profile) {
                viewProfile();
                return true;
            } else if (id == R.id.menu_delete_messages) {
                deleteMessages();
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    private void blockUser() {
        chatService.blockUser(currentUserId, receiverId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String body = response.body().string().trim();
                        if ("Success".equals(body)) {
                            Toast.makeText(requireActivity().getApplicationContext(), "User has been successfully blocked", Toast.LENGTH_LONG).show();

                            recyclerView.postDelayed(() -> {
                                requireActivity()
                                        .getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.home_page_fragment, new HomeFragment())
                                        .commit();
                            }, 1200);

                        } else {
                            Toast.makeText(requireContext(), "Blocking failed: " + body, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Error reading response", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Unsuccessful server response", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(requireContext(), "Blocking error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void viewProfile() {
        Toast.makeText(getContext(), "Clicked: View profile", Toast.LENGTH_SHORT).show();
        // TODO: add profile page
    }

    private void deleteMessages() {
        messageAdapter.clearMessages();
        Toast.makeText(getContext(), "Messages have been removed (only from the view)", Toast.LENGTH_SHORT).show();
    }
}

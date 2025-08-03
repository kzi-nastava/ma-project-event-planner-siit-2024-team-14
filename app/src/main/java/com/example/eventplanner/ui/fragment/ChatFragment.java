package com.example.eventplanner.ui.fragment;

import static com.example.eventplanner.data.model.users.UserModel.ROLE_ORGANIZER;
import static com.example.eventplanner.data.model.users.UserModel.ROLE_PROVIDER;
import static com.example.eventplanner.ui.util.Util.toastError;

import android.os.Bundle;
import android.text.TextUtils;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.data.model.chat.MessageModel;
import com.example.eventplanner.data.model.users.UserModel;
import com.example.eventplanner.data.network.ClientUtils;
import com.example.eventplanner.data.network.auth.AuthService;
import com.example.eventplanner.data.network.services.chat.ChatService;
import com.example.eventplanner.databinding.FragmentChatBinding;
import com.example.eventplanner.ui.adapter.MessageAdapter;

import java.util.Optional;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends Fragment {

    private ChatViewModel viewModel;
    private FragmentChatBinding binding;
    private final AuthService authService = ClientUtils.authService;
    private MessageObserver messageObserver; // in order to sub to incoming messages only after we load the inbox

    private RecyclerView recyclerView;
    private EditText messageInput;
    private ImageButton sendButton;
    private MessageAdapter messageAdapter;
    private final ChatService chatService = ClientUtils.chatService;

    private Integer currentUserId;
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

        receiverId = requireArguments().getInt("receiverId", -1);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentChatBinding.inflate(inflater, container, false);

        recyclerView = binding.recyclerViewMessages;
        messageInput = binding.editTextMessage;
        sendButton = binding.buttonSend;

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        currentUserId = Optional.ofNullable(authService.getUser()).map(UserModel::getId).orElse(null);
        if (currentUserId == null) {
            view.post(() ->
                    FragmentTransition.to(new HomeFragment(), requireActivity(), R.id.home_page_fragment)
            );

            return;
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        messageAdapter = new MessageAdapter(getContext(), currentUserId);
        recyclerView.setAdapter(messageAdapter);

        sendButton.setOnClickListener(v -> sendMessage());

        ImageButton optionsButton = binding.buttonOptions;
        optionsButton.setOnClickListener(this::showOptionsMenu);

        ChatViewModelFactory factory = new ChatViewModelFactory(currentUserId, receiverId);
        viewModel = new ViewModelProvider(this, factory)
                .get(factory.getKey(), ChatViewModel.class);

        viewModel.chat.observe(getViewLifecycleOwner(), chat -> {
            messageAdapter.setMessages(chat.getMessages());
            recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);

            Optional.ofNullable(viewModel.getChatter())
                    .ifPresent(chatter -> {
                        binding.textName.setText("");
                        binding.textEmail.setText(chatter.getEmail());
                        binding.textRole.setText(chatter.getRole());
                    });

            if (messageObserver == null)
                viewModel.message.observeForever(messageObserver = new MessageObserver());
        });

        viewModel.error.observe(getViewLifecycleOwner(), toastError(requireContext()));
        viewModel.error.observe(getViewLifecycleOwner(), err -> sendButton.setEnabled(true)); // in case we failed to send a message, allow retries
        viewModel.fetchMessages(); // ahh, to avoid missing some messages (should probably add them to chat in the vm, and just observe chat)
    }

    private void sendMessage() {
        String text = messageInput.getText().toString().trim();
        if (TextUtils.isEmpty(text)) return;

        sendButton.setEnabled(false);
        viewModel.sendMessage(text);
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
        // if chatter is a Provider or Organizer navigate to their profile
        Optional.ofNullable(viewModel.getChatter())
                .ifPresent(chatter -> {
                    Fragment profileFragment;
                    Bundle args = new Bundle();

                    switch (chatter.getRole()) {
                        case ROLE_PROVIDER:
                        {
                            profileFragment = new ViewProviderProfileFragment();
                            args.putInt("providerId", chatter.getId());
                            break;
                        }
                        case ROLE_ORGANIZER:
                        {
                            profileFragment = new ViewOrganizerProfileFragment();
                            args.putInt("organizerId", chatter.getId());
                            break;
                        }
                        default:
                            return;
                    }

                    profileFragment.setArguments(args);
                    FragmentTransition.to(
                            profileFragment,
                            requireActivity(),
                            R.id.home_page_fragment,
                            true
                    );
                });
    }

    private void deleteMessages() {
        messageAdapter.clearMessages();
        Toast.makeText(getContext(), "Messages have been removed (only from the view)", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Optional.ofNullable(messageObserver)
                .ifPresent(viewModel.message::removeObserver);
    }


    private class MessageObserver implements Observer<MessageModel> {
        @Override
        public void onChanged(MessageModel messageModel) {
            messageAdapter.addMessage(messageModel);
            recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);

            binding.editTextMessage.setText("");
            sendButton.setEnabled(true);
        }
    }

}

package com.example.eventplanner.ui.fragment.au;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventplanner.R;
import com.example.eventplanner.data.network.ClientUtils;
import com.example.eventplanner.ui.activity.LoginActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InvitationRegisterFragment extends Fragment {

    private EditText emailEditText, passwordEditText, confirmPasswordEditText, fullNameEditText;
    private Button registerButton;
    private TextView loginRedirectText;

    private String email;
    private long eventId;

    public static InvitationRegisterFragment newInstance(String email, long eventId) {
        InvitationRegisterFragment fragment = new InvitationRegisterFragment();
        Bundle args = new Bundle();
        args.putString("email", email);
        args.putLong("eventId", eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_invitation_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emailEditText = view.findViewById(R.id.emailEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        confirmPasswordEditText = view.findViewById(R.id.confirmPasswordEditText);
        fullNameEditText = view.findViewById(R.id.fullNameEditText);
        registerButton = view.findViewById(R.id.registerButton);
        loginRedirectText = view.findViewById(R.id.loginRedirectText);

        if (getArguments() != null) {
            email = getArguments().getString("email", "");
            eventId = getArguments().getLong("eventId", -1);
            emailEditText.setText(email);
        }

        registerButton.setOnClickListener(v -> register());

        loginRedirectText.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            Toast.makeText(getContext(), "Check your email to activate your account.", Toast.LENGTH_LONG).show();
        });

    }

    private void register() {
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        String fullName = fullNameEditText.getText().toString().trim();

        if (TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword) || TextUtils.isEmpty(fullName)) {
            Toast.makeText(getContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare data object to send
        // You might need to create a DTO class InvitationRegisterRequest for this (see below)

        InvitationRegisterRequest data = new InvitationRegisterRequest(email, password, confirmPassword, fullName, eventId);

        ClientUtils.userService.registerInvitation(data).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), "Registration successful! Check your email.", Toast.LENGTH_LONG).show();
                    if (getActivity() != null) {
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                } else {
                    Toast.makeText(getContext(), "Registration failed: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Registration error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // DTO for registration request
    public static class InvitationRegisterRequest {
        public String email;
        public String password;
        public String confirmPassword;
        public String fullName;
        public long eventId;

        public InvitationRegisterRequest(String email, String password, String confirmPassword, String fullName, long eventId) {
            this.email = email;
            this.password = password;
            this.confirmPassword = confirmPassword;
            this.fullName = fullName;
            this.eventId = eventId;
        }
    }

    // ApiResponse stub (napravi svoj model prema backendu)
    public static class ApiResponse {
        public String message;
    }
}


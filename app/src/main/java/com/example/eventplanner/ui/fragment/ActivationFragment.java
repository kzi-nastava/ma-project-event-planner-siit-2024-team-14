package com.example.eventplanner.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventplanner.R;
import com.example.eventplanner.data.network.ClientUtils;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivationFragment extends Fragment {

    private TextView tvActivationMessage, tvActivationSubMessage;
    private String token;
    private String role;

    public static ActivationFragment newInstance(String token, String role) {
        ActivationFragment fragment = new ActivationFragment();
        Bundle args = new Bundle();
        args.putString("token", token);
        args.putString("role", role);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_activation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvActivationMessage = view.findViewById(R.id.tvActivationMessage);
        tvActivationSubMessage = view.findViewById(R.id.tvActivationSubMessage);

        if (getArguments() != null) {
            token = getArguments().getString("token");
            role = getArguments().getString("role");
        }

        if (token != null && role != null) {
            activateAccount(token, role);
        } else {
            showError("Invalid activation request.");
        }
    }

    private void activateAccount(String token, String role) {
        tvActivationMessage.setText("Activating your account...");

        String baseUrl = "http://10.0.2.2:8080/api/";
        String url;

        if ("ServiceAndProductProvider".equalsIgnoreCase(role)) {
            url = baseUrl + "providers/activate?token=" + token;
        } else if ("User".equalsIgnoreCase(role)) {
            url = baseUrl + "invitations/activate?token=" + token;
        } else {
            url = baseUrl + "organizers/activate?token=" + token;
        }

        ClientUtils.userService.activateAccount(url).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String resp = response.body().string();
                        JSONObject json = new JSONObject(resp);
                        String message = json.optString("message", "Your email is verified successfully!");
                        showSuccess(message);
                    } catch (Exception e) {
                        showSuccess("Your email is verified successfully!");
                    }
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        JSONObject json = new JSONObject(errorBody);
                        String message = json.optString("message", "Invalid or expired activation link.");
                        showError(message);
                    } catch (Exception e) {
                        showError("Invalid or expired activation link.");
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showError("Activation failed: " + t.getMessage());
            }
        });
    }

    private void showSuccess(String message) {
        tvActivationMessage.setText("Success!");
        tvActivationSubMessage.setText(message);
    }

    private void showError(String message) {
        tvActivationMessage.setText("Error");
        tvActivationSubMessage.setText(message);
    }
}

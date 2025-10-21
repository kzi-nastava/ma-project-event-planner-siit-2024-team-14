package com.example.eventplanner.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eventplanner.R;
import com.example.eventplanner.data.model.registration.EmailVerificationModel;
import com.example.eventplanner.data.network.registration.ActivationApi;
import com.example.eventplanner.data.network.registration.ApiClient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivationActivity extends AppCompatActivity {

    private TextView activationMessage, successText;
    private ProgressBar progress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activation_activity);

        activationMessage = findViewById(R.id.activationMessage);
        successText = findViewById(R.id.successText);
        progress = findViewById(R.id.progress);

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        activationMessage.setText("Activating your account...");
        successText.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);

        String token, role;
        Uri data = intent.getData();
        if (data != null) {
            token = data.getQueryParameter("token");
            role  = data.getQueryParameter("role");
        } else {
            token = intent.getStringExtra("token");
            role  = intent.getStringExtra("role");
        }

        if (token == null || token.trim().isEmpty()) {
            progress.setVisibility(View.GONE);
            activationMessage.setText("Invalid activation request.");
            Toast.makeText(this, "No token in link", Toast.LENGTH_LONG).show();
            return;
        }

        ActivationApi api = ApiClient.get().create(ActivationApi.class);
        Log.d("Activation", "Token=" + token + " role=" + role + " -> calling verify");

        api.verifyPost(new EmailVerificationModel(token)).enqueue(new Callback<ResponseBody>() {
            @Override public void onResponse(Call<ResponseBody> c, Response<ResponseBody> r) {
                progress.setVisibility(View.GONE);
                if (r.isSuccessful()) {
                    activationMessage.setText("Your account has been activated successfully!");
                    successText.setVisibility(View.VISIBLE);
                } else {
                    activationMessage.setText("Invalid or expired activation link. (" + r.code() + ")");
                }
            }
            @Override public void onFailure(Call<ResponseBody> c, Throwable t) {
                progress.setVisibility(View.GONE);
                activationMessage.setText("Network error: " + t.getMessage());
            }
        });

    }

}


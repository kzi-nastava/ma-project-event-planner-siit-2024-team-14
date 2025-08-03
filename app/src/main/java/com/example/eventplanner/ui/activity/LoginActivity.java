package com.example.eventplanner.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.eventplanner.data.model.login.LoginModel;
import com.example.eventplanner.data.model.login.LoginResponseModel;
import com.example.eventplanner.data.network.services.notifications.NotificationWebSocketManager;
import com.example.eventplanner.data.network.ClientUtils;
import com.example.eventplanner.ui.fragment.ProfileFragment;
import com.example.eventplanner.R;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button confirmButton;
    private TextView registerLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmButton = findViewById(R.id.confirmButton);
        registerLink = findViewById(R.id.registerLink);

        confirmButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            loginUser(email, password);
        });

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileFragment profileFragment = new ProfileFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(android.R.id.content, profileFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    private void loginUser(String email, String password) {
        LoginModel loginModel = new LoginModel(email, password);

        ClientUtils.authService.login(loginModel, new Callback<LoginResponseModel>() {
            @Override
            public void onResponse(Call<LoginResponseModel> call, Response<LoginResponseModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponseModel loginResponse = response.body();
                    if (loginResponse.isSuccess()) {
                        saveUserData(loginResponse);
                        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

                        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);

                        int userId = prefs.getInt("userId", -1);
                        boolean isMuted = prefs.getBoolean("muted", false);

                        if (userId != -1 && !isMuted) {
                            NotificationWebSocketManager.connect(getApplicationContext(), userId, notification -> {
                                // Handle notification here if needed
                            });
                        }

                        prefs.edit().putString("auth_token", loginResponse.getToken()).apply();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Login failed: " + loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        if (response.errorBody() != null) {
                            Gson gson = new Gson();
                            LoginResponseModel errorResponse = gson.fromJson(response.errorBody().charStream(), LoginResponseModel.class);
                            Toast.makeText(LoginActivity.this, "Login failed: " + errorResponse.getMessage(), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Login failed. Unknown error.", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, "Login failed: Unexpected error", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponseModel> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserData(LoginResponseModel loginResponse) {
        if (loginResponse == null || loginResponse.getUser() == null) return;

        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("token", loginResponse.getToken());
        editor.putString("user", new Gson().toJson(loginResponse.getUser()));
        editor.putString("userCity", loginResponse.getUser().getCity());
        editor.putString("userEmail", loginResponse.getUser().getEmail());
        editor.putInt("userId", loginResponse.getUser().getId());
        editor.putString("userPassword", loginResponse.getUser().getPassword());
        editor.putString("role", loginResponse.getUser().getRole());
        editor.putBoolean("muted", loginResponse.getUser().getMuted());

        editor.apply();
    }
}

package com.example.eventplanner.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.eventplanner.data.model.LoginDTO;
import com.example.eventplanner.data.model.LoginResponseDTO;
import com.example.eventplanner.data.network.services.user.UserService;
import com.example.eventplanner.ui.fragment.ProfileFragment;
import com.example.eventplanner.R;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button confirmButton;
    private TextView registerLink;

    private UserService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmButton = findViewById(R.id.confirmButton);
        registerLink = findViewById(R.id.registerLink);

        // Retrofit inicijalizacija
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080")  // localhost za emulator, promeni na pravi URL ako treba
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(UserService.class);

        confirmButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if(email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            loginUser(email, password);
        });

        // Set an OnClickListener for the "Register here" text view
        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the ProfileFragment
                ProfileFragment profileFragment = new ProfileFragment();
                // Begin the fragment transaction
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(android.R.id.content, profileFragment);  // Replace the current activity content with the fragment
                transaction.addToBackStack(null);  // Add the transaction to the back stack
                transaction.commit();  // Commit the transaction
            }
        });
    }

    private void loginUser(String email, String password) {
        LoginDTO loginDTO = new LoginDTO(email, password);
        Call<LoginResponseDTO> call = apiService.login(loginDTO);

        call.enqueue(new Callback<LoginResponseDTO>() {
            @Override
            public void onResponse(Call<LoginResponseDTO> call, Response<LoginResponseDTO> response) {
                if(response.isSuccessful() && response.body() != null) {
                    LoginResponseDTO loginResponse = response.body();
                    if(loginResponse.isSuccess()) {
                        saveUserData(loginResponse);
                        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                        // Sačuvaj token i korisnika u SharedPreferences ili globalno i nastavi dalje
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);

                        finish(); // završi LoginActivity

                        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                        prefs.edit().putString("auth_token", loginResponse.getToken()).apply();


                    } else {
                        Toast.makeText(LoginActivity.this, "Login failed: " + loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Login error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponseDTO> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserData(LoginResponseDTO loginResponse) {
        if (loginResponse == null || loginResponse.getUser() == null) return;

        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("token", loginResponse.getToken());
        editor.putString("user", new Gson().toJson(loginResponse.getUser()));
        editor.putString("userCity", loginResponse.getUser().getCity());
        editor.putInt("userId", loginResponse.getUser().getId());
        editor.putString("userPassword", loginResponse.getUser().getPassword());
        editor.putString("role", loginResponse.getUser().getRole());

        editor.apply();
    }

}

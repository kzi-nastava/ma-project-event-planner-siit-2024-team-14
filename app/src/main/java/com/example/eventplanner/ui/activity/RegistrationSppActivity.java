package com.example.eventplanner.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eventplanner.R;

public class RegistrationSppActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_spp_activity);

        // Find the "Log in here" text view by ID
        TextView loginLink = findViewById(R.id.loginLink);

        // Set an OnClickListener for the "Log in here" text view
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the LoginActivity when the link is clicked
                Intent intent = new Intent(RegistrationSppActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Optionally, finish the current activity to prevent the user from returning to the registration screen
            }
        });
    }
}

package com.example.eventplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RegistrationEoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_eo_activity);

        TextView loginLink = findViewById(R.id.loginLink);

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the LoginActivity when the link is clicked
                Intent intent = new Intent(RegistrationEoActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Optionally, finish the current activity to prevent the user from returning to the registration screen
            }
        });
    }
}

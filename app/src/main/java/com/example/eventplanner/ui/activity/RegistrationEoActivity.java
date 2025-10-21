package com.example.eventplanner.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eventplanner.R;
import com.example.eventplanner.ui.fragment.registration.RegistrationEoFragment;

public class RegistrationEoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_eo_activity);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.registration_eo, new RegistrationEoFragment())
                    .commit();
        }

        TextView loginLink = findViewById(R.id.loginLink);
        if (loginLink != null) {
            loginLink.setOnClickListener(v -> {
                Intent intent = new Intent(RegistrationEoActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            });
        }
    }
}

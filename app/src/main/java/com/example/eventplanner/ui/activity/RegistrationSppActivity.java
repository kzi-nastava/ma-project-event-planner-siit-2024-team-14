package com.example.eventplanner.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eventplanner.R;
import com.example.eventplanner.ui.fragment.registration.RegistrationSppFragment;

public class RegistrationSppActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_spp_activity);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.registration_spp, new RegistrationSppFragment())
                    .commit();
        }

        TextView loginLink = findViewById(R.id.loginLink);
        if (loginLink != null) {
            loginLink.setOnClickListener(v -> {
                startActivity(new Intent(RegistrationSppActivity.this, LoginActivity.class));
                finish();
            });
        }
    }
}

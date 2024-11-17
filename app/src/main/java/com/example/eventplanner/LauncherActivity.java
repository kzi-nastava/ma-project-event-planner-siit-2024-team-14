package com.example.eventplanner; // Replace with your actual package name

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class LauncherActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher_activity); // Replace with your launcher layout

        // Use a handler to delay the transition to the MainActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start MainActivity after 5 seconds
                Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Optional: close LauncherActivity so the user can't go back to it
            }
        }, 3000); // 5000ms = 5 seconds
    }
}

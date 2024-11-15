package com.example.eventplanner;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private ImageView navigationIcon;
    private boolean isNavigationViewVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationView = findViewById(R.id.navigation_view);
        navigationIcon = findViewById(R.id.navigation_icon);

        navigationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNavigationViewVisible) {
                    // Hide the NavigationView
                    navigationView.setVisibility(View.GONE);
                } else {
                    // Show the NavigationView
                    navigationView.setVisibility(View.VISIBLE);
                }
                isNavigationViewVisible = !isNavigationViewVisible;
            }
        });
    }
}

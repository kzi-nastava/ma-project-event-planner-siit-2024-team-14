package com.example.eventplanner.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.eventplanner.ui.fragment.ProfileFragment;
import com.example.eventplanner.R;
import com.example.eventplanner.ui.fragment.SettingsFragment;
import com.example.eventplanner.ui.fragment.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private ImageView navigationIcon;
    private boolean isNavigationViewVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize NavigationView and NavigationIcon
        navigationView = findViewById(R.id.navigation_view);
        navigationIcon = findViewById(R.id.navigation_icon);

        // Set up the bottom navigation view listener
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation_view);
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            // Determine which fragment to show based on the selected item
            if (item.getItemId() == R.id.home) {
                selectedFragment = new HomeFragment();
            } else if (item.getItemId() == R.id.profile) {
                selectedFragment = new ProfileFragment();
            } else if (item.getItemId() == R.id.settings) {
                selectedFragment = new SettingsFragment();
            }

            // Replace the current fragment with the selected fragment
            if (selectedFragment != null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.home_page_fragment, selectedFragment);
                transaction.commit();
            }

            return true; // Return true to indicate the item selection was handled
        });

        // Set up the navigation drawer toggle (icon click to show/hide the navigation drawer)
        navigationIcon.setOnClickListener(v -> {
            if (isNavigationViewVisible) {
                // Hide the NavigationView
                navigationView.setVisibility(View.GONE);
            } else {
                // Show the NavigationView
                navigationView.setVisibility(View.VISIBLE);
            }
            isNavigationViewVisible = !isNavigationViewVisible;
        });

        // Set the NavigationView item selection listener
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Fragment selectedFragment = null;

                // Handle the navigation item selection
                if (item.getItemId() == R.id.nav_home) {
                    selectedFragment = new HomeFragment(); // Open HomeFragment
                } else if (item.getItemId() == R.id.nav_profile) {
                    selectedFragment = new ProfileFragment(); // Open ProfileFragment
                }

                // If a fragment was selected, replace the current fragment
                if (selectedFragment != null) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.home_page_fragment, selectedFragment);
                    transaction.addToBackStack(null); // Optionally add to back stack
                    transaction.commit();
                }

                // Close the navigation drawer after selection
                navigationView.setVisibility(View.GONE);
                isNavigationViewVisible = false;

                return true;
            }
        });

        // Initialize with the HomeFragment by default if savedInstanceState is null
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.home_page_fragment, new HomeFragment())
                    .commit();
        }
    }
}

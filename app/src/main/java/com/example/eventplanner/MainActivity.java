package com.example.eventplanner;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.eventplanner.R;

import com.example.eventplanner.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private Fragment selectedFragment = new HomeFragment(); // Default fragment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);

        // Set up the navigation listener
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                selectedFragment = new HomeFragment(); // Show Home Fragment
            } else if (item.getItemId() == R.id.profile) {
                selectedFragment = new ProfileFragment(); // Show Profile Fragment
            } else if (item.getItemId() == R.id.settings) {
                selectedFragment = new SettingsFragment(); // Show Settings Fragment
            } else {
                return false; // Return false if none of the items are matched
            }

            // Perform fragment transaction
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, selectedFragment)
                    .commit();
            return true;
        });

        // Load the default fragment when the app starts
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, selectedFragment)
                    .commit();
        }
    }
}
/*
public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_main);

        // Set initial fragment
        replaceFragment(new HomeFragment());

        Log.d("AAAAAAAa", "binding.bottomNavigationView: " + binding.bottomNavigationView);

        // Bottom Navigation Item selection listener
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            int itemId = item.getItemId();

            Log.d("omg", "omg");

            if (itemId == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (itemId == R.id.profile) {
                replaceFragment(new ProfileFragment());
            } else if (itemId == R.id.settings) {
                replaceFragment(new SettingsFragment());
            }
            return true;  // If you handle the item, return true
        });
    }

    // Helper method to replace fragments

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, fragment); // Replace with the container ID
        transaction.addToBackStack(null); // Optional: Add to back stack if you want to allow back navigation
        transaction.commit();
    }
*/




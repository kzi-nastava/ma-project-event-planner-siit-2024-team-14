package com.example.eventplanner.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.eventplanner.ui.fragment.ProfileFragment;
import com.example.eventplanner.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        // Find the "Register here" text view by ID
        TextView registerLink = findViewById(R.id.registerLink);

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
}

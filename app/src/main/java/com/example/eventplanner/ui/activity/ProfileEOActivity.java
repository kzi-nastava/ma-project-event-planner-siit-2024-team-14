package com.example.eventplanner.ui.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.eventplanner.R;

public class ProfileEOActivity extends AppCompatActivity {

    private ImageView profileImage;
    private TextView userName;
    private TextView userEmail;
    private TextView userPhone;
    private Button editButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_eo_profile);

        // Initialize views
        profileImage = findViewById(R.id.profileImage);
        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        userPhone = findViewById(R.id.userPhone);
        editButton = findViewById(R.id.editButton);

        // Set some mock data (In real implementation, get from database or API)
        userName.setText("John Doe");
        userEmail.setText("johndoe@example.com");
        userPhone.setText("(123) 456-7890");

        // Set the edit button listener
        editButton.setOnClickListener(v -> {
            // Handle profile edit functionality (e.g., open a new activity or fragment)
            // Example: startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
        });

        // Handling window insets for edge-to-edge layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}

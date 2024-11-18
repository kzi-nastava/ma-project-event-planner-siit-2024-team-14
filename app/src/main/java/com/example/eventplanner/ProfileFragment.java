package com.example.eventplanner;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ProfileFragment extends Fragment {

    // Default constructor
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Find buttons by their ID
        Button loginButton = view.findViewById(R.id.login_button);
        Button registerEoButton = view.findViewById(R.id.register_button_eo);
        Button registerSppButton = view.findViewById(R.id.register_button_spp);

        // Set the OnClickListener for the login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open LoginActivity
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        // Set the OnClickListener for the register as EO button
        registerEoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open RegistrationEoActivity
                Intent intent = new Intent(getActivity(), RegistrationEoActivity.class);
                startActivity(intent);
            }
        });

        // Set the OnClickListener for the register as SPP button
        registerSppButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open RegistrationSppActivity
                Intent intent = new Intent(getActivity(), RegistrationSppActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

}

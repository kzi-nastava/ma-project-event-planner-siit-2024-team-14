package com.example.eventplanner.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


import com.example.eventplanner.R;
import com.example.eventplanner.ui.activity.RegistrationEoActivity;
import com.example.eventplanner.ui.activity.RegistrationSppActivity;
import com.example.eventplanner.ui.activity.LoginActivity;

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

        View profileContent = view.findViewById(R.id.profile_content);

        SharedPreferences prefs = getActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("auth_token", null);

        if (token != null) {
            // Korisnik je ulogovan – prikaži profil, sakrij dugmad
            loginButton.setVisibility(View.GONE);
            registerEoButton.setVisibility(View.GONE);
            registerSppButton.setVisibility(View.GONE);

            if (profileContent != null) {
                profileContent.setVisibility(View.VISIBLE);
            }

            // Pronađi logout dugme i postavi listener
            Button logoutButton = view.findViewById(R.id.logout_button);
            logoutButton.setOnClickListener(v -> {
                prefs.edit().remove("auth_token").apply();
                Toast.makeText(getActivity(), "Logged out!", Toast.LENGTH_SHORT).show();

                // Osveži fragment da se prikažu dugmad za login/registraciju
                getParentFragmentManager()
                        .beginTransaction()
                        .detach(this)
                        .attach(this)
                        .commit();

                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.home_page_fragment, new HomeFragment())  // R.id.fragment_container je id tvog container-a za fragmente
                            .commit();
                }

            });

        } else {
            // Korisnik NIJE ulogovan – prikaži dugmad, sakrij profil
            loginButton.setVisibility(View.VISIBLE);
            registerEoButton.setVisibility(View.VISIBLE);
            registerSppButton.setVisibility(View.VISIBLE);

            if (profileContent != null) {
                profileContent.setVisibility(View.GONE);
            }

        }

        loginButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), LoginActivity.class)));
        registerEoButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), RegistrationEoActivity.class)));
        registerSppButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), RegistrationSppActivity.class)));

        return view;


    }

}

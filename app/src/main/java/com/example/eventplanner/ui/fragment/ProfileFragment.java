package com.example.eventplanner.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.eventplanner.R;
import com.example.eventplanner.data.network.services.notifications.NotificationWebSocketManager;
import com.example.eventplanner.ui.activity.LoginActivity;
import com.example.eventplanner.ui.activity.RegistrationEoActivity;
import com.example.eventplanner.ui.activity.RegistrationSppActivity;
import com.example.eventplanner.ui.fragment.profiles.ViewOrganizerProfileFragment;
import com.example.eventplanner.ui.fragment.profiles.ViewProviderProfileFragment;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        Button loginButton       = view.findViewById(R.id.login_button);
        Button registerEoButton  = view.findViewById(R.id.register_button_eo);
        Button registerSppButton = view.findViewById(R.id.register_button_spp);
        View profileContent      = view.findViewById(R.id.profile_content);

        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String token  = prefs.getString("auth_token", null);
        String role   = prefs.getString("role", null);
        int userId    = prefs.getInt("userId", -1);

        if (token != null) {
            // Logged in → route by role if we know it
            if (role != null && userId > 0) {
                if ("EventOrganizer".equalsIgnoreCase(role)) {
                    // EO profile
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.home_page_fragment, ViewOrganizerProfileFragment.newInstance(userId))
                            .addToBackStack(null)
                            .commit();
                } else if ("ServiceAndProductProvider".equalsIgnoreCase(role)) {
                    // Provider profile
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.home_page_fragment, ViewProviderProfileFragment.newInstance(userId))
                            .addToBackStack(null)
                            .commit();
                } else {
                    // Unknown role → keep generic profile visible
                    if (profileContent != null) profileContent.setVisibility(View.VISIBLE);
                }
            }

            // Hide auth buttons when logged in
            loginButton.setVisibility(View.GONE);
            registerEoButton.setVisibility(View.GONE);
            registerSppButton.setVisibility(View.GONE);

            // Logout button inside profile_content
            Button logoutButton = view.findViewById(R.id.logout_button);
            if (logoutButton != null) {
                logoutButton.setOnClickListener(v -> {
                    prefs.edit().remove("auth_token").apply();
                    Toast.makeText(getActivity(), "Logged out!", Toast.LENGTH_SHORT).show();
                    NotificationWebSocketManager.disconnect();

                    // Refresh this fragment to show auth buttons again
                    getParentFragmentManager()
                            .beginTransaction()
                            .detach(this)
                            .attach(this)
                            .commit();

                    // Back to Home
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.home_page_fragment, new HomeFragment())
                            .commit();
                });
            }
        } else {
            // Not logged in → show auth buttons, hide profile content
            loginButton.setVisibility(View.VISIBLE);
            registerEoButton.setVisibility(View.VISIBLE);
            registerSppButton.setVisibility(View.VISIBLE);
            if (profileContent != null) profileContent.setVisibility(View.GONE);
        }

        // Auth actions
        loginButton.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), LoginActivity.class)));
        registerEoButton.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), RegistrationEoActivity.class)));
        registerSppButton.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), RegistrationSppActivity.class)));

        return view;
    }
}

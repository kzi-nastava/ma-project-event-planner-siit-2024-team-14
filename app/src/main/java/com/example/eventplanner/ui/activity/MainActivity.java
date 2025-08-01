package com.example.eventplanner.ui.activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.eventplanner.R;
import com.example.eventplanner.data.network.services.notifications.NotificationWebSocketManager;
import com.example.eventplanner.ui.fragment.ActivationFragment;
import com.example.eventplanner.ui.fragment.AdminCommentsFragment;
import com.example.eventplanner.ui.fragment.AdminReportsFragment;
import com.example.eventplanner.ui.fragment.AllBookingsFragment;
import com.example.eventplanner.ui.fragment.AllInvitationsFragment;
import com.example.eventplanner.ui.fragment.BookingServiceRequestFragment;
import com.example.eventplanner.ui.fragment.HomeFragment;
import com.example.eventplanner.ui.fragment.InvitationFragment;
import com.example.eventplanner.ui.fragment.InvitationRegisterFragment;
import com.example.eventplanner.ui.fragment.MyEventsFragment;
import com.example.eventplanner.ui.fragment.NotificationFragment;
import com.example.eventplanner.ui.fragment.ProfileFragment;
import com.example.eventplanner.ui.fragment.SettingsFragment;
import com.example.eventplanner.ui.fragment.UpgradeOrganizerFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.Manifest;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private ImageView navigationIcon;
    private boolean isNavigationViewVisible = false;

    private static final String PREFS_NAME = "MyAppPrefs";

    private SharedPreferences prefs;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

    private static final String KEY_ROLE = "role";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getIntent() != null && getIntent().getData() != null) {
            handleDeepLink(getIntent().getData());
        }else if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.home_page_fragment, new HomeFragment())
                    .commit();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1001);
            }
        }

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        navigationView = findViewById(R.id.navigation_view);
        navigationIcon = findViewById(R.id.navigation_icon);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation_view);
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.home) {
                selectedFragment = new HomeFragment();
            } else if (item.getItemId() == R.id.profile) {
                selectedFragment = new ProfileFragment();
            } else if (item.getItemId() == R.id.settings) {
                selectedFragment = new SettingsFragment();
            }
            if (selectedFragment != null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.home_page_fragment, selectedFragment);
                transaction.commit();
            }
            return true;
        });

        navigationIcon.setOnClickListener(v -> {
            if (isNavigationViewVisible) {
                navigationView.setVisibility(View.GONE);
            } else {
                navigationView.setVisibility(View.VISIBLE);
            }
            isNavigationViewVisible = !isNavigationViewVisible;
        });

        setupNavigationMenuByRole();

        navigationView.setNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int id = item.getItemId();

            if (id == R.id.home) {
                selectedFragment = new HomeFragment();
            } else if (id == R.id.profile) {
                selectedFragment = new ProfileFragment();
            } else if (id == R.id.nav_comments) {
                selectedFragment = new AdminCommentsFragment();
            }else if(id == R.id.nav_notifications){
                selectedFragment = new NotificationFragment();
            } else if (id == R.id.nav_user_management) {
                // TODO: Otvori UserManagement fragment/activity
            } else if (id == R.id.nav_event_types) {
                // TODO: Otvori EventTypes fragment/activity
            } else if (id == R.id.nav_reports) {
                selectedFragment = new AdminReportsFragment();
            } else if (id == R.id.nav_logout) {
                logoutUser();
                return true;
            } else if(id == R.id.nav_become_organizer){
                String email = prefs.getString("userEmail", "");
                String password =  prefs.getString("userPassword", "");
                selectedFragment = new UpgradeOrganizerFragment(email, password);
            } else if(id == R.id.nav_become_provider){
                return true;
            }else if(id == R.id.nav_my_events){
                selectedFragment = new MyEventsFragment();
            }else if(id == R.id.nav_calendar){
                return true;
            }else if(id == R.id.nav_invitations){
                selectedFragment = new AllInvitationsFragment();
            }else if(id == R.id.nav_budget_planning){
                return true;
            }else if(id == R.id.nav_favourites){
                return true;
            }else if(id == R.id.nav_messages){
                return true;
            }else if(id == R.id.nav_my_services){
                return true;
            }else if(id == R.id.nav_all_bookings){
                selectedFragment = new AllBookingsFragment();
            }else if(id == R.id.nav_booking_requests){
                selectedFragment = new BookingServiceRequestFragment();
            }else if(id == R.id.nav_price_list){
                return true;
            }

            if (selectedFragment != null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.home_page_fragment, selectedFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }

            navigationView.setVisibility(View.GONE);
            isNavigationViewVisible = false;

            return true;
        });


    }

    private void setupNavigationMenuByRole() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String role = prefs.getString(KEY_ROLE, null);

        Menu menu = navigationView.getMenu();
        menu.clear();

        if (role == null) {
            menu.add(Menu.NONE, R.id.home, Menu.NONE, "Home").setIcon(R.drawable.home);
            menu.add(Menu.NONE, R.id.nav_login, Menu.NONE, "Log in");
            menu.add(Menu.NONE, R.id.nav_register_organizer, Menu.NONE, "Register as event organizer");
            menu.add(Menu.NONE, R.id.nav_register_provider, Menu.NONE, "Register as event product and service provider");
            return;
        }

        switch (role.toLowerCase()) {
            case "admin":
                menu.add(Menu.NONE, R.id.home, Menu.NONE, "Home").setIcon(R.drawable.home);
                menu.add(Menu.NONE, R.id.nav_user_management, Menu.NONE, "User Management");
                menu.add(Menu.NONE, R.id.nav_event_types, Menu.NONE, "Event Types");
                menu.add(Menu.NONE, R.id.nav_categories, Menu.NONE, "Categories");
                menu.add(Menu.NONE, R.id.nav_comments, Menu.NONE, "Comments & reviews requests");
                menu.add(Menu.NONE, R.id.nav_reports, Menu.NONE, "Reports");
                menu.add(Menu.NONE, R.id.nav_notifications, Menu.NONE, "Notifications");
                menu.add(Menu.NONE, R.id.profile, Menu.NONE, "Profile");
                menu.add(Menu.NONE, R.id.nav_logout, Menu.NONE, "Log out").setIcon(R.drawable.logout);
                break;

            case "user":
                menu.add(Menu.NONE, R.id.nav_become_organizer, Menu.NONE, "Become an event organizer");
                menu.add(Menu.NONE, R.id.nav_become_provider, Menu.NONE, "Become a product and service provider");
                menu.add(Menu.NONE, R.id.nav_logout, Menu.NONE, "Log out").setIcon(R.drawable.logout);
                break;

            case "eventorganizer":
                menu.add(Menu.NONE, R.id.nav_my_events, Menu.NONE, "My events");
                menu.add(Menu.NONE, R.id.nav_calendar, Menu.NONE, "Calendar");
                menu.add(Menu.NONE, R.id.nav_categories, Menu.NONE, "Categories");
                menu.add(Menu.NONE, R.id.nav_invitations, Menu.NONE, "Invitations");
                menu.add(Menu.NONE, R.id.nav_budget_planning, Menu.NONE, "Budget planning");
                menu.add(Menu.NONE, R.id.nav_favourites, Menu.NONE, "Favourite services/products");
                menu.add(Menu.NONE, R.id.nav_messages, Menu.NONE, "Messages");
                menu.add(Menu.NONE, R.id.nav_notifications, Menu.NONE, "Notifications");
                menu.add(Menu.NONE, R.id.nav_logout, Menu.NONE, "Log out");
                break;

            case "serviceandproductprovider":
                menu.add(Menu.NONE, R.id.nav_my_services, Menu.NONE, "My services/products");
                menu.add(Menu.NONE, R.id.nav_all_bookings, Menu.NONE, "All Bookings");
                menu.add(Menu.NONE, R.id.nav_booking_requests, Menu.NONE, "Booking service requests");
                menu.add(Menu.NONE, R.id.nav_reviews, Menu.NONE, "Reviews and ratings");
                menu.add(Menu.NONE, R.id.nav_categories, Menu.NONE, "Categories");
                menu.add(Menu.NONE, R.id.nav_price_list, Menu.NONE, "Price list");
                menu.add(Menu.NONE, R.id.nav_notifications, Menu.NONE, "Notifications");
                menu.add(Menu.NONE, R.id.nav_messages, Menu.NONE, "Messages");
                menu.add(Menu.NONE, R.id.nav_logout, Menu.NONE, "Log out");
                break;
        }
    }

    public void logoutUser() {
        if (prefs != null) {
            prefs.edit().clear().apply();
        }
        Toast.makeText(this, "Logged out!", Toast.LENGTH_SHORT).show();

        NotificationWebSocketManager.disconnect();

        setupNavigationMenuByRole();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void handleDeepLink(Uri data) {
        String path = data.getPath(); // npr. "/invitation/login", "/invitation/register", "/activate"
        String email = data.getQueryParameter("email");
        String eventIdStr = data.getQueryParameter("eventId");
        String token = data.getQueryParameter("token");
        String role = data.getQueryParameter("role");

        long eventId = -1;
        if (eventIdStr != null) {
            try {
                eventId = Long.parseLong(eventIdStr);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        if (path != null) {
            switch (path) {
                case "/invitation/login":
                    startActivity(new Intent(this, LoginActivity.class));
                    break;

                case "/invitation/register":
                    if (email != null && eventId != -1) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.home_page_fragment, InvitationRegisterFragment.newInstance(email, eventId))
                                .addToBackStack(null)
                                .commit();
                    }
                    break;

                case "/activate":
                    if (token != null && role != null) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.home_page_fragment, ActivationFragment.newInstance(token, role))
                                .addToBackStack(null)
                                .commit();
                    }
                    break;

                default:
                    break;
            }
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        NotificationWebSocketManager.disconnect();
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

}

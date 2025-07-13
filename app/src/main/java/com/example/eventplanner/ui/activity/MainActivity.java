package com.example.eventplanner.ui.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.eventplanner.R;
import com.example.eventplanner.ui.fragment.AdminCommentsFragment;
import com.example.eventplanner.ui.fragment.HomeFragment;
import com.example.eventplanner.ui.fragment.ProfileFragment;
import com.example.eventplanner.ui.fragment.SettingsFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private ImageView navigationIcon;
    private boolean isNavigationViewVisible = false;

    // SharedPreferences key za ulogu
    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String KEY_ROLE = "role";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            } else if (id == R.id.nav_user_management) {
                // TODO: Otvori UserManagement fragment/activity
            } else if (id == R.id.nav_event_types) {
                // TODO: Otvori EventTypes fragment/activity
            } else if (id == R.id.nav_reports) {
                // TODO: Otvori Reports fragment/activity
            } else if (id == R.id.nav_logout) {
                logoutUser();
                return true;
            } else if(id == R.id.nav_become_organizer){
                return true;
            } else if(id == R.id.nav_become_provider){
                return true;
            }else if(id == R.id.nav_my_events){
                return true;
            }else if(id == R.id.nav_calendar){
                return true;
            }else if(id == R.id.nav_invitations){
                return true;
            }else if(id == R.id.nav_budget_planning){
                return true;
            }else if(id == R.id.nav_favourites){
                return true;
            }else if(id == R.id.nav_messages){
                return true;
            }else if(id == R.id.nav_my_services){
                return true;
            }else if(id == R.id.nav_all_bookings){
                return true;
            }else if(id == R.id.nav_booking_requests){
                return true;
            }else if(id == R.id.nav_price_list){
                return true;
            }else if(id == R.id.nav_logout){
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

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.home_page_fragment, new HomeFragment())
                    .commit();
        }
    }

    private void setupNavigationMenuByRole() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String role = prefs.getString(KEY_ROLE, null);

        Menu menu = navigationView.getMenu();
        menu.clear();

        if (role == null) {
            // Neulogovani korisnik - nema opcija ili možeš dodati samo login ako želiš
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

    private void logoutUser() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        setupNavigationMenuByRole();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.home_page_fragment, new HomeFragment())
                .commit();
    }
}

package com.example.eventplanner;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServicesOverviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services_overview);

        RecyclerView servicesRecyclerView = findViewById(R.id.services_recycler_view);
        servicesRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));

        List<ServiceBrief> services = Arrays.asList(
          new ServiceBrief("Wedding Photography", "Capture your special day with stunning photographs that you'll cherish forever. Our professional team ensures every moment is beautifully preserved.", R.drawable.wedding_photography),
          new ServiceBrief("Catering Services", "Delicious and customizable menus for every occasion. From small gatherings to large events, we provide food that satisfies every guest.", R.drawable.catering_service),
          new ServiceBrief("DJ and Live Music", "Make your events unforgettable with professional DJ services and live music performances. We bring energy and entertainment to your celebrations.", R.drawable.background)
        );

        ServiceAdapter adapter = new ServiceAdapter(this, services);
        servicesRecyclerView.setAdapter(adapter);
    }
}
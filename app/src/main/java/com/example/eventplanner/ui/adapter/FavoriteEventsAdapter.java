package com.example.eventplanner.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.data.model.events.EventModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FavoriteEventsAdapter extends RecyclerView.Adapter<FavoriteEventsAdapter.VH> {

    public interface Listener {
        void onOpen(EventModel event);
    }

    private final List<EventModel> items = new ArrayList<>();
    private final Listener listener;
    private static final String BASE = "http://10.0.2.2:8080/api/";

    public FavoriteEventsAdapter(Listener listener) { this.listener = listener; }

    public void submit(List<EventModel> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite_event, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        EventModel e = items.get(pos);
        if (e == null) return;

        // Organizer name: First + Last (fallback "Organizer")
        String first = safe(e.getOrganizerFirstName());
        String last  = safe(e.getOrganizerLastName());
        String organizerName = (first + " " + last).trim();
        if (organizerName.isEmpty()) organizerName = "Organizer";
        if (h.organizerName != null) h.organizerName.setText(organizerName);
        if (h.avatar != null) h.avatar.setText(initialsOf(organizerName));

        // Event title (fallback "Event")
        if (h.name != null) h.name.setText(nullTo(e.getName(), "Event"));

        // Description (fallback empty)
        if (h.description != null) h.description.setText(nullTo(e.getDescription(), ""));

        // Location (fallback "Unknown")
        if (h.location != null) h.location.setText("📍 " + nullTo(e.getLocation(), "Unknown"));

        // Date: try to pretty-format; fallback to raw startDate
        if (h.date != null) h.date.setText("🗓  " + prettyDate(e.getStartDate()));

        // ✅ Photo logic unchanged
        if (h.photo != null) {
            String url = BASE + "events/get-photo/" + e.getId();
            Glide.with(h.photo.getContext())
                    .load(url)
                    .placeholder(R.drawable.profile_placeholder)
                    .centerCrop()
                    .into(h.photo);
        }

        if (h.viewMore != null) {
            h.viewMore.setOnClickListener(v -> {
                if (listener != null) listener.onOpen(e);
            });
        }
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView avatar, organizerName, name, description, location, date;
        ImageView photo;
        Button viewMore;
        VH(@NonNull View v) {
            super(v);
            // These IDs must exist in item_favorite_event.xml
            avatar        = v.findViewById(R.id.avatar);
            organizerName = v.findViewById(R.id.organizerName);
            name          = v.findViewById(R.id.name);
            description   = v.findViewById(R.id.description);
            location      = v.findViewById(R.id.location);
            date          = v.findViewById(R.id.date);
            photo         = v.findViewById(R.id.photo);
            viewMore      = v.findViewById(R.id.viewMore);
        }
    }

    // -------- helpers --------
    private static String safe(String s) { return s == null ? "" : s; }
    private static String nullTo(String s, String fb) { return s == null ? fb : s; }

    private static String initialsOf(String name) {
        if (name == null) return "EV";
        String t = name.trim();
        if (t.isEmpty()) return "EV";
        String[] p = t.split("\\s+");
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < Math.min(2, p.length); i++) {
            if (!p[i].isEmpty()) b.append(Character.toUpperCase(p[i].charAt(0)));
        }
        return b.length() > 0 ? b.toString() : "EV";
    }

    private static String prettyDate(String raw) {
        if (raw == null || raw.trim().isEmpty()) return "";
        String[] patterns = {
                "yyyy-MM-dd'T'HH:mm:ss",
                "yyyy-MM-dd",
                "yyyy/MM/dd",
                "dd.MM.yyyy"
        };
        for (String p : patterns) {
            try {
                Date d = new SimpleDateFormat(p, Locale.getDefault()).parse(raw);
                if (d != null) {
                    return new SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(d);
                }
            } catch (ParseException ignored) {}
        }
        return raw; // fallback if unparseable
    }
}

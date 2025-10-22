package com.example.eventplanner.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;

import java.util.ArrayList;
import java.util.List;

public class ActivitiesAdapter extends RecyclerView.Adapter<ActivitiesAdapter.VH> {

    public static class ActivityItem {
        public final String name;
        public final String description;
        public final String startTime;
        public final String endTime;
        public final String location;

        public ActivityItem(String name, String description, String startTime, String endTime, String location) {
            this.name = name;
            this.description = description == null ? "" : description;
            this.startTime = startTime;
            this.endTime = endTime;
            this.location = location == null ? "" : location;
        }
    }

    private final List<ActivityItem> items = new ArrayList<>();

    public void submit(List<ActivityItem> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    public void add(ActivityItem item) {
        items.add(item);
        notifyItemInserted(items.size() - 1);
    }

    public List<ActivityItem> getItems() {
        return items;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_activity, parent, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        ActivityItem a = items.get(pos);
        h.name.setText(a.name);
        h.description.setText(a.description.isEmpty() ? "—" : a.description);
        h.time.setText(a.startTime + " — " + a.endTime);
        h.location.setText(a.location.isEmpty() ? "—" : a.location);
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView name, description, time, location;
        VH(@NonNull View v) {
            super(v);
            name = v.findViewById(R.id.act_name);
            description = v.findViewById(R.id.act_description);
            time = v.findViewById(R.id.act_time);
            location = v.findViewById(R.id.act_location);
        }
    }
}

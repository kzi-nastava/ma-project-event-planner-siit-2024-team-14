package com.example.eventplanner.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;

import java.util.ArrayList;
import java.util.List;

public class FavoriteSolutionAdapter extends RecyclerView.Adapter<FavoriteSolutionAdapter.VH> {

    public interface Listener {
        void onOpen(SolutionItem item);
    }

    public static class SolutionItem {
        public int id;
        public String title;
        public String subtitle;  // e.g., price or short desc
        public String photoUrl;  // absolute or relative to BASE

        public SolutionItem(int id, String title, String subtitle, String photoUrl) {
            this.id = id;
            this.title = title;
            this.subtitle = subtitle;
            this.photoUrl = photoUrl;
        }
    }

    private final List<SolutionItem> items = new ArrayList<>();
    private final Listener listener;
    private static final String BASE = "http://10.0.2.2:8080/"; // for relative photos if needed

    public FavoriteSolutionAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submit(List<SolutionItem> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite_solution, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        SolutionItem s = items.get(pos);
        h.title.setText(s.title == null ? "Solution" : s.title);
        h.subtitle.setText(s.subtitle == null ? "" : s.subtitle);

        String url = s.photoUrl;
        if (url != null && !url.startsWith("http")) {
            url = BASE + (url.startsWith("/") ? url.substring(1) : url);
        }

        Glide.with(h.photo.getContext())
                .load(url)
                .placeholder(R.drawable.profile_placeholder)
                .centerCrop()
                .into(h.photo);

        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onOpen(s);
        });
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView photo;
        TextView title, subtitle;
        VH(@NonNull View v) {
            super(v);
            photo = v.findViewById(R.id.photo);
            title = v.findViewById(R.id.title);
        }
    }
}

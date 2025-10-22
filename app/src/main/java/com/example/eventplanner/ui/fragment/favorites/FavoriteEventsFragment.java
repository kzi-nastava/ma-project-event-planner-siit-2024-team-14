package com.example.eventplanner.ui.fragment.favorites;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;

import com.example.eventplanner.R;
import com.example.eventplanner.data.model.events.EventModel;
import com.example.eventplanner.data.network.ClientUtils;
import com.example.eventplanner.ui.adapter.FavoriteEventsAdapter;
import com.example.eventplanner.ui.fragment.events.EventDetailsFragment;
import com.example.eventplanner.data.model.events.ToggleFavoriteResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteEventsFragment extends Fragment implements FavoriteEventsAdapter.Listener {

    private ProgressBar progress;
    private TextView errorText, emptyText;
    private View content;
    private RecyclerView recycler;
    private FavoriteEventsAdapter adapter;

    private int userId = -1;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup c, @Nullable Bundle b) {
        return inflater.inflate(R.layout.fragment_favorite_events, c, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle b) {
        super.onViewCreated(v, b);

        progress = v.findViewById(R.id.progress);
        errorText = v.findViewById(R.id.errorText);
        content = v.findViewById(R.id.content);
        emptyText = v.findViewById(R.id.emptyText);
        recycler = v.findViewById(R.id.recycler);

        recycler.setLayoutManager(new GridLayoutManager(requireContext(), 1));
        adapter = new FavoriteEventsAdapter(this);
        recycler.setAdapter(adapter);

        SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", 0);
        userId = prefs.getInt("userId", -1);

        if (userId <= 0) {
            showError("You must be logged in.");
            return;
        }

        loadData();
    }

    private void setLoading(boolean loading) {
        progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        content.setVisibility(loading ? View.GONE : View.VISIBLE);
        errorText.setVisibility(View.GONE);
    }

    private void showError(String msg) {
        progress.setVisibility(View.GONE);
        content.setVisibility(View.GONE);
        errorText.setVisibility(View.VISIBLE);
        errorText.setText(msg);
    }

    private void showEmpty(boolean empty) {
        emptyText.setVisibility(empty ? View.VISIBLE : View.GONE);
        recycler.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    private void loadData() {
        setLoading(true);
        ClientUtils.organizerService.getFavoriteEvents(userId).enqueue(new Callback<List<EventModel>>() {
            @Override public void onResponse(Call<List<EventModel>> call, Response<List<EventModel>> resp) {
                setLoading(false);
                if (resp.isSuccessful() && resp.body() != null) {
                    List<EventModel> events = resp.body();
                    adapter.submit(events);
                    showEmpty(events.isEmpty());
                } else {
                    showError("Failed to load favorite events.");
                }
            }
            @Override public void onFailure(Call<List<EventModel>> call, Throwable t) {
                setLoading(false);
                showError("Network error: " + t.getMessage());
            }
        });
    }

    @Override
    public void onOpen(EventModel event) {
        Fragment details = EventDetailsFragment.newInstance(event.getId());
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.home_page_fragment, details)
                .addToBackStack(null)
                .commit();
    }

}

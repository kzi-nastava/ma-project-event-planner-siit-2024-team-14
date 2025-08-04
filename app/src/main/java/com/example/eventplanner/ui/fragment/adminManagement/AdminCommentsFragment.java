package com.example.eventplanner.ui.fragment.adminManagement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.data.model.comments.CommentModel;
import com.example.eventplanner.data.network.services.comments.CommentApiClient;
import com.example.eventplanner.ui.adapter.CommentAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminCommentsFragment extends Fragment {

    private RecyclerView recyclerView;
    private CommentAdapter adapter;

    public AdminCommentsFragment() {}

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_admin_comments, container, false);
        recyclerView = view.findViewById(R.id.comments_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        loadComments();
        return view;
    }

    private void loadComments() {
        CommentApiClient.getInstance().getPendingComments().enqueue(new Callback<List<CommentModel>>() {
            @Override
            public void onResponse(Call<List<CommentModel>> call, Response<List<CommentModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter = new CommentAdapter(response.body(), getContext());
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<CommentModel>> call, Throwable t) {
                // Handle failure
            }
        });
    }
}

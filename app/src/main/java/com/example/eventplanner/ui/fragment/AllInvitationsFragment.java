package com.example.eventplanner.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.data.model.invitations.GroupedInvitationModel;
import com.example.eventplanner.data.network.ClientUtils;
import com.example.eventplanner.data.network.services.invitations.InvitationService;
import com.example.eventplanner.ui.adapter.InvitationAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllInvitationsFragment extends Fragment {

    private InvitationService invitationService;
    private RecyclerView recyclerView;
    private InvitationAdapter adapter;
    private List<GroupedInvitationModel> groupedInvitations = new ArrayList<>();
    private int organizerId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_invitations, container, false);
        recyclerView = view.findViewById(R.id.rvInvitations);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new InvitationAdapter(groupedInvitations, getContext());
        recyclerView.setAdapter(adapter);

        SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        organizerId = prefs.getInt("userId", -1);
        invitationService = ClientUtils.invitationService;
        loadInvitations();

        return view;
    }

    private void loadInvitations() {
        invitationService.getInvitationsForOrganizer(organizerId).enqueue(new Callback<List<GroupedInvitationModel>>() {
            @Override
            public void onResponse(Call<List<GroupedInvitationModel>> call, Response<List<GroupedInvitationModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    groupedInvitations.clear();
                    groupedInvitations.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Failed to load invitations", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<GroupedInvitationModel>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}

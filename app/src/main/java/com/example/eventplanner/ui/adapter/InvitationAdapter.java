package com.example.eventplanner.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.data.model.invitations.GroupedInvitationModel;
import com.example.eventplanner.data.model.invitations.InvitationModel;
import com.example.eventplanner.ui.fragment.invitations.InvitationFragment;

import java.util.ArrayList;
import java.util.List;

public class InvitationAdapter extends RecyclerView.Adapter<InvitationAdapter.InviteViewHolder> {

    private List<GroupedInvitationModel> groups;
    private Context context;

    public InvitationAdapter(List<GroupedInvitationModel> groups, Context context) {
        this.groups = groups;
        this.context = context;
    }

    @NonNull
    @Override
    public InviteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InviteViewHolder(LayoutInflater.from(context).inflate(R.layout.item_grouped_invitation, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull InviteViewHolder holder, int position) {
        GroupedInvitationModel group = groups.get(position);
        holder.tvEventName.setText(group.getEvent().getName());

        InvitationListAdapter inviteAdapter = new InvitationListAdapter(group.getInvitations(), group.getEvent().getMaxParticipants());
        holder.rvGuests.setAdapter(inviteAdapter);

        holder.btnAdd.setEnabled(group.getInvitations().size() < group.getEvent().getMaxParticipants());
        holder.btnAdd.setOnClickListener(v -> {
            InvitationFragment fragment = new InvitationFragment();

            Bundle args = new Bundle();
            args.putLong("event_id", group.getEvent().getId());
            args.putInt("max_guests", group.getEvent().getMaxParticipants());

            // Dodavanje postojeće liste email adresa
            ArrayList<String> existingEmails = new ArrayList<>();
            for (InvitationModel i : group.getInvitations()) {
                existingEmails.add(i.getGuestEmail());
            }
            args.putStringArrayList("existing_emails", existingEmails);

            fragment.setArguments(args);

            ((FragmentActivity) context).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.home_page_fragment, fragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    static class InviteViewHolder extends RecyclerView.ViewHolder {
        TextView tvEventName;
        RecyclerView rvGuests;
        Button btnAdd;

        public InviteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEventName = itemView.findViewById(R.id.tvEventName);
            rvGuests = itemView.findViewById(R.id.rvGuests);
            btnAdd = itemView.findViewById(R.id.btnAddGuest);
            rvGuests.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        }
    }
}

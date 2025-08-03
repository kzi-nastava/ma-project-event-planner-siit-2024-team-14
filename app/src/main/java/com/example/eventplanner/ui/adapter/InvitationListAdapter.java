package com.example.eventplanner.ui.adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.data.model.invitations.InvitationModel;

import java.util.List;

public class InvitationListAdapter extends RecyclerView.Adapter<InvitationListAdapter.ViewHolder> {

    private List<InvitationModel> invitations;
    private int maxGuests;

    public InvitationListAdapter(List<InvitationModel> invitations, int maxGuests) {
        this.invitations = invitations;
        this.maxGuests = maxGuests;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invited_guest, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InvitationModel guest = invitations.get(position);
        holder.tvGuestEmail.setText(guest.getGuestEmail());
    }

    @Override
    public int getItemCount() {
        return invitations.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvGuestEmail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGuestEmail = itemView.findViewById(R.id.tvGuestEmail);
        }
    }
}


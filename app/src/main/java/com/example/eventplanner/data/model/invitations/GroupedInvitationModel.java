package com.example.eventplanner.data.model.invitations;
import com.example.eventplanner.data.model.events.EventModel;

import java.util.List;

public class GroupedInvitationModel {
    private EventModel event;
    private List<InvitationModel> invitations;

    // Getteri i setteri
    public EventModel getEvent() { return event; }
    public void setEvent(EventModel event) { this.event = event; }

    public List<InvitationModel> getInvitations() { return invitations; }
    public void setInvitations(List<InvitationModel> invitations) { this.invitations = invitations; }
}

package com.example.eventplanner.data.model.invitations;

import java.util.List;

public class InvitationRequestModel {
    private Integer eventId;
    private List<String> guestEmails;

    public InvitationRequestModel(Integer eventId, List<String> guestEmails) {
        this.eventId = eventId;
        this.guestEmails = guestEmails;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public List<String> getGuestEmails() {
        return guestEmails;
    }

    public void setGuestEmails(List<String> guestEmails) {
        this.guestEmails = guestEmails;
    }
}


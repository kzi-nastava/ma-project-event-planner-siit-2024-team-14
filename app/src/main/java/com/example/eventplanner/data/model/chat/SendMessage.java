package com.example.eventplanner.data.model.chat;

public class SendMessage {
    private int senderId; // need this bc server does not support auth when stomp is used
    private String content;
    private transient int recipientId;


    //region Constructors

    public SendMessage() {}

    public SendMessage(int from, int to, String content) {
        senderId = from;
        recipientId = to;
        this.content = content;
    }

    //endregion


    //region Getters and Setters

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(int recipientId) {
        this.recipientId = recipientId;
    }

    //endregion

}

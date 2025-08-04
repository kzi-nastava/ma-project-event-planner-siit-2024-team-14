package com.example.eventplanner.data.model.chat;

import com.example.eventplanner.data.model.users.UserModel;

import java.util.List;

public class ChatModel {
    private int id;
    private UserModel sender, recipient;
    private List<MessageModel> messages;

    //region Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserModel getSender() {
        return sender;
    }

    public void setSender(UserModel sender) {
        this.sender = sender;
    }

    public UserModel getRecipient() {
        return recipient;
    }

    public void setRecipient(UserModel recipient) {
        this.recipient = recipient;
    }

    public List<MessageModel> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageModel> messages) {
        this.messages = messages;
    }

    //endregion

}

package com.example.eventplanner.data.model.chat;

import java.time.LocalDateTime;

public class MessageModel {
    private int chatId, senderId;
    private String content;
    private LocalDateTime timestamp;


    public MessageModel() {}


    //region Getters and Setters

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    //endregion

}

package com.example.eventplanner.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.data.model.chat.MessageModel;


import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private final List<MessageModel> messages = new ArrayList<>();
    private final LayoutInflater inflater;
    private final int currentUserId;

    public MessageAdapter(Context context, int currentUserId) {
        this.inflater = LayoutInflater.from(context);
        this.currentUserId = currentUserId;
    }

    public void setMessages(List<MessageModel> messageList) {
        messages.clear();
        messages.addAll(messageList);
        notifyDataSetChanged();
    }

    public void addMessage(MessageModel message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    public void clearMessages() {
        messages.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        MessageModel message = messages.get(position);
        return message.getSenderId() == currentUserId ? 1 : 0;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            view = inflater.inflate(R.layout.item_message_sent, parent, false);
        } else {
            view = inflater.inflate(R.layout.item_message_received, parent, false);
        }
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        MessageModel message = messages.get(position);
        holder.messageText.setText(message.getContent());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;

        MessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.textMessage);
        }
    }
}

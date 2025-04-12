package com.group4.mindhaven;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// Adapter class that connects the message data to Recycler UI
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    // List that holds all messages (user and AI)
    private List<Message> messages;

    // Constructor to receive the list
    public ChatAdapter(List<Message> messages) {
        this.messages = messages;
    }

    // Instructs the RecyclerView which type of xml file to accord to for a given message
    @Override
    public int getItemViewType(int position) {
        // Enum.ordinal() gives 0 for USER, 1 for AI
        return messages.get(position).getType().ordinal();
    }

    // Inflates the contact item layout and creates a ViewHolder
    // Called when RecyclerView needs to create a new ViewHolder
    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {  // If the message belongs to User (index 0 in enum class)
            view = LayoutInflater.from(parent.getContext())
                    // inflate or render the message based on instructions in user's xml file
                    .inflate(R.layout.message_user, parent, false);
        } else {  // if it is AI (index 1 in enum class)
            view = LayoutInflater.from(parent.getContext())
                    // inflate or render the message based instructions in ai's xml file
                    .inflate(R.layout.messager_ai, parent, false);
        }
        // pass the new view to the view holder
        return new ChatViewHolder(view);
    }

    // Binds text into the TextView
    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Message message = messages.get(position);
        if (message.getType() == Message.MessageType.AI) {
            // For AI messages, use the TypeWriter effect
            TypeWriter typeWriter = new TypeWriter(holder.messageText);
            typeWriter.animateText(message.getContent());
        } else {
            // For user messages, show text immediately
            holder.messageText.setText(message.getContent());
        }
    }

    // Tells RecyclerView how many messages there are
    @Override
    public int getItemCount() {
        return messages.size();
    }

    // ViewHolder class to hold elements to each chat item
    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            // This finds the TextView inside
            messageText = itemView.findViewById(R.id.messageText);
        }
    }
    public void updateMessages(List<Message> newMessages) {
        this.messages = newMessages;
    }
}

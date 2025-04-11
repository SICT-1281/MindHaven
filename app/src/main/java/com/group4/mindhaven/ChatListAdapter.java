package com.group4.mindhaven;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// Adapter class for displaying the list of chat sessions (left side menu)
public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder>{
    // List of all chat sessions to display
    private List<ChatSession> chatSessions;

    // Listener for handling click events on each chat item
    private OnChatClickListener listener;

    // Define an interface (contract) for click events
    // The definition of this method is handed to ChatActivity in our design
    public interface OnChatClickListener {
        void onChatClick(String chatId);
        void onChatDelete(String chatId);
    }

    // Constructor for the adapter
    public ChatListAdapter(List<ChatSession> chatSessions, OnChatClickListener listener) {
        this.chatSessions = chatSessions; // Assign the provided list to the internal list
        this.listener = listener;         // Assign the click listener to internal listener
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate a chat_list_item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_item, parent, false);

        // Create and return a new ViewHolder containing this view
        return new ChatViewHolder(view);
    }

    // ViewHolder class holds the views for each list item (improves performance)
    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        // Get the ChatSession at the current position in the list
        ChatSession session = chatSessions.get(position);

        // Set the title of the chat session to the TextView
        holder.textView.setText(session.getTitle());

        // Set a click listener on the entire item view
        holder.itemView.setOnClickListener(v -> {
            // When clicked, trigger the listener and pass the selected chatId
            listener.onChatClick(session.getChatId());
        });
        holder.deleteButton.setOnClickListener(v -> {
            ChatManager manager = ChatManager.getInstance();
            if (manager.getAllChatSessions().size() <= 1) {
                Toast.makeText(v.getContext(), "Please keep at least one chat.", Toast.LENGTH_SHORT).show();
                return;
            }
            listener.onChatDelete(session.getChatId());
        });
    }
    @Override
    public int getItemCount() {
        // Return the total number of chat sessions
        return chatSessions.size();
    }

    // ViewHolder class holds the views for each list item (improves performance)
    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView textView;  // TextView to display the chat title\
        Button deleteButton;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            // Find the TextView inside the inflated layout
            textView = itemView.findViewById(R.id.chatTitle);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
    public void updateChatSessions(List<ChatSession> newChatSessions) {
        this.chatSessions = newChatSessions;
    }

}

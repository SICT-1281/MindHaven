package com.group4.mindhaven;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatListAdapter chatListAdapter;
    private ChatManager manager = ChatManager.getInstance();
    private String currentChatId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_history);

        currentChatId = getIntent().getStringExtra("currentChatId");

        recyclerView = findViewById(R.id.chatHistoryRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        chatListAdapter = new ChatListAdapter(
                new ArrayList<>(manager.getAllChatSessions()),
                new ChatListAdapter.OnChatClickListener() {
                    @Override
                    public void onChatClick(String chatId) {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("selectedChatId", chatId);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }

                    @Override
                    public void onChatDelete(String chatId) {
                        if (manager.getAllChatSessions().size() <= 1) {
                            Toast.makeText(ChatHistoryActivity.this,
                                    "Please keep at least one chat.",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (chatId.equals(currentChatId)) {
                            for (ChatSession session : manager.getAllChatSessions()) {
                                if (!session.getChatId().equals(chatId)) {
                                    currentChatId = session.getChatId();
                                    break;
                                }
                            }
                        }

                        manager.deleteChat(chatId, ChatHistoryActivity.this);
                        chatListAdapter.updateChatSessions(
                                new ArrayList<>(manager.getAllChatSessions()));
                        chatListAdapter.notifyDataSetChanged();
                    }
                });

        recyclerView.setAdapter(chatListAdapter);
    }
}
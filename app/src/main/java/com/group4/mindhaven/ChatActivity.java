package com.group4.mindhaven;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private static final int REQUEST_CHAT_HISTORY = 1001;
    ChatManager manager = ChatManager.getInstance();
    private EditText userInput;
    private Button sendButton;
    private RecyclerView chatView;
    private String chatID;
    private List<Message> messageList;
    private ChatAdapter chatAdapter;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        
        // Check if user is signed in
        if (!isUserSignedIn()) {
            Intent intent = new Intent(ChatActivity.this, SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.chat_activity);

        Button keywordButton1 = findViewById(R.id.KeywordButton1);
        Button keywordButton2 = findViewById(R.id.KeywordButton2);
        Button keywordButton3 = findViewById(R.id.KeywordButton3);
        Button keywordButton4 = findViewById(R.id.KeywordButton4);

        keywordButton1.setOnClickListener(v -> addKeywordToChat("General"));
        keywordButton2.setOnClickListener(v -> addKeywordToChat("School"));
        keywordButton3.setOnClickListener(v -> addKeywordToChat("Work"));
        keywordButton4.setOnClickListener(v -> addKeywordToChat("Home"));

        manager.loadChats(this);
        manager.initializeDefaultChat();

        List<ChatSession> chatSessions = manager.getAllChatSessions();
        chatID = chatSessions.get(0).getChatId();
        messageList = chatSessions.get(0).getMessages();

        chatView = findViewById(R.id.ChatView);
        userInput = findViewById(R.id.InputBox);
        sendButton = findViewById(R.id.SendButton);

        chatAdapter = new ChatAdapter(messageList);
        chatView.setLayoutManager(new LinearLayoutManager(this));
        chatView.setAdapter(chatAdapter);
        userInput.clearFocus();

        sendButton.setOnClickListener(v -> {
            String input = userInput.getText().toString().trim();
            if (!input.isEmpty()) {
                messageList.add(new Message(input, Message.MessageType.USER));
                chatAdapter.notifyItemInserted(messageList.size() - 1);
                chatView.scrollToPosition(messageList.size() - 1);
                getAiResponse(input);
                userInput.setText("");
            } else {
                Toast.makeText(this, "Please try again later", Toast.LENGTH_SHORT).show();
            }
        });

        FloatingActionButton addChatButton = findViewById(R.id.addChatButton);
        addChatButton.setOnClickListener(v -> showNewChatDialog());

        Button chatHistoryButton = findViewById(R.id.chatHistoryButton);
        chatHistoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(ChatActivity.this, ChatHistoryActivity.class);
            intent.putExtra("currentChatId", chatID);
            startActivityForResult(intent, REQUEST_CHAT_HISTORY);
        });

        setupBottomNavigation();
    }

    private boolean isUserSignedIn() {
        return mAuth.getCurrentUser() != null;
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Finish the current activity
                return true;
            } else if (itemId == R.id.navigation_chat) {
                // Already on chat page
                return true;
            } else if (itemId == R.id.navigation_profile) {
                Intent intent = new Intent(ChatActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish(); // Finish the current activity
                return true;
            }
            return false;
        });
    }

    private void showNewChatDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Chat Name");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Chat name");
        builder.setView(input);

        builder.setPositiveButton("Create", (dialog, which) -> {
            String title = input.getText().toString().trim();
            if (title.isEmpty()) {
                Toast.makeText(this, "Chat name cannot be empty.", Toast.LENGTH_SHORT).show();
                return;
            }
            ChatSession newSession = manager.createNewChat(title, this);
            chatID = newSession.getChatId();
            messageList = newSession.getMessages();
            updateChatWindow();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHAT_HISTORY && resultCode == RESULT_OK && data != null) {
            String selectedChatId = data.getStringExtra("selectedChatId");
            if (selectedChatId != null && !selectedChatId.equals(chatID)) {
                chatID = selectedChatId;
                updateChatWindow();
            }
        }
    }

    private void getAiResponse(String input) {
        new Thread(() -> {
            try {
                String apiKey = "AIzaSyAh6zFzjPmbhRWAGYoU42TIPLjoXzOv6ZQ";
                URL url = new URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey);

                manager.addMessageToChatHistory(chatID, new Message(input, Message.MessageType.USER));

                List<Message> history = manager.getChatHistory(chatID);
                StringBuilder historyBuilder = new StringBuilder();
                for (Message message : history) {
                    historyBuilder.append("USER: ").append(message.getContent()).append("\n");
                }
                
                ChatSession session = manager.getChatSession(chatID);
                List<String> keywords = session.getKeywords();
                String keywordsText = keywords.isEmpty() ? "" : "The user wants to discuss about: " + String.join(", "
                        , keywords) + ".\n\n";
                // Hints for the AI to act as a psychologist
                String prePrompt =
                        "This conversation is with an empathetic and gentle AI designed to support mental well-being. " +
                                "The AI always responds kindly, never harshly. " +
                                "It does not escalate situations, never recommends calling police or emergency services. " +
                                "Its purpose is to comfort and emotionally support the user in a calm, compassionate way when needed.\n\n" +
                                "And at the same time, it responds the user in a regular manner if user simply just want to chat\n\n" +
                                "The topic will be about " + keywordsText +
                                "Below is the conversation history. Please respond accurately and try to be concise:\n\n" +
                                historyBuilder.toString() + "\nAI:";

                String requestBody = "{ \"contents\": [" +
                        "{ \"role\": \"user\", \"parts\": [" +
                        "{ \"text\": \"" + prePrompt + input + "\" }" +
                        "] }" +
                        "] }";

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setDoOutput(true);
                connection.getOutputStream().write(requestBody.getBytes(StandardCharsets.UTF_8));

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }
                reader.close();

                String reply = extractReply(responseBuilder.toString());

                manager.addMessageToChatHistory(chatID, new Message(reply, Message.MessageType.AI));
                runOnUiThread(() -> {
                    messageList.add(new Message(reply, Message.MessageType.AI));
                    chatAdapter.notifyItemInserted(messageList.size() - 1);
                    chatView.scrollToPosition(messageList.size() - 1);
                    manager.saveChats(this);
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Failed to retrieve response", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateChatWindow() {
        ChatSession session = manager.getChatSession(chatID);
        messageList = session.getMessages();
        chatAdapter.updateMessages(messageList);
        chatAdapter.notifyDataSetChanged();
    }

    // Extract the AI's response from JSON format
    private String extractReply(String json) {
        try {
            // parse the raw JSON string into a JSON object
            JsonObject root = new Gson().fromJson(json, JsonObject.class);
            // Get the "candidates" array from the response
            JsonArray candidates = root.getAsJsonArray("candidates");

            // If candidates is not null and not empty
            if (candidates != null && !candidates.isEmpty()) {
                // Access the "content" object inside the first candidate
                JsonObject content = candidates.get(0).getAsJsonObject()
                        .getAsJsonObject("content");
                // From "content", retrieve the "parts" array
                JsonArray parts = content.getAsJsonArray("parts");

                // If "parts" is not null and not empty
                if (parts != null && !parts.isEmpty()) {
                    // Return the actual reply text from the first "parts" object
                    return parts.get(0).getAsJsonObject().get("text").getAsString();
                }
            }
            // Ai's response is empty, show error message
            return "Please try again";
        } catch (Exception e) {
            // if any error occurs during parsing, print the error
            e.printStackTrace();
            ;
            return "Error extracting response";
        }
    }
    private void addKeywordToChat(String keyword) {
        ChatSession session = manager.getChatSession(chatID);
        List<String> keywords = session.getKeywords();

        if (!keywords.contains(keyword)) {
            keywords.add(keyword);
            session.setKeywords(keywords);
            manager.saveChats(this);
            Toast.makeText(this, "Got it! ", Toast.LENGTH_SHORT).show();
        }
    }
}
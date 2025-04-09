package com.group4.mindhaven;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.List;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import kotlin.collections.ArrayDeque;


public class ChatActivity extends AppCompatActivity {
    private EditText userInput;
    private Button sendButton;
    private RecyclerView chatView;

    // Chat state
    private List<Message> messageList;
    private ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.navigation_chat) {
                Intent intent = new Intent(ChatActivity.this, ChatActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.navigation_profile) {
                Intent intent = new Intent(ChatActivity.this, ProfileActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });

        // Link views
        chatView = findViewById(R.id.ChatView);
        userInput = findViewById(R.id.InputBox);
        sendButton = findViewById(R.id.SendButton);

        // Initialize chat history and adapter
        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList);
        // Stack items vertically from top to bottom in standard vertical list style
        chatView.setLayoutManager(new LinearLayoutManager(this));
        chatView.setAdapter(chatAdapter);
        userInput.clearFocus(); // clear EditText flaw

        // set listener and action for send button
        sendButton.setOnClickListener( v -> {
            // Retrieve user input from the input box
            String input = userInput.getText().toString().trim();
            if (!input.isEmpty()) {
                messageList.add(new Message(input, Message.MessageType.USER));
                chatAdapter.notifyItemInserted(messageList.size() - 1);
                chatView.scrollToPosition(messageList.size() - 1);
                // retrieve the AI's response
                getAiResponse(input);
                userInput.setText("");  // Clear input box after sending the message
            } else {
                Toast.makeText(this, "Please try again later", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getAiResponse(String input) {
        new Thread(() -> {
            try {
                String apiKey = "AIzaSyAh6zFzjPmbhRWAGYoU42TIPLjoXzOv6ZQ";
                URL url = new URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key="
                        + apiKey);

                // Hints for the AI to act as a psychologist
                String prePrompt =
                        "This conversation is with an empathetic and gentle AI designed to support mental well-being. " +
                                "The AI always responds kindly, never harshly. " +
                                "It does not escalate situations, never recommends calling police or emergency services. " +
                                "Its sole purpose is to comfort and emotionally support the user in a calm, compassionate way.\n\n" +
                                "Below is the user's message. Please respond with kindness and emotional support only:\n\n";

                // Request body that includes the hint and user input
                String requestBody = "{ \"contents\": [" +
                        "{ \"role\": \"user\", \"parts\": [" +
                        "{ \"text\": \"" + prePrompt + input + "\" }" +
                        "] }" +
                        "] }";

                // Open a connection to the target URL (preparing to send an HTTP request)
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                // Set the request method to POST (sending data)
                connection.setRequestMethod("POST");
                // Signal the server that we are sending JSON data, and it's encoded in UTF-8
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                // Enable output so we can write data (the JSON body) to this connection
                connection.setDoOutput(true);

                // Send request
                connection.getOutputStream().write(requestBody.getBytes("UTF-8"));
                // Retrieve the response from server, and create a buffered reader to read it
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                // String builder as container to contain responses
                StringBuilder responseBuilder = new StringBuilder();

                // String to store each line of the responses
                String line;
                while ((line = reader.readLine()) != null) {
                    // Append each line to the container
                    responseBuilder.append(line);
                }
                reader.close();

                // Convert full API response to a string
                String responseText = responseBuilder.toString();
                // Use custom extractor to get AI reply
                String reply = extractReply(responseText);

                runOnUiThread(() -> {
                    messageList.add(new Message(reply, Message.MessageType.AI));
                    chatAdapter.notifyItemInserted(messageList.size() - 1);
                    chatView.scrollToPosition(messageList.size() - 1);
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this,
                        "Failed to retrieve response, please check internet connection",
                        Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    // Extract the AI's response from JSON format
    private String extractReply(String json) {
        try {
            // parse the raw JSON string into a JSON object
            JsonObject root = new Gson().fromJson(json, JsonObject.class);
            // Get the "candidates" array from the response
            JsonArray candidates = root.getAsJsonArray("candidates");

            // If candidates is not null and not empty
            if (candidates != null && candidates.size() > 0) {
                // Access the "content" object inside the first candidate
                JsonObject content = candidates.get(0).getAsJsonObject()
                        .getAsJsonObject("content");
                // From "content", retrieve the "parts" array
                JsonArray parts = content.getAsJsonArray("parts");

                // If "parts" is not null and not empty
                if (parts != null && parts.size() > 0) {
                    // Return the actual reply text from the first "parts" object
                    return parts.get(0).getAsJsonObject().get("text").getAsString();
                }
            }
            // Ai's response is empty, show error message
            return "Please try again";
        } catch (Exception e) {
            // if any error occurs during parsing, print the error
            e.printStackTrace();;
            return "Error extracting response";
        }
    }
}
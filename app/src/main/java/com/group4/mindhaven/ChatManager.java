package com.group4.mindhaven;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ChatManager {
    private static ChatManager instance;

    private Map<String, ChatSession> chatMap;

    private Map<String, List<Message>> chatHistories = new HashMap<>();


    public ChatManager () {
        this.chatMap = new HashMap<>();
        this.chatHistories = new HashMap<>();
    }

    void addChatSession (ChatSession session) {
        chatMap.put(session.getChatId(), session);
    }

    void removeChatSession (String chatID) {
        chatMap.remove(chatID);
    }
    public static ChatManager getInstance() {
        if (instance == null) {
            instance = new ChatManager();
        }
        return instance;
    }

    protected void saveChats(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("ChatMapCollector",
                MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        // convert contactList into a JSON string to store in SharedPreferences
        String json = gson.toJson(chatMap);
        editor.putString("contacts", json);
        editor.apply(); // Save data
    }

    // Loads the contacts in SharedPreference
    protected Map<String, ChatSession> loadChats(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("ChatMapCollector", MODE_PRIVATE);
        String json = prefs.getString("contacts", null);

        if (json != null) {
            Gson gson = new Gson();
            // declare the type we will be using
            Type type = new TypeToken<Map<String, ChatSession>>() {}.getType();
            // convert JSON back to ArrayList of contacts
            return gson.fromJson(json, type);
        } else {
            return new HashMap<>(); // Start blank if no data has found
        }
    }

    public ChatSession getChatSession(String chatID) {
        return chatMap.get(chatID);
    }

    public List<ChatSession> getAllChatSessions() {
        return new ArrayList<>(chatMap.values());
    }

    public void initializeDefaultChat() {
        if (chatMap.isEmpty()) {
            // Generate a truly random ID
            String defaultChatId = UUID.randomUUID().toString();
            // Create a default and empty session
            ChatSession defaultSession = new ChatSession(defaultChatId, "Default",
                    new ArrayList<>(), new ArrayList<>(), false);
            chatMap.put(defaultChatId, defaultSession);
        }
    }

    public void initializeChatHistory(String chatId) {
        if (!chatHistories.containsKey(chatId)) {
            chatHistories.put(chatId, new ArrayList<>());
        }
    }

    public void addMessageToChatHistory(String chatId, Message message) {
        initializeChatHistory(chatId);
        List<Message> history = chatHistories.get(chatId);
        history.add(message);

        int maxHistorySize = 10;
        if (history.size() > maxHistorySize) {
            history.remove(0);
        }
    }

    public List<Message> getChatHistory(String chatId) {
        return chatHistories.getOrDefault(chatId, new ArrayList<>());
    }

    public ChatSession createNewChat(String title, Context context) {
        // Random ID
        String newChatId = UUID.randomUUID().toString();
        ChatSession newSession = new ChatSession(newChatId,
                title, new ArrayList<>(), new ArrayList<>(), false);
        chatMap.put(newChatId, newSession);
        saveChats(context);

        return getChatSession(newChatId);
    }

    public void deleteChat(String chatId, Context context) {
        chatMap.remove(chatId); // remove this chat from map
        chatHistories.remove(chatId);
        saveChats(context);  // save changes
    }



    public void clearChatHistory(String chatId) {
        chatHistories.remove(chatId);
    }
}
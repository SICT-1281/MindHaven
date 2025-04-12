package com.group4.mindhaven;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProfileActivity extends AppCompatActivity {
    private TextView userNameText;
    private TextView userEmailText;
    private MaterialButton signOutButton;
    private MaterialButton savedItemsButton;
    private MaterialButton editButton;
    private SharedPreferences sharedPreferences;
    private TextView chatCountText;
    private TextView quotesCountText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Check if user is signed in
        sharedPreferences = getSharedPreferences("MindHavenPrefs", MODE_PRIVATE);
        if (!sharedPreferences.getBoolean("isSignedIn", false)) {
            Intent intent = new Intent(ProfileActivity.this, SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }
        
        setContentView(R.layout.profile_activity);

        // Initialize views
        userNameText = findViewById(R.id.userNameText);
        userEmailText = findViewById(R.id.userEmailText);
        signOutButton = findViewById(R.id.signOutButton);
        savedItemsButton = findViewById(R.id.savedItemsButton);
        editButton = findViewById(R.id.editButton);
        chatCountText = findViewById(R.id.chatCountText);
        quotesCountText = findViewById(R.id.quotesCountText);

        // Load user data
        loadUserData();
        updateStatsCounts();

        // Set click listeners
        signOutButton.setOnClickListener(v -> signOut());
        savedItemsButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, SavedActivity.class);
            startActivity(intent);
        });
        
        // Set up edit button click listener
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog();
            }
        });

        // Set up bottom navigation
        setupBottomNavigation();
    }

    private void showEditDialog() {
        // Create the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.RoundedDialog);
        
        // Create the layout
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 16);

        // Create name input
        TextInputLayout nameLayout = new TextInputLayout(this);
        nameLayout.setHint("Name");
        nameLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
        nameLayout.setBoxCornerRadii(8, 8, 8, 8);
        nameLayout.setBoxStrokeColor(getResources().getColor(R.color.colorPrimary, getTheme()));
        
        TextInputEditText nameInput = new TextInputEditText(this);
        nameInput.setText(userNameText.getText());
        nameInput.setSingleLine(true);
        nameInput.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        nameLayout.addView(nameInput);

        // Create email input
        TextInputLayout emailLayout = new TextInputLayout(this);
        emailLayout.setHint("Email");
        emailLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
        emailLayout.setBoxCornerRadii(8, 8, 8, 8);
        emailLayout.setBoxStrokeColor(getResources().getColor(R.color.colorPrimary, getTheme()));
        
        TextInputEditText emailInput = new TextInputEditText(this);
        emailInput.setText(userEmailText.getText());
        emailInput.setSingleLine(true);
        emailInput.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailLayout.addView(emailInput);

        // Add inputs to layout
        layout.addView(nameLayout);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.topMargin = 16;
        layout.addView(emailLayout, params);

        // Set the custom view
        builder.setView(layout);

        // Set the title
        builder.setTitle("Edit Profile");

        // Set the buttons
        builder.setPositiveButton("Save", (dialog, which) -> {
            String newName = nameInput.getText().toString().trim();
            String newEmail = emailInput.getText().toString().trim();

            if (newName.isEmpty()) {
                nameLayout.setError("Name is required");
                return;
            }

            if (newEmail.isEmpty()) {
                emailLayout.setError("Email is required");
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                emailLayout.setError("Please enter a valid email address");
                return;
            }

            // Save to SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("userName", newName);
            editor.putString("userEmail", newEmail);
            editor.apply();

            // Update UI
            userNameText.setText(newName);
            userEmailText.setText(newEmail);

            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", null);

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        dialog.show();
    }

    private void loadUserData() {
        String name = sharedPreferences.getString("userName", "User");
        String email = sharedPreferences.getString("userEmail", "");

        userNameText.setText(name);
        userEmailText.setText(email);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.navigation_chat) {
                Intent intent = new Intent(ProfileActivity.this, ChatActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.navigation_profile) {
                // Already on profile page
                return true;
            }
            return false;
        });
    }

    private void signOut() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isSignedIn", false);
        editor.apply();
        
        Intent intent = new Intent(ProfileActivity.this, SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void updateStatsCounts() {
        // Update chat count
        SharedPreferences chatPrefs = getSharedPreferences("ChatMapCollector", MODE_PRIVATE);
        String chatJson = chatPrefs.getString("contacts", "{}");
        Type type = new TypeToken<Map<String, ChatSession>>(){}.getType();
        Map<String, ChatSession> chatMap = new Gson().fromJson(chatJson, type);
        int chatCount = chatMap != null ? chatMap.size() : 0;
        chatCountText.setText(String.valueOf(chatCount));

        // Update quotes count
        SharedPreferences quotesPrefs = getSharedPreferences("saved_quotes", MODE_PRIVATE);
        Set<String> quotesSet = quotesPrefs.getStringSet("quotes", new HashSet<>());
        int quotesCount = quotesSet.size();
        quotesCountText.setText(String.valueOf(quotesCount));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStatsCounts();
    }
} 
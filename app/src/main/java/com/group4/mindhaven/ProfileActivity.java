package com.group4.mindhaven;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class ProfileActivity extends AppCompatActivity {
    private TextInputEditText nameInput;
    private TextInputEditText emailInput;
    private TextInputEditText phoneInput;
    private TextInputEditText bioInput;
    private MaterialButton saveButton;
    private MaterialButton signOutButton;
    private MaterialButton savedItemsButton;
    private SharedPreferences sharedPreferences;

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
        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        phoneInput = findViewById(R.id.phoneInput);
        bioInput = findViewById(R.id.bioInput);
        saveButton = findViewById(R.id.saveButton);
        signOutButton = findViewById(R.id.signOutButton);
        savedItemsButton = findViewById(R.id.savedItemsButton);

        // Load user data
        loadUserData();

        // Set click listeners
        saveButton.setOnClickListener(v -> saveUserData());
        signOutButton.setOnClickListener(v -> signOut());
        savedItemsButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, SavedActivity.class);
            startActivity(intent);
        });

        // Set up bottom navigation
        setupBottomNavigation();
    }

    private void loadUserData() {
        String name = sharedPreferences.getString("userName", "User");
        String email = sharedPreferences.getString("userEmail", "");
        String phone = sharedPreferences.getString("userPhone", "0");
        String bio = sharedPreferences.getString("userBio", "Hi");

        nameInput.setText(name);
        emailInput.setText(email);
        phoneInput.setText(phone);
        bioInput.setText(bio);
    }

    private void saveUserData() {
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String bio = bioInput.getText().toString().trim();

        if (name.isEmpty()) {
            nameInput.setError("Name is required");
            return;
        }

        if (email.isEmpty()) {
            emailInput.setError("Email is required");
            return;
        }

        if (phone.isEmpty()) {
            phoneInput.setError("Phone number is required");
            return;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userName", name);
        editor.putString("userEmail", email);
        editor.putString("userPhone", phone);
        editor.putString("userBio", bio);
        editor.apply();

        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
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
} 
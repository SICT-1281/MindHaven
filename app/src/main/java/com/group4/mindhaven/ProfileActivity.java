package com.group4.mindhaven;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import android.util.Log;

public class ProfileActivity extends AppCompatActivity {
    private TextView userNameText;
    private TextView userEmailText;
    private SharedPreferences sharedPreferences;
    private TextView chatCountText;
    private TextView quotesCountText;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        
        // Check if user is signed in
        if (!isUserSignedIn()) {
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
        MaterialButton signOutButton = findViewById(R.id.signOutButton);
        MaterialButton savedItemsButton = findViewById(R.id.savedItemsButton);
        MaterialButton editButton = findViewById(R.id.editButton);
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

    private boolean isUserSignedIn() {
        return mAuth.getCurrentUser() != null;
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
            String newName = Objects.requireNonNull(nameInput.getText()).toString().trim();
            String newEmail = Objects.requireNonNull(emailInput.getText()).toString().trim();

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

            // Get current user ID
            String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
            Log.d("ProfileActivity", "User ID: " + userId);
            
            // Update user data in Firestore
            Map<String, Object> updates = new HashMap<>();
            updates.put("name", newName);
            updates.put("email", newEmail);
            Log.d("ProfileActivity", "Updating Firestore with: " + updates);
            
            db.collection("users").document(userId)
                    .update(updates)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("ProfileActivity", "Firestore update successful");
                        // Update UI immediately with the new values
                        userNameText.setText(newName);
                        userEmailText.setText(newEmail);
                        
                        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ProfileActivity", "Firestore update failed", e);
                        Toast.makeText(this, "Error updating profile: " + e.getMessage(), 
                                Toast.LENGTH_SHORT).show();
                    });
        });

        builder.setNegativeButton("Cancel", null);

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.dialog_background);
        dialog.show();
    }

    private void loadUserData() {
        // Get current user
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        Log.d("ProfileActivity", "Loading user data for ID: " + userId);
        
        // Get user data from Firestore
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String email = documentSnapshot.getString("email");
                        Log.d("ProfileActivity", "Retrieved from Firestore - Name: " + name + ", Email: " + email);
                        
                        userNameText.setText(name);
                        userEmailText.setText(email);
                    } else {
                        Log.d("ProfileActivity", "No user document found in Firestore");
                        // Create a new user document if it doesn't exist
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("name", userNameText.getText().toString());
                        userData.put("email", userEmailText.getText().toString());
                        
                        db.collection("users").document(userId)
                                .set(userData)
                                .addOnSuccessListener(aVoid -> Log.d("ProfileActivity", "Created new user document"))
                                .addOnFailureListener(e -> Log.e("ProfileActivity", "Error creating user document", e));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ProfileActivity", "Error loading user data", e);
                    Toast.makeText(this, "Error loading user data: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else // Already on profile page
                if (itemId == R.id.navigation_chat) {
                Intent intent = new Intent(ProfileActivity.this, ChatActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else return itemId == R.id.navigation_profile;
        });
    }

    private void signOut() {
        mAuth.signOut();
        
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

    public void openResourceLink(View view) {
        String url = "";
        // Get the index of the clicked LinearLayout in its parent container
        int index = ((ViewGroup) view.getParent()).indexOfChild(view);
        
        // Determine the URL based on the index
        if (index == 0) {
            url = "https://www.befrienders.org";
        } else if (index == 1) {
            url = "https://www.mhanational.org";
        } else if (index == 2) {
            url = "https://www.mind.org.uk";
        } else if (index == 3) {
            url = "https://www.imhrc.org";
        } else if (index == 4) {
            url = "https://www.unicef.org/mental-health";
        }

        if (!url.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        }
    }
} 
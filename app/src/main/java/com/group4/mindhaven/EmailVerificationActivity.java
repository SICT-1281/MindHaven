package com.group4.mindhaven;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EmailVerificationActivity extends AppCompatActivity {

    private MaterialButton verifyButton;
    private MaterialButton resendButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String name;
    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        // Get data from intent
        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize views
        verifyButton = findViewById(R.id.verifyButton);
        resendButton = findViewById(R.id.resendButton);

        // Set click listeners
        verifyButton.setOnClickListener(v -> checkEmailVerification());
        resendButton.setOnClickListener(v -> resendVerificationEmail());

        // Send verification email
        sendVerificationEmail();
    }

    private void sendVerificationEmail() {
        // Create user with email and password
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Send verification email
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            user.sendEmailVerification()
                                    .addOnCompleteListener(emailTask -> {
                                        if (emailTask.isSuccessful()) {
                                            Toast.makeText(EmailVerificationActivity.this, 
                                                    "Verification email sent to " + email, 
                                                    Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(EmailVerificationActivity.this, 
                                                    "Failed to send verification email: " + 
                                                    Objects.requireNonNull(emailTask.getException()).getMessage(), 
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        // If sign up fails, display a message to the user
                        Toast.makeText(EmailVerificationActivity.this, 
                                "Authentication failed: " + Objects.requireNonNull(task.getException()).getMessage(), 
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void resendVerificationEmail() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(EmailVerificationActivity.this, 
                                    "Verification email resent to " + email, 
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(EmailVerificationActivity.this, 
                                    "Failed to resend verification email: " + 
                                    Objects.requireNonNull(task.getException()).getMessage(), 
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void checkEmailVerification() {
        // Reload the user to get the latest email verification status
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.reload().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser reloadedUser = mAuth.getCurrentUser();
                    if (reloadedUser != null && reloadedUser.isEmailVerified()) {
                        // Email is verified, create user profile in Firestore
                        String userId = reloadedUser.getUid();
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("name", name);
                        userData.put("email", email);

                        db.collection("users").document(userId)
                                .set(userData)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(EmailVerificationActivity.this, 
                                            "Email verified. Account created successfully", 
                                            Toast.LENGTH_SHORT).show();
                                    navigateToMainActivity();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(EmailVerificationActivity.this, 
                                            "Error creating profile: " + e.getMessage(), 
                                            Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(EmailVerificationActivity.this, 
                                "Email not verified. Please check your email and click the verification link.", 
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(EmailVerificationActivity.this, 
                            "Error reloading user: " + Objects.requireNonNull(task.getException()).getMessage(), 
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(EmailVerificationActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
} 
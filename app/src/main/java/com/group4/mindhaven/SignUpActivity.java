package com.group4.mindhaven;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class SignUpActivity extends AppCompatActivity {

    private TextInputEditText nameInput;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private TextInputEditText confirmPasswordInput;
    private MaterialButton signUpButton;
    private TextView signInPrompt;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MindHavenPrefs", MODE_PRIVATE);

        // Initialize views
        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        signUpButton = findViewById(R.id.signupButton);
        signInPrompt = findViewById(R.id.signinPrompt);

        // Set click listeners
        signUpButton.setOnClickListener(v -> handleSignUp());
        signInPrompt.setOnClickListener(v -> navigateToSignIn());
    }

    private void handleSignUp() {
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(name)) {
            nameInput.setError("Name is required");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Password is required");
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordInput.setError("Please confirm your password");
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordInput.setError("Passwords do not match");
            return;
        }

        // For demo purposes, we'll just save the user info
        // In a real app, you would register the user with a backend server
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isSignedIn", true);
        editor.putString("userName", name);
        editor.putString("userEmail", email);
        
        // Set default values for bio and phone number
        editor.putString("userBio", "Hi~ Introduce Yourself Please~");
        editor.putString("userPhone", "000-000-0000");
        
        editor.apply();

        Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show();

        // Navigate to main activity
        navigateToMainActivity();
    }

    private void navigateToSignIn() {
        Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
} 
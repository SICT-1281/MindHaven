package com.group4.mindhaven;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class SignInActivity extends AppCompatActivity {

    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private MaterialButton signInButton;
    private TextView signUpPrompt;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_activity);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MindHavenPrefs", MODE_PRIVATE);

        // Check if user is already signed in
        if (isUserSignedIn()) {
            navigateToMainActivity();
            return;
        }

        // Initialize views
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        signInButton = findViewById(R.id.signinButton);
        signUpPrompt = findViewById(R.id.signupPrompt);

        // Set click listeners
        signInButton.setOnClickListener(v -> handleSignIn());
        signUpPrompt.setOnClickListener(v -> navigateToSignUp());
    }

    private boolean isUserSignedIn() {
        return sharedPreferences.getBoolean("isSignedIn", false);
    }

    private void handleSignIn() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Password is required");
            return;
        }

        // For demo purposes, we'll use a simple validation
        // In a real app, you would validate against a backend server
        if (email.equals("demo@example.com") && password.equals("password")) {
            // Save sign-in state
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isSignedIn", true);
            editor.putString("userEmail", email);
            editor.apply();

            // Navigate to main activity
            navigateToMainActivity();
        } else {
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToSignUp() {
        Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
} 
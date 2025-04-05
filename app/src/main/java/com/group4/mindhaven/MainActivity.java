package com.group4.mindhaven;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button ChatButton = findViewById(R.id.ChatButton);
        Button BackupButton = findViewById(R.id.BackupButton);

        ChatButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ChatActivity.class);
            startActivity(intent);
        });

        BackupButton.setOnClickListener(v -> {
            setContentView(R.layout.chat_page);
        });
    }
}

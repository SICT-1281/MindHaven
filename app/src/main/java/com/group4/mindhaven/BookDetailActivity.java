package com.group4.mindhaven;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class BookDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("MindHavenPrefs", MODE_PRIVATE);
        if (!sharedPreferences.getBoolean("isSignedIn", false)) {
            Intent intent = new Intent(BookDetailActivity.this, SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }
        
        setContentView(R.layout.book_detail);

        Intent intent = getIntent();
        String bookTitle = intent.getStringExtra("bookTitle");
        int imageResId = intent.getIntExtra("bookImage", 0);
        String bookLink = intent.getStringExtra("bookLink");
        String authorName = intent.getStringExtra("bookAuthor");

        TextView titleText = findViewById(R.id.bookTitleText);
        ImageView bookImage = findViewById(R.id.bookCoverImage);
        Button readMoreButton = findViewById(R.id.openLinkButton);
        TextView authorNameText = findViewById(R.id.bookAuthorText);

        titleText.setText(bookTitle);
        bookImage.setImageResource(imageResId);
        authorNameText.setText(authorName);

        readMoreButton.setOnClickListener(v -> {
            String url = getIntent().getStringExtra("bookLink");
            AlertDialog.Builder builder = new AlertDialog.Builder(BookDetailActivity.this);
            builder.setTitle("Book Link");
            builder.setMessage(bookLink);
            builder.setPositiveButton("Go to Link", (dialog, which) -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            builder.show();
        });
    }
}
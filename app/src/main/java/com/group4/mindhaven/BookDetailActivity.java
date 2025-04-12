package com.group4.mindhaven;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BookDetailActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private Book currentBook;
    private List<Book> savedBooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("MindHavenPrefs", MODE_PRIVATE);
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

        currentBook = new Book(bookTitle, imageResId, bookLink, authorName);

        TextView titleText = findViewById(R.id.bookTitleText);
        ImageView bookImage = findViewById(R.id.bookCoverImage);
        Button readMoreButton = findViewById(R.id.openLinkButton);
        Button saveBookButton = findViewById(R.id.saveBookButton);
        TextView authorNameText = findViewById(R.id.bookAuthorText);

        titleText.setText(bookTitle);
        bookImage.setImageResource(imageResId);
        authorNameText.setText(authorName);

        // Load saved books
        loadSavedBooks();

        // Check if book is already saved
        if (isBookSaved()) {
            saveBookButton.setText("Unsave Book");
        }

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

        saveBookButton.setOnClickListener(v -> {
            if (isBookSaved()) {
                unsaveBook();
                saveBookButton.setText("Save Book");
                Toast.makeText(this, "Book unsaved", Toast.LENGTH_SHORT).show();
            } else {
                saveBook();
                saveBookButton.setText("Unsave Book");
                Toast.makeText(this, "Book saved", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadSavedBooks() {
        String savedBooksJson = sharedPreferences.getString("savedBooks", "[]");
        Type type = new TypeToken<List<Book>>(){}.getType();
        savedBooks = new Gson().fromJson(savedBooksJson, type);
        if (savedBooks == null) {
            savedBooks = new ArrayList<>();
        }
    }

    private boolean isBookSaved() {
        for (Book book : savedBooks) {
            if (book.getTitle().equals(currentBook.getTitle())) {
                return true;
            }
        }
        return false;
    }

    private void saveBook() {
        savedBooks.add(currentBook);
        saveBooksToPreferences();
    }

    private void unsaveBook() {
        savedBooks.removeIf(book -> book.getTitle().equals(currentBook.getTitle()));
        saveBooksToPreferences();
    }

    private void saveBooksToPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("savedBooks", new Gson().toJson(savedBooks));
        editor.apply();
    }
}
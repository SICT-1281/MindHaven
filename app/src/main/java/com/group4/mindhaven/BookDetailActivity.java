package com.group4.mindhaven;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BookDetailActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private Book currentBook;
    private List<Book> savedBooks;
    private ImageButton saveButton;

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

        // Get book data from intent
        Intent intent = getIntent();
        String bookTitle = intent.getStringExtra("bookTitle");
        int imageResId = intent.getIntExtra("bookImage", R.drawable.book_placeholder_background);
        String bookLink = intent.getStringExtra("bookLink");
        String authorName = intent.getStringExtra("bookAuthor");

        // Create current book with the image resource
        currentBook = new Book(bookTitle, imageResId, bookLink, authorName);

        // Setup toolbar and back button
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        // Initialize views
        ImageView bookCoverImage = findViewById(R.id.bookCoverImage);
        TextView titleText = findViewById(R.id.bookTitleText);
        TextView authorText = findViewById(R.id.bookAuthorText);
        TextView summaryText = findViewById(R.id.bookSummaryText);
        MaterialButton readMoreButton = findViewById(R.id.openLinkButton);
        saveButton = findViewById(R.id.saveButton);

        // Set book data
        if (imageResId != 0) {
            bookCoverImage.setImageResource(imageResId);
        } else {
            bookCoverImage.setImageResource(R.drawable.book_placeholder_background);
        }
        titleText.setText(bookTitle);
        authorText.setText("By " + authorName);
        summaryText.setText(getBookSummary(bookTitle));

        // Load saved books and update button state
        loadSavedBooks();
        updateSaveButtonState();

        // Set click listeners
        readMoreButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(BookDetailActivity.this);
            builder.setTitle("Book Link");
            builder.setMessage(bookLink);
            builder.setPositiveButton("Go to Link", (dialog, which) -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(bookLink));
                startActivity(browserIntent);
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            builder.show();
        });

        saveButton.setOnClickListener(v -> {
            if (isBookSaved()) {
                unsaveBook();
                updateSaveButtonState();
                Toast.makeText(this, "Book removed from saved items", Toast.LENGTH_SHORT).show();
            } else {
                saveBook();
                updateSaveButtonState();
                Toast.makeText(this, "Book saved successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getBookSummary(String bookTitle) {
        // Add summaries for each book
        switch (bookTitle) {
            case "As a Man Thinketh":
                return "A philosophical examination of the power of thought, demonstrating how our thoughts shape our character, circumstances, and ultimately our destiny. The book emphasizes personal responsibility and the role of positive thinking in achieving success and happiness.";
            case "Think and Grow Rich":
                return "A comprehensive guide to personal development and success, based on Hill's study of successful individuals. The book outlines principles for achieving wealth and personal success, emphasizing the power of personal beliefs and persistence.";
            case "Meditations":
                return "A series of personal writings reflecting on life, leadership, and philosophy. Marcus Aurelius's private notes to himself offer timeless wisdom on dealing with adversity, maintaining focus, and living a meaningful life.";
            case "Self-Reliance":
                return "An essay advocating for individualism and personal independence. Emerson encourages readers to trust their own thoughts and intuitions, resist conformity, and maintain their integrity in the face of social pressures.";
            case "The Prophet":
                return "A collection of poetic essays covering various aspects of life, from love and marriage to work and death. Through the voice of the prophet Al Mustafa, Gibran shares profound insights about the human experience.";
            default:
                return "A transformative book that explores profound insights about personal growth, self-discovery, and the pursuit of wisdom. Through engaging narrative and practical wisdom, it guides readers on a journey of understanding and self-improvement.";
        }
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
        // Check if book already exists
        for (Book book : savedBooks) {
            if (book.getTitle().equals(currentBook.getTitle())) {
                return; // Book already saved
            }
        }
        // Add new book with all properties including image
        savedBooks.add(new Book(
            currentBook.getTitle(),
            currentBook.getImageResId(),
            currentBook.getLink(),
            currentBook.getName()
        ));
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

    private void updateSaveButtonState() {
        boolean isSaved = isBookSaved();
        saveButton.setImageResource(isSaved ? R.drawable.ic_star_filled : R.drawable.ic_star_outline);
    }
}
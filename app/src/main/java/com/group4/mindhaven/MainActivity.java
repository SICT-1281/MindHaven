package com.group4.mindhaven;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;


public class MainActivity extends AppCompatActivity {

    private final List<Book> BookList = BookDataProvider.getBooks();
    private FirebaseAuth mAuth;
    private TextView dailyQuoteText;
    private TextView quoteAuthor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Check if user is signed in before calling super.onCreate
        mAuth = FirebaseAuth.getInstance();
        if (!isUserSignedIn()) {
            navigateToSignIn();
            return;
        }
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupBottomNavigation();
        setupBookList();
        setupFloatingActionButtons();
    }

    private boolean isUserSignedIn() {
        return mAuth.getCurrentUser() != null;
    }

    private void navigateToSignIn() {
        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                // Already on home page
                return true;
            } else if (itemId == R.id.navigation_chat) {
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                startActivity(intent);
                finish(); // Finish the current activity
                return true;
            } else if (itemId == R.id.navigation_profile) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish(); // Finish the current activity
                return true;
            }
            return false;
        });
    }

    private void setupBookList() {
        // Left side chat list
        RecyclerView bookListView = findViewById(R.id.BookListRecyclerView);

        // Stack items horizontally from left to right
        bookListView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
        BookListAdapter bookListAdapter = new BookListAdapter(BookList,
                position -> {
                    Book selectedBook = BookList.get(position);
                    Intent intent = new Intent(MainActivity.this, BookDetailActivity.class);
                    intent.putExtra("bookTitle", selectedBook.getTitle());
                    intent.putExtra("bookImage", selectedBook.getImageResId());
                    intent.putExtra("bookLink", selectedBook.getLink());
                    intent.putExtra("bookAuthor", selectedBook.getName());
                    startActivity(intent);
                });
        bookListView.setAdapter(bookListAdapter);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupFloatingActionButtons() {
        FloatingActionButton startChatFAB = findViewById(R.id.StartChatFAB);
        startChatFAB.setContentDescription("Start a new chat");

        // Variables to track movement
        final float[] dX = new float[1];
        final float[] dY = new float[1];
        final int[] lastAction = new int[1];

        startChatFAB.setOnTouchListener((view, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    dX[0] = view.getX() - event.getRawX();
                    dY[0] = view.getY() - event.getRawY();
                    lastAction[0] = MotionEvent.ACTION_DOWN;
                    return true;

                case MotionEvent.ACTION_MOVE:
                    float newX = event.getRawX() + dX[0];
                    float newY = event.getRawY() + dY[0];
                    
                    // Add bounds checking
                    int screenWidth = getResources().getDisplayMetrics().widthPixels;
                    int screenHeight = getResources().getDisplayMetrics().heightPixels;
                    int buttonWidth = view.getWidth();
                    int buttonHeight = view.getHeight();
                    
                    // Keep button within screen bounds
                    newX = Math.max(0, Math.min(screenWidth - buttonWidth, newX));
                    newY = Math.max(0, Math.min(screenHeight - buttonHeight, newY));
                    
                    view.animate()
                        .x(newX)
                        .y(newY)
                        .setDuration(0)
                        .start();
                    lastAction[0] = MotionEvent.ACTION_MOVE;
                    return true;

                case MotionEvent.ACTION_UP:
                    if (lastAction[0] == MotionEvent.ACTION_DOWN) {
                        // This was a click (no movement), launch chat activity
                        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                        startActivity(intent);
                    }
                    return true;

                default:
                    return false;
            }
        });

        setupDailyQuote();
    }

    private void setupDailyQuote() {
        dailyQuoteText = findViewById(R.id.dailyQuoteText);
        quoteAuthor = findViewById(R.id.quoteAuthor);
        FloatingActionButton newQuoteFAB = findViewById(R.id.newQuoteFAB);
        FloatingActionButton saveQuoteFAB = findViewById(R.id.saveQuoteFAB);

        showRandomQuote();

        newQuoteFAB.setOnClickListener(v -> showRandomQuote());

        saveQuoteFAB.setOnClickListener(v -> {
            String currentQuote = dailyQuoteText.getText().toString();
            String author = quoteAuthor.getText().toString();
            if (!currentQuote.equals("Click to get inspiration!")) {
                saveQuote(currentQuote + " - " + author);
            }
        });
    }

    private void showRandomQuote() {
        Quote[] quotes = {
            new Quote("The only way to do great work is to love what you do.", "Steve Jobs"),
            new Quote("Believe you can and you're halfway there.", "Theodore Roosevelt"),
            new Quote("It does not matter how slowly you go as long as you do not stop.", "Confucius"),
            new Quote("Success is not final, failure is not fatal: it is the courage to continue that counts.", "Winston Churchill"),
            new Quote("The future belongs to those who believe in the beauty of their dreams.", "Eleanor Roosevelt"),
            new Quote("Happiness is not something ready made. It comes from your own actions.", "Dalai Lama"),
            new Quote("The only limit to our realization of tomorrow is our doubts of today.", "Franklin D. Roosevelt"),
            new Quote("You are never too old to set another goal or to dream a new dream.", "C.S. Lewis"),
            new Quote("The best way to predict the future is to create it.", "Peter Drucker"),
            new Quote("Life is 10% what happens to us and 90% how we react to it.", "Charles R. Swindoll")
        };

        Random random = new Random();
        Quote selectedQuote = quotes[random.nextInt(quotes.length)];
        
        dailyQuoteText.setText(selectedQuote.text);
        quoteAuthor.setText(selectedQuote.author);
    }

    private static class Quote {
        String text;
        String author;

        Quote(String text, String author) {
            this.text = text;
            this.author = author;
        }
    }

    private void saveQuote(String quote) {
        SharedPreferences prefs = getSharedPreferences("saved_quotes", MODE_PRIVATE);
        Set<String> savedQuotes = new HashSet<>(prefs.getStringSet("quotes", new HashSet<>()));
        if (savedQuotes.add(quote)) {
            prefs.edit().putStringSet("quotes", savedQuotes).apply();
            Toast.makeText(this, "Quote saved!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Quote already saved", Toast.LENGTH_SHORT).show();
        }
    }
}

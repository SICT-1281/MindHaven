package com.group4.mindhaven;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private RecyclerView BookListView;  // Left side chat list
    private BookListAdapter bookListAdapter;
    private List<Book> BookList = BookDataProvider.getBooks();
    private TextView dailyQuoteTextView;
    private SharedPreferences sharedPreferences;
    private TextView dailyQuoteText;
    private FloatingActionButton newQuoteFAB;
    private FloatingActionButton saveQuoteFAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Check if user is signed in
        sharedPreferences = getSharedPreferences("MindHavenPrefs", MODE_PRIVATE);
        if (!isUserSignedIn()) {
            navigateToSignIn();
            return;
        }
        
        setContentView(R.layout.activity_main);

        setupBottomNavigation();
        setupBookList();
        setupFloatingActionButtons();
    }

    private boolean isUserSignedIn() {
        return sharedPreferences.getBoolean("isSignedIn", false);
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
                return true;
            } else if (itemId == R.id.navigation_profile) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    private void setupBookList() {
        BookListView = findViewById(R.id.BookListRecyclerView);

        // Stack items horizontally from left to right
        BookListView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
        bookListAdapter = new BookListAdapter(BookList,
                position -> {
                    Book selectedBook = BookList.get(position);
                    Intent intent = new Intent(MainActivity.this, BookDetailActivity.class);
                    intent.putExtra("bookTitle", selectedBook.getTitle());
                    intent.putExtra("bookImage", selectedBook.getImageResId());
                    intent.putExtra("bookLink", selectedBook.getLink());
                    intent.putExtra("bookAuthor", selectedBook.getName());
                    startActivity(intent);
                });
        BookListView.setAdapter(bookListAdapter);
    }

    private void setupFloatingActionButtons() {
        FloatingActionButton startChatFAB = findViewById(R.id.StartChatFAB);
        startChatFAB.setContentDescription("Start a new chat");
        startChatFAB.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ChatActivity.class);
            startActivity(intent);
        });

        setupDailyQuote();
    }

    private void setupDailyQuote() {
        dailyQuoteText = findViewById(R.id.dailyQuoteText);
        newQuoteFAB = findViewById(R.id.newQuoteFAB);
        saveQuoteFAB = findViewById(R.id.saveQuoteFAB);

        // 初始化时显示随机引用
        showRandomQuote();

        newQuoteFAB.setOnClickListener(v -> showRandomQuote());

        saveQuoteFAB.setOnClickListener(v -> {
            String currentQuote = dailyQuoteText.getText().toString();
            if (!currentQuote.equals("Click to get inspiration!")) {
                saveQuote(currentQuote);
            }
        });
    }

    private void showRandomQuote() {
        String[] quotes = {
            "The only way to do great work is to love what you do. - Steve Jobs",
            "Believe you can and you're halfway there. - Theodore Roosevelt",
            "It does not matter how slowly you go as long as you do not stop. - Confucius",
            "Success is not final, failure is not fatal: it is the courage to continue that counts. - Winston Churchill",
            "The future belongs to those who believe in the beauty of their dreams. - Eleanor Roosevelt",
            "Happiness is not something ready made. It comes from your own actions. - Dalai Lama",
            "The only limit to our realization of tomorrow is our doubts of today. - Franklin D. Roosevelt",
            "You are never too old to set another goal or to dream a new dream. - C.S. Lewis",
            "The best way to predict the future is to create it. - Peter Drucker",
            "Life is 10% what happens to us and 90% how we react to it. - Charles R. Swindoll"
        };

        Random random = new Random();
        int index = random.nextInt(quotes.length);
        dailyQuoteText.setText(quotes[index]);
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

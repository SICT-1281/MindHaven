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

        FloatingActionButton newQuoteFAB = findViewById(R.id.newQuoteFAB);
        FloatingActionButton saveQuoteFAB = findViewById(R.id.saveQuoteFAB);
        TextView quoteText = findViewById(R.id.dailyQuoteText);

        newQuoteFAB.setOnClickListener(v -> {
            fetchDailyQuote();
        });

        saveQuoteFAB.setOnClickListener(v -> {
            String currentQuote = quoteText.getText().toString();
            if (!currentQuote.equals("Click to get inspiration!")) {
                SharedPreferences prefs = getSharedPreferences("saved_quotes", MODE_PRIVATE);
                Set<String> savedQuotes = new HashSet<>(prefs.getStringSet("quotes", new HashSet<>()));
                if (savedQuotes.add(currentQuote)) {
                    prefs.edit().putStringSet("quotes", savedQuotes).apply();
                    Toast.makeText(this, "Quote saved!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Quote already saved", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchDailyQuote() {
        dailyQuoteTextView = findViewById(R.id.dailyQuoteText);
        dailyQuoteTextView.setMovementMethod(new ScrollingMovementMethod());
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://zenquotes.io/api/random")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> dailyQuoteTextView.setText("Failed to load quote."));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseData = response.body().string();
                    try {
                        JSONArray jsonArray = new JSONArray(responseData);
                        JSONObject quoteObject = jsonArray.getJSONObject(0);
                        String quote = quoteObject.getString("q");
                        String author = quoteObject.getString("a");
                        String fullQuote = quote + "\n\n— " + author;

                        runOnUiThread(() -> dailyQuoteTextView.setText(fullQuote));
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() -> dailyQuoteTextView.setText("Error parsing quote."));
                    }
                } else {
                    runOnUiThread(() -> dailyQuoteTextView.setText("Failed to load quote."));
                }
            }
        });
    }
}

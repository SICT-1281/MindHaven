package com.group4.mindhaven;
import android.content.Intent;
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
import java.util.List;
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

    TextView dailyQuoteTextView;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
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

        FloatingActionButton startChatFAB = findViewById(R.id.StartChatFAB);
        startChatFAB.setContentDescription("Start a new chat");
        startChatFAB.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ChatActivity.class);
            startActivity(intent);
        });

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

        FloatingActionButton dailyQuoteFAB = findViewById(R.id.newQuoteFAB);
        dailyQuoteFAB.setOnClickListener(v -> {
            fetchDailyQuote();
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

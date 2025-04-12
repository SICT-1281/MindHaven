package com.group4.mindhaven;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SavedBooksActivity extends AppCompatActivity {
    private RecyclerView savedBooksRecyclerView;
    private BookListAdapter bookListAdapter;
    private List<Book> savedBooks = new ArrayList<>();
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_books);

        sharedPreferences = getSharedPreferences("MindHavenPrefs", MODE_PRIVATE);
        savedBooksRecyclerView = findViewById(R.id.savedBooksRecyclerView);

        // Load saved books
        loadSavedBooks();

        // Setup RecyclerView
        savedBooksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookListAdapter = new BookListAdapter(savedBooks, position -> {
            Book selectedBook = savedBooks.get(position);
            Intent intent = new Intent(SavedBooksActivity.this, BookDetailActivity.class);
            intent.putExtra("bookTitle", selectedBook.getTitle());
            intent.putExtra("bookImage", selectedBook.getImageResId());
            intent.putExtra("bookLink", selectedBook.getLink());
            intent.putExtra("bookAuthor", selectedBook.getName());
            startActivity(intent);
        });
        savedBooksRecyclerView.setAdapter(bookListAdapter);
    }

    private void loadSavedBooks() {
        String savedBooksJson = sharedPreferences.getString("savedBooks", "[]");
        Type type = new TypeToken<List<Book>>(){}.getType();
        savedBooks = new Gson().fromJson(savedBooksJson, type);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSavedBooks();
        bookListAdapter.notifyDataSetChanged();
    }
} 
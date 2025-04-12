package com.group4.mindhaven;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import static android.content.Context.MODE_PRIVATE;

public class SavedBooksFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView emptyView;
    private ArrayList<String> savedBooks;
    private BooksAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saved_books, container, false);
        
        recyclerView = view.findViewById(R.id.savedBooksRecyclerView);
        emptyView = view.findViewById(R.id.emptyBooksView);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        loadSavedBooks();
        
        return view;
    }

    private void loadSavedBooks() {
        SharedPreferences prefs = requireContext().getSharedPreferences("saved_books", MODE_PRIVATE);
        Set<String> booksSet = prefs.getStringSet("books", new HashSet<>());
        
        savedBooks = new ArrayList<>(booksSet);
        
        updateBooksView();
    }

    private void updateBooksView() {
        if (savedBooks.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            adapter = new BooksAdapter();
            recyclerView.setAdapter(adapter);
        }
    }

    private void deleteBook(int position) {
        String bookToDelete = savedBooks.get(position);
        savedBooks.remove(position);
        
        // Update SharedPreferences
        SharedPreferences prefs = requireContext().getSharedPreferences("saved_books", MODE_PRIVATE);
        Set<String> booksSet = new HashSet<>(savedBooks);
        prefs.edit().putStringSet("books", booksSet).apply();
        
        // Show feedback
        Toast.makeText(getContext(), "Book deleted", Toast.LENGTH_SHORT).show();
        
        // Update view
        updateBooksView();
    }

    private class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.BookViewHolder> {
        @Override
        public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_saved_book, parent, false);
            return new BookViewHolder(view);
        }

        @Override
        public void onBindViewHolder(BookViewHolder holder, int position) {
            holder.bookTitle.setText(savedBooks.get(position));
            holder.deleteButton.setOnClickListener(v -> deleteBook(position));
        }

        @Override
        public int getItemCount() {
            return savedBooks.size();
        }

        class BookViewHolder extends RecyclerView.ViewHolder {
            TextView bookTitle;
            View deleteButton;

            BookViewHolder(View itemView) {
                super(itemView);
                bookTitle = itemView.findViewById(R.id.bookTitle);
                deleteButton = itemView.findViewById(R.id.deleteButton);
            }
        }
    }
} 
package com.group4.mindhaven;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import static android.content.Context.MODE_PRIVATE;

public class SavedBooksFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView emptyView;
    private List<Book> savedBooks;
    private BooksAdapter adapter;
    private SavedFragment parentFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getParentFragment() instanceof SavedFragment) {
            parentFragment = (SavedFragment) getParentFragment();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saved_books, container, false);
        
        recyclerView = view.findViewById(R.id.savedBooksRecyclerView);
        emptyView = view.findViewById(R.id.emptyBooksView);
        
        // Use GridLayoutManager to display books in a grid
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        loadSavedBooks();
        
        return view;
    }

    private void loadSavedBooks() {
        SharedPreferences prefs = requireContext().getSharedPreferences("MindHavenPrefs", MODE_PRIVATE);
        String savedBooksJson = prefs.getString("savedBooks", "[]");
        Type type = new TypeToken<List<Book>>(){}.getType();
        savedBooks = new Gson().fromJson(savedBooksJson, type);
        if (savedBooks == null) {
            savedBooks = new ArrayList<>();
        }
        
        // Log saved books for debugging
        for (Book book : savedBooks) {
            System.out.println("Saved book: " + book.getTitle() + ", Image: " + book.getImageResId());
        }
        
        updateBooksView();
        notifyParentCountChanged();
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
        savedBooks.remove(position);
        
        // Update SharedPreferences
        SharedPreferences prefs = requireContext().getSharedPreferences("MindHavenPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("savedBooks", new Gson().toJson(savedBooks));
        editor.apply();
        
        // Show feedback
        Toast.makeText(getContext(), "Book deleted", Toast.LENGTH_SHORT).show();
        
        // Update view
        updateBooksView();
        notifyParentCountChanged();
    }

    private void notifyParentCountChanged() {
        if (parentFragment != null) {
            parentFragment.updateSavedCount(0);
        }
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
            Book book = savedBooks.get(position);
            holder.bookTitle.setText(book.getTitle());
            holder.bookAuthor.setText(book.getName());
            
            // Load book image
            int imageResId = book.getImageResId();
            if (imageResId != 0) {
                holder.bookImage.setImageResource(imageResId);
            } else {
                holder.bookImage.setImageResource(R.drawable.book_placeholder_background);
            }
            
            // Set click listener for the entire item
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), BookDetailActivity.class);
                intent.putExtra("bookTitle", book.getTitle());
                intent.putExtra("bookImage", book.getImageResId());
                intent.putExtra("bookLink", book.getLink());
                intent.putExtra("bookAuthor", book.getName());
                startActivity(intent);
            });
            
            holder.deleteButton.setOnClickListener(v -> deleteBook(position));
        }

        @Override
        public int getItemCount() {
            return savedBooks.size();
        }

        class BookViewHolder extends RecyclerView.ViewHolder {
            TextView bookTitle;
            TextView bookAuthor;
            ImageView bookImage;
            View deleteButton;

            BookViewHolder(View itemView) {
                super(itemView);
                bookTitle = itemView.findViewById(R.id.bookTitle);
                bookAuthor = itemView.findViewById(R.id.bookAuthor);
                bookImage = itemView.findViewById(R.id.bookImage);
                deleteButton = itemView.findViewById(R.id.deleteButton);
            }
        }
    }
} 
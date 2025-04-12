package com.group4.mindhaven;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// Adapter class for displaying the list of books
public class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.BookViewHolder> {
    // List of all books to display
    private List<Book> BookList;

    // Listener for handling click events on each chat item
    private OnBookClickListener listener;

    // Define an interface (contract) for click events
    // The definition of this method is handed to Main Activity in our design
    public interface OnBookClickListener {
        void onBookClick(int position);
    }

    // Constructor for the adapter
    public BookListAdapter(List<Book> BookList, OnBookClickListener listener) {
        this.BookList = BookList;         // Assign the provided list to the internal list
        this.listener = listener;         // Assign the click listener to internal listener
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate a item_book layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);

        // Create and return a new ViewHolder containing this view
        return new BookViewHolder(view);
    }

    // ViewHolder class holds the views for each list item (improves performance)
    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        holder.imageView.setImageResource(getBook(position).getImageResId());
        holder.titleView.setText(getBook(position).getTitle());
        holder.itemView.setOnClickListener(v -> {
            listener.onBookClick(position);
        });
    }

    @Override
    public int getItemCount() {
        // Return the total number of chat sessions
        return BookList.size();
    }

    public Book getBook(int position) {
        return BookList.get(position);
    }



    // ViewHolder class holds the views for each list item (improves performance)
    public static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleView;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.bookCover);
            titleView = itemView.findViewById(R.id.bookTitle);
        }
    }
}
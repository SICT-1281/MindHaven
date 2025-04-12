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

public class SavedQuotesFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView emptyView;
    private ArrayList<String> savedQuotes;
    private QuotesAdapter adapter;
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
        View view = inflater.inflate(R.layout.fragment_saved_quotes, container, false);
        
        recyclerView = view.findViewById(R.id.savedQuotesRecyclerView);
        emptyView = view.findViewById(R.id.emptyQuotesView);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        loadSavedQuotes();
        
        return view;
    }

    private void loadSavedQuotes() {
        SharedPreferences prefs = requireContext().getSharedPreferences("saved_quotes", MODE_PRIVATE);
        Set<String> quotesSet = prefs.getStringSet("quotes", new HashSet<>());
        
        savedQuotes = new ArrayList<>(quotesSet);
        
        updateQuotesView();
        notifyParentCountChanged();
    }

    private void updateQuotesView() {
        if (savedQuotes.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            adapter = new QuotesAdapter();
            recyclerView.setAdapter(adapter);
        }
    }

    private void deleteQuote(int position) {
        String quoteToDelete = savedQuotes.get(position);
        savedQuotes.remove(position);
        
        // Update SharedPreferences
        SharedPreferences prefs = requireContext().getSharedPreferences("saved_quotes", MODE_PRIVATE);
        Set<String> quotesSet = new HashSet<>(savedQuotes);
        prefs.edit().putStringSet("quotes", quotesSet).apply();
        
        // Show feedback
        Toast.makeText(getContext(), "Quote deleted", Toast.LENGTH_SHORT).show();
        
        // Update view
        updateQuotesView();
        notifyParentCountChanged();
    }

    private void notifyParentCountChanged() {
        if (parentFragment != null) {
            parentFragment.updateSavedCount(1);
        }
    }

    private class QuotesAdapter extends RecyclerView.Adapter<QuotesAdapter.QuoteViewHolder> {
        @Override
        public QuoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_saved_quote, parent, false);
            return new QuoteViewHolder(view);
        }

        @Override
        public void onBindViewHolder(QuoteViewHolder holder, int position) {
            holder.quoteText.setText(savedQuotes.get(position));
            holder.deleteButton.setOnClickListener(v -> deleteQuote(position));
        }

        @Override
        public int getItemCount() {
            return savedQuotes.size();
        }

        class QuoteViewHolder extends RecyclerView.ViewHolder {
            TextView quoteText;
            View deleteButton;

            QuoteViewHolder(View itemView) {
                super(itemView);
                quoteText = itemView.findViewById(R.id.quoteText);
                deleteButton = itemView.findViewById(R.id.deleteButton);
            }
        }
    }
} 
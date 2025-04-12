package com.group4.mindhaven;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static android.content.Context.MODE_PRIVATE;

public class SavedFragment extends Fragment {
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private SavedPagerAdapter pagerAdapter;
    private TextView savedCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saved, container, false);
        
        viewPager = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tabLayout);
        savedCount = view.findViewById(R.id.savedCount);
        
        pagerAdapter = new SavedPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        
        new TabLayoutMediator(tabLayout, viewPager,
            (tab, position) -> {
                tab.setText(position == 0 ? "Books" : "Quotes");
            }
        ).attach();

        // Update saved count when tab changes
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateSavedCount(position);
            }
        });
        
        // Initial count update
        updateSavedCount(0);
        
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateSavedCount(viewPager.getCurrentItem());
    }

    public void updateSavedCount(int position) {
        int count;
        if (position == 0) {
            // Count saved books
            SharedPreferences prefs = requireContext().getSharedPreferences("MindHavenPrefs", MODE_PRIVATE);
            String savedBooksJson = prefs.getString("savedBooks", "[]");
            Type type = new TypeToken<List<Book>>(){}.getType();
            List<Book> savedBooks = new Gson().fromJson(savedBooksJson, type);
            count = savedBooks != null ? savedBooks.size() : 0;
        } else {
            // Count saved quotes
            SharedPreferences prefs = requireContext().getSharedPreferences("saved_quotes", MODE_PRIVATE);
            Set<String> quotesSet = prefs.getStringSet("quotes", new HashSet<>());
            count = quotesSet.size();
        }
        
        savedCount.setText("(" + count + ")");
    }
} 
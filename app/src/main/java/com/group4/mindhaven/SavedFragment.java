package com.group4.mindhaven;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class SavedFragment extends Fragment {
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private SavedPagerAdapter pagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saved, container, false);
        
        viewPager = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tabLayout);
        
        pagerAdapter = new SavedPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        
        new TabLayoutMediator(tabLayout, viewPager,
            (tab, position) -> {
                tab.setText(position == 0 ? "Books" : "Quotes");
            }
        ).attach();
        
        return view;
    }
} 
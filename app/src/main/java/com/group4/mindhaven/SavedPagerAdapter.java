package com.group4.mindhaven;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class SavedPagerAdapter extends FragmentStateAdapter {
    public SavedPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return position == 0 ? new SavedBooksFragment() : new SavedQuotesFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
} 
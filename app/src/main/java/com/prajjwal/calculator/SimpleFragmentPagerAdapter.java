package com.prajjwal.calculator;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

    public SimpleFragmentPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new ScientificCalculator();
        }

        else if (position == 1){
            return new SimpleCalculator();
        }
        else {
            return new History();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}

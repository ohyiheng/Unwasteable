package my.edu.utar.unwasteable.ui;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class AddPagerAdapter extends FragmentStateAdapter {
    public AddPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new LocationAddFragment();
            case 2:
                return new CategoryAddFragment();
            default:
                return new ItemAddFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

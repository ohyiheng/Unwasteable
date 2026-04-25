package my.edu.utar.unwasteable.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import java.time.LocalDate;
import java.util.List;

import my.edu.utar.unwasteable.R;
import my.edu.utar.unwasteable.data.Item;
import my.edu.utar.unwasteable.viewmodel.ItemViewModel;

public class HomeFragment extends Fragment {

    private TextView tvHomePantryCount;
    private TextView tvHomeSoonCount;
    private TextView tvHomeExpiredCount;
    private TextView tvHomeUnknownCount;
    private TextView tvHomeTipBody;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        bindViews(view);
        setupCardNavigation(view);
        setupDashboardData();

        return view;
    }

    private void bindViews(View view) {
        tvHomePantryCount = view.findViewById(R.id.tvHomePantryCount);
        tvHomeSoonCount = view.findViewById(R.id.tvHomeSoonCount);
        tvHomeExpiredCount = view.findViewById(R.id.tvHomeExpiredCount);
        tvHomeUnknownCount = view.findViewById(R.id.tvHomeUnknownCount);
        tvHomeTipBody = view.findViewById(R.id.tvHomeTipBody);
    }

    private void setupCardNavigation(View view) {
        view.findViewById(R.id.cardHomePantry).setOnClickListener(v ->
                openPantryWithFilter(view, ItemListFragment.FILTER_ALL)
        );

        view.findViewById(R.id.cardHomeSoon).setOnClickListener(v ->
                openPantryWithFilter(view, ItemListFragment.FILTER_SOON)
        );

        view.findViewById(R.id.cardHomeExpired).setOnClickListener(v ->
                openPantryWithFilter(view, ItemListFragment.FILTER_EXPIRED)
        );

        view.findViewById(R.id.cardHomeUnknown).setOnClickListener(v ->
                openPantryWithFilter(view, ItemListFragment.FILTER_UNKNOWN)
        );
    }

    private void openPantryWithFilter(View view, String filter) {
        Bundle args = new Bundle();
        args.putString(ItemListFragment.ARG_INITIAL_FILTER, filter);

        Navigation.findNavController(view).navigate(R.id.item_list, args);
    }

    private void setupDashboardData() {
        ItemViewModel itemViewModel = new ViewModelProvider(this).get(ItemViewModel.class);
        itemViewModel.getAllItems().observe(getViewLifecycleOwner(), this::updateDashboard);
    }

    private void updateDashboard(List<Item> items) {
        int totalCount = items == null ? 0 : items.size();
        int soonCount = 0;
        int expiredCount = 0;
        int unknownCount = 0;

        if (items != null) {
            LocalDate today = LocalDate.now();

            for (Item item : items) {
                if (item.expiryDate == null) {
                    unknownCount++;
                } else if (item.expiryDate.isBefore(today)) {
                    expiredCount++;
                } else if (!item.expiryDate.isAfter(today.plusDays(3))) {
                    soonCount++;
                }
            }
        }

        tvHomePantryCount.setText(String.valueOf(totalCount));
        tvHomeSoonCount.setText(String.valueOf(soonCount));
        tvHomeExpiredCount.setText(String.valueOf(expiredCount));
        tvHomeUnknownCount.setText(String.valueOf(unknownCount));

        updateFoodSavingTip(soonCount, expiredCount);
    }

    private void updateFoodSavingTip(int soonCount, int expiredCount) {
        if (expiredCount > 0) {
            tvHomeTipBody.setText(
                    getResources().getQuantityString(
                            R.plurals.home_tip_expired,
                            expiredCount,
                            expiredCount
                    )
            );
            return;
        }

        if (soonCount > 0) {
            tvHomeTipBody.setText(
                    getResources().getQuantityString(
                            R.plurals.home_tip_soon,
                            soonCount,
                            soonCount
                    )
            );
            return;
        }

        tvHomeTipBody.setText(R.string.home_tip_body_good);
    }
}
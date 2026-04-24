package my.edu.utar.unwasteable.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import my.edu.utar.unwasteable.R;
import my.edu.utar.unwasteable.data.Item;
import my.edu.utar.unwasteable.viewmodel.ItemViewModel;

public class ItemListFragment extends Fragment {

    private static final String FILTER_ALL = "all";
    private static final String FILTER_FRESH = "fresh";
    private static final String FILTER_SOON = "soon";
    private static final String FILTER_EXPIRED = "expired";
    private static final String FILTER_UNKNOWN = "unknown";

    private ItemViewModel itemViewModel;
    private ItemAdapter itemAdapter;

    private RecyclerView recyclerView;
    private View emptyStateContainer;
    private TextView tvPantryItemCount;
    private TextView tvEmptyStateTitle;
    private TextView tvEmptyStateBody;
    private Button buttonAddFirstItem;
    private TextInputEditText editSearchItems;

    private final List<Item> allItems = new ArrayList<>();
    private String selectedFilter = FILTER_ALL;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_items);
        emptyStateContainer = view.findViewById(R.id.emptyStateContainer);
        tvPantryItemCount = view.findViewById(R.id.tvPantryItemCount);
        tvEmptyStateTitle = view.findViewById(R.id.tvEmptyStateTitle);
        tvEmptyStateBody = view.findViewById(R.id.tvEmptyStateBody);
        buttonAddFirstItem = view.findViewById(R.id.button_add_first_item);
        editSearchItems = view.findViewById(R.id.edit_search_items);

        buttonAddFirstItem.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.add)
        );

        setupRecyclerView();
        setupSearch();
        setupFilterChips(view);
        setupViewModel();

        return view;
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        itemAdapter = new ItemAdapter(this::showDeleteConfirmation);
        recyclerView.setAdapter(itemAdapter);
    }

    private void setupSearch() {
        editSearchItems.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // No action needed
            }
        });
    }

    private void setupFilterChips(View view) {
        Chip chipAll = view.findViewById(R.id.chip_filter_all);
        Chip chipFresh = view.findViewById(R.id.chip_filter_fresh);
        Chip chipSoon = view.findViewById(R.id.chip_filter_soon);
        Chip chipExpired = view.findViewById(R.id.chip_filter_expired);
        Chip chipUnknown = view.findViewById(R.id.chip_filter_unknown);

        chipAll.setOnClickListener(v -> {
            selectedFilter = FILTER_ALL;
            applyFilters();
        });

        chipFresh.setOnClickListener(v -> {
            selectedFilter = FILTER_FRESH;
            applyFilters();
        });

        chipSoon.setOnClickListener(v -> {
            selectedFilter = FILTER_SOON;
            applyFilters();
        });

        chipExpired.setOnClickListener(v -> {
            selectedFilter = FILTER_EXPIRED;
            applyFilters();
        });

        chipUnknown.setOnClickListener(v -> {
            selectedFilter = FILTER_UNKNOWN;
            applyFilters();
        });
    }

    private void setupViewModel() {
        itemViewModel = new ViewModelProvider(this).get(ItemViewModel.class);
        itemViewModel.getAllItems().observe(getViewLifecycleOwner(), items -> {
            allItems.clear();

            if (items != null) {
                allItems.addAll(items);
            }

            applyFilters();
        });
    }

    private void applyFilters() {
        String query = getSearchQuery();
        List<Item> filteredItems = new ArrayList<>();

        for (Item item : allItems) {
            if (matchesSearch(item, query) && matchesStatusFilter(item)) {
                filteredItems.add(item);
            }
        }

        itemAdapter.setLocalItems(filteredItems);
        updatePantryState(filteredItems);
    }

    private String getSearchQuery() {
        if (editSearchItems.getText() == null) {
            return "";
        }

        return editSearchItems.getText()
                .toString()
                .trim()
                .toLowerCase(Locale.ROOT);
    }

    private boolean matchesSearch(Item item, String query) {
        if (query.isEmpty()) {
            return true;
        }

        String name = item.name == null ? "" : item.name.toLowerCase(Locale.ROOT);
        String quantity = String.valueOf(item.quantity).toLowerCase(Locale.ROOT);
        String expiryDate = item.expiryDate == null ? "" : item.expiryDate.toString().toLowerCase(Locale.ROOT);

        return name.contains(query)
                || quantity.contains(query)
                || expiryDate.contains(query);
    }

    private boolean matchesStatusFilter(Item item) {
        if (FILTER_ALL.equals(selectedFilter)) {
            return true;
        }

        return selectedFilter.equals(getItemStatus(item.expiryDate));
    }

    private String getItemStatus(LocalDate expiryDate) {
        if (expiryDate == null) {
            return FILTER_UNKNOWN;
        }

        LocalDate today = LocalDate.now();

        if (expiryDate.isBefore(today)) {
            return FILTER_EXPIRED;
        }

        if (!expiryDate.isAfter(today.plusDays(3))) {
            return FILTER_SOON;
        }

        return FILTER_FRESH;
    }

    private void updatePantryState(List<Item> filteredItems) {
        int totalCount = allItems.size();
        int visibleCount = filteredItems == null ? 0 : filteredItems.size();

        updateCountText(totalCount, visibleCount);

        if (totalCount == 0) {
            showEmptyPantryState();
            return;
        }

        if (visibleCount == 0) {
            showNoMatchingResultState();
            return;
        }

        recyclerView.setVisibility(View.VISIBLE);
        emptyStateContainer.setVisibility(View.GONE);
    }

    private void updateCountText(int totalCount, int visibleCount) {
        boolean isFiltering = !FILTER_ALL.equals(selectedFilter) || !getSearchQuery().isEmpty();

        if (isFiltering && totalCount > 0) {
            tvPantryItemCount.setText(
                    getString(R.string.pantry_filtered_count, visibleCount, totalCount)
            );
            return;
        }

        tvPantryItemCount.setText(
                getResources().getQuantityString(
                        R.plurals.pantry_item_count,
                        totalCount,
                        totalCount
                )
        );
    }

    private void showEmptyPantryState() {
        recyclerView.setVisibility(View.GONE);
        emptyStateContainer.setVisibility(View.VISIBLE);

        tvEmptyStateTitle.setText(R.string.pantry_empty_title);
        tvEmptyStateBody.setText(R.string.pantry_empty_body);
        buttonAddFirstItem.setVisibility(View.VISIBLE);
    }

    private void showNoMatchingResultState() {
        recyclerView.setVisibility(View.GONE);
        emptyStateContainer.setVisibility(View.VISIBLE);

        tvEmptyStateTitle.setText(R.string.pantry_no_result_title);
        tvEmptyStateBody.setText(R.string.pantry_no_result_body);
        buttonAddFirstItem.setVisibility(View.GONE);
    }

    private void showDeleteConfirmation(Item item) {
        if (item == null) {
            return;
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.delete_item_title)
                .setMessage(R.string.delete_item_message)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    itemViewModel.delete(item);
                    Toast.makeText(getContext(), R.string.item_deleted, Toast.LENGTH_SHORT).show();
                })
                .show();
    }
}
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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

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

        itemAdapter = new ItemAdapter(new ItemAdapter.OnItemActionListener() {
            @Override
            public void onEdit(Item item) {
                showEditItemDialog(item);
            }

            @Override
            public void onDelete(Item item) {
                showDeleteConfirmation(item);
            }
        });

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
        String locationName = item.locationName == null ? "" : item.locationName.toLowerCase(Locale.ROOT);
        String categoryName = item.categoryName == null ? "" : item.categoryName.toLowerCase(Locale.ROOT);

        return name.contains(query)
                || quantity.contains(query)
                || expiryDate.contains(query)
                || locationName.contains(query)
                || categoryName.contains(query);
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

    private void showEditItemDialog(Item item) {
        if (item == null) {
            return;
        }

        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_edit_item, null, false);

        TextInputEditText editName = dialogView.findViewById(R.id.edit_item_name);
        TextInputEditText editQuantity = dialogView.findViewById(R.id.edit_item_quantity);
        TextInputEditText editExpiryDate = dialogView.findViewById(R.id.edit_item_expiry_date);
        TextInputEditText editLocationName = dialogView.findViewById(R.id.edit_item_location_name);
        TextInputEditText editCategoryName = dialogView.findViewById(R.id.edit_item_category_name);

        editName.setText(item.name == null ? "" : item.name);
        editQuantity.setText(formatQuantityForEdit(item.quantity));
        editLocationName.setText(item.locationName == null ? "" : item.locationName);
        editCategoryName.setText(item.categoryName == null ? "" : item.categoryName);

        if (item.expiryDate != null) {
            editExpiryDate.setText(item.expiryDate.toString());
        } else {
            editExpiryDate.setText("");
        }

        editExpiryDate.setOnClickListener(v -> showDatePicker(editExpiryDate));

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.edit_item_title)
                .setView(dialogView)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.update, null)
                .create();

        dialog.setOnShowListener(dialogInterface ->
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                    boolean updated = updateItemFromDialog(
                            item,
                            editName,
                            editQuantity,
                            editExpiryDate,
                            editLocationName,
                            editCategoryName
                    );

                    if (updated) {
                        dialog.dismiss();
                    }
                })
        );

        dialog.show();
    }

    private boolean updateItemFromDialog(
            Item item,
            TextInputEditText editName,
            TextInputEditText editQuantity,
            TextInputEditText editExpiryDate,
            TextInputEditText editLocationName,
            TextInputEditText editCategoryName
    ) {
        editName.setError(null);
        editQuantity.setError(null);
        editExpiryDate.setError(null);
        editLocationName.setError(null);
        editCategoryName.setError(null);

        String name = getDialogText(editName);
        String quantityText = getDialogText(editQuantity);
        String expiryText = getDialogText(editExpiryDate);
        String locationName = getDialogText(editLocationName);
        String categoryName = getDialogText(editCategoryName);

        if (name.isEmpty()) {
            editName.setError(getString(R.string.error_item_name_required));
            editName.requestFocus();
            return false;
        }

        if (quantityText.isEmpty()) {
            editQuantity.setError(getString(R.string.error_quantity_required));
            editQuantity.requestFocus();
            return false;
        }

        double quantity;

        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException e) {
            editQuantity.setError(getString(R.string.error_invalid_quantity));
            editQuantity.requestFocus();
            return false;
        }

        if (quantity <= 0) {
            editQuantity.setError(getString(R.string.error_quantity_positive));
            editQuantity.requestFocus();
            return false;
        }

        LocalDate expiryDate = null;

        if (!expiryText.isEmpty()) {
            try {
                expiryDate = LocalDate.parse(expiryText);
            } catch (DateTimeParseException e) {
                editExpiryDate.setError(getString(R.string.error_invalid_date_format));
                editExpiryDate.requestFocus();
                return false;
            }
        }

        item.name = name;
        item.quantity = quantity;
        item.expiryDate = expiryDate;
        item.locationName = locationName.isEmpty() ? null : locationName;
        item.categoryName = categoryName.isEmpty() ? null : categoryName;

        itemViewModel.update(item);
        Toast.makeText(getContext(), R.string.item_updated, Toast.LENGTH_SHORT).show();

        return true;
    }

    private String getDialogText(TextInputEditText editText) {
        return editText.getText() != null
                ? editText.getText().toString().trim()
                : "";
    }

    private void showDatePicker(TextInputEditText targetEditText) {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.expiry_date_hint))
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.show(getParentFragmentManager(), "EDIT_ITEM_DATE_PICKER");

        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            String formattedDate = dateFormat.format(new Date(selection));
            targetEditText.setText(formattedDate);
            targetEditText.setError(null);
        });

        datePicker.addOnDismissListener(dialog -> targetEditText.clearFocus());
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

    private String formatQuantityForEdit(double quantity) {
        if (quantity == (int) quantity) {
            return String.valueOf((int) quantity);
        }

        return String.valueOf(quantity);
    }
}
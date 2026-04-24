package my.edu.utar.unwasteable.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import my.edu.utar.unwasteable.R;
import my.edu.utar.unwasteable.data.Category;
import my.edu.utar.unwasteable.data.Item;
import my.edu.utar.unwasteable.data.Location;
import my.edu.utar.unwasteable.viewmodel.CategoryViewModel;
import my.edu.utar.unwasteable.viewmodel.ItemViewModel;
import my.edu.utar.unwasteable.viewmodel.LocationViewModel;

public class AddFragment extends Fragment {

    private ItemViewModel itemViewModel;
    private LocationViewModel locationViewModel;
    private CategoryViewModel categoryViewModel;

    private TextInputEditText editName;
    private TextInputEditText editQuantity;
    private TextInputEditText editExpiryDate;
    private TextInputEditText editLocationName;
    private TextInputEditText editCategoryName;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        setupViewModels();
        bindViews(view);
        setupActions(view);

        return view;
    }

    private void setupViewModels() {
        itemViewModel = new ViewModelProvider(this).get(ItemViewModel.class);
        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
    }

    private void bindViews(View view) {
        editName = view.findViewById(R.id.edit_name);
        editQuantity = view.findViewById(R.id.edit_quantity);
        editExpiryDate = view.findViewById(R.id.edit_expiry_date);
        editLocationName = view.findViewById(R.id.edit_location_name);
        editCategoryName = view.findViewById(R.id.edit_category_name);
    }

    private void setupActions(View view) {
        editExpiryDate.setOnClickListener(v -> showDatePicker(editExpiryDate));

        Button buttonSave = view.findViewById(R.id.button_save);
        buttonSave.setOnClickListener(v -> saveEnteredDetails());
    }

    private void showDatePicker(TextInputEditText targetEditText) {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.expiry_date_hint))
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.show(getParentFragmentManager(), "ADD_ITEM_DATE_PICKER");

        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            String formattedDate = dateFormat.format(new Date(selection));
            targetEditText.setText(formattedDate);
            targetEditText.setError(null);
        });

        datePicker.addOnDismissListener(dialog -> targetEditText.clearFocus());
    }

    private void saveEnteredDetails() {
        clearAllErrors();

        String itemName = getText(editName);
        String quantityText = getText(editQuantity);
        String expiryText = getText(editExpiryDate);
        String locationName = getText(editLocationName);
        String categoryName = getText(editCategoryName);

        boolean hasItemInput = !itemName.isEmpty() || !quantityText.isEmpty();
        boolean hasOnlyExpiryDateForItem = !expiryText.isEmpty() && !hasItemInput;
        boolean hasLocationInput = !locationName.isEmpty();
        boolean hasCategoryInput = !categoryName.isEmpty();

        if (!hasItemInput && !hasLocationInput && !hasCategoryInput) {
            editName.setError(getString(R.string.error_enter_one_detail));
            editName.requestFocus();
            return;
        }

        if (hasOnlyExpiryDateForItem && !hasLocationInput && !hasCategoryInput) {
            editName.setError(getString(R.string.error_item_name_required));
            editName.requestFocus();
            return;
        }

        Item itemToSave = null;

        if (hasItemInput) {
            itemToSave = buildItemOrShowError(itemName, quantityText, expiryText);

            if (itemToSave == null) {
                return;
            }
        }

        Location locationToSave = null;
        if (hasLocationInput) {
            locationToSave = new Location();
            locationToSave.name = locationName;
        }

        Category categoryToSave = null;
        if (hasCategoryInput) {
            categoryToSave = new Category();
            categoryToSave.name = categoryName;
        }

        int savedCount = 0;

        if (itemToSave != null) {
            itemViewModel.insert(itemToSave);
            savedCount++;
        }

        if (locationToSave != null) {
            locationViewModel.insert(locationToSave);
            savedCount++;
        }

        if (categoryToSave != null) {
            categoryViewModel.insert(categoryToSave);
            savedCount++;
        }

        clearSavedFields(itemToSave != null, locationToSave != null, categoryToSave != null);

        int toastMessage = savedCount == 1
                ? R.string.toast_detail_saved
                : R.string.toast_details_saved;

        Toast.makeText(requireContext(), toastMessage, Toast.LENGTH_SHORT).show();
    }

    private Item buildItemOrShowError(String name, String quantityText, String expiryText) {
        if (name.isEmpty()) {
            editName.setError(getString(R.string.error_item_name_required));
            editName.requestFocus();
            return null;
        }

        if (quantityText.isEmpty()) {
            editQuantity.setError(getString(R.string.error_quantity_required));
            editQuantity.requestFocus();
            return null;
        }

        double quantity;

        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException e) {
            editQuantity.setError(getString(R.string.error_invalid_quantity));
            editQuantity.requestFocus();
            return null;
        }

        if (quantity <= 0) {
            editQuantity.setError(getString(R.string.error_quantity_positive));
            editQuantity.requestFocus();
            return null;
        }

        LocalDate expiryDate = null;

        if (!expiryText.isEmpty()) {
            try {
                expiryDate = LocalDate.parse(expiryText);
            } catch (DateTimeParseException e) {
                editExpiryDate.setError(getString(R.string.error_invalid_date_format));
                editExpiryDate.requestFocus();
                return null;
            }
        }

        Item item = new Item();
        item.name = name;
        item.quantity = quantity;
        item.expiryDate = expiryDate;

        return item;
    }

    private String getText(TextInputEditText editText) {
        return editText.getText() != null
                ? editText.getText().toString().trim()
                : "";
    }

    private void clearAllErrors() {
        editName.setError(null);
        editQuantity.setError(null);
        editExpiryDate.setError(null);
        editLocationName.setError(null);
        editCategoryName.setError(null);
    }

    private void clearSavedFields(boolean itemSaved, boolean locationSaved, boolean categorySaved) {
        if (itemSaved) {
            editName.setText("");
            editQuantity.setText("");
            editExpiryDate.setText("");
        }

        if (locationSaved) {
            editLocationName.setText("");
        }

        if (categorySaved) {
            editCategoryName.setText("");
        }

        clearAllErrors();
        editName.requestFocus();
    }
}
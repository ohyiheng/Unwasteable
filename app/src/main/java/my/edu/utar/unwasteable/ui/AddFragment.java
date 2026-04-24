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

        Button buttonSaveItem = view.findViewById(R.id.button_save);
        Button buttonSaveLocation = view.findViewById(R.id.button_save_location);
        Button buttonSaveCategory = view.findViewById(R.id.button_save_category);

        buttonSaveItem.setOnClickListener(v -> saveItem());
        buttonSaveLocation.setOnClickListener(v -> saveLocation());
        buttonSaveCategory.setOnClickListener(v -> saveCategory());
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

    private void saveItem() {
        clearItemErrors();

        String name = getText(editName);
        String quantityText = getText(editQuantity);
        String expiryText = getText(editExpiryDate);

        if (name.isEmpty()) {
            editName.setError(getString(R.string.error_item_name_required));
            editName.requestFocus();
            return;
        }

        if (quantityText.isEmpty()) {
            editQuantity.setError(getString(R.string.error_quantity_required));
            editQuantity.requestFocus();
            return;
        }

        double quantity;

        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException e) {
            editQuantity.setError(getString(R.string.error_invalid_quantity));
            editQuantity.requestFocus();
            return;
        }

        if (quantity <= 0) {
            editQuantity.setError(getString(R.string.error_quantity_positive));
            editQuantity.requestFocus();
            return;
        }

        LocalDate expiryDate = null;

        if (!expiryText.isEmpty()) {
            try {
                expiryDate = LocalDate.parse(expiryText);
            } catch (DateTimeParseException e) {
                editExpiryDate.setError(getString(R.string.error_invalid_date_format));
                editExpiryDate.requestFocus();
                return;
            }
        }

        Item item = new Item();
        item.name = name;
        item.quantity = quantity;
        item.expiryDate = expiryDate;

        itemViewModel.insert(item);

        Toast.makeText(requireContext(), "Item saved", Toast.LENGTH_SHORT).show();
        clearItemForm();
    }

    private void saveLocation() {
        editLocationName.setError(null);

        String name = getText(editLocationName);

        if (name.isEmpty()) {
            editLocationName.setError("Location name is required");
            editLocationName.requestFocus();
            return;
        }

        Location location = new Location();
        location.name = name;

        locationViewModel.insert(location);

        Toast.makeText(requireContext(), "Location saved", Toast.LENGTH_SHORT).show();
        editLocationName.setText("");
        editLocationName.setError(null);
    }

    private void saveCategory() {
        editCategoryName.setError(null);

        String name = getText(editCategoryName);

        if (name.isEmpty()) {
            editCategoryName.setError("Category name is required");
            editCategoryName.requestFocus();
            return;
        }

        Category category = new Category();
        category.name = name;

        categoryViewModel.insert(category);

        Toast.makeText(requireContext(), "Category saved", Toast.LENGTH_SHORT).show();
        editCategoryName.setText("");
        editCategoryName.setError(null);
    }

    private String getText(TextInputEditText editText) {
        return editText.getText() != null
                ? editText.getText().toString().trim()
                : "";
    }

    private void clearItemErrors() {
        editName.setError(null);
        editQuantity.setError(null);
        editExpiryDate.setError(null);
    }

    private void clearItemForm() {
        editName.setText("");
        editQuantity.setText("");
        editExpiryDate.setText("");
        clearItemErrors();
        editName.requestFocus();
    }
}
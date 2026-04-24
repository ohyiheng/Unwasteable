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
import my.edu.utar.unwasteable.data.Item;
import my.edu.utar.unwasteable.viewmodel.ItemViewModel;

public class ItemAddFragment extends Fragment {

    private ItemViewModel itemViewModel;
    private TextInputEditText editName;
    private TextInputEditText editQuantity;
    private TextInputEditText editExpiryDate;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_item_add, container, false);

        itemViewModel = new ViewModelProvider(this).get(ItemViewModel.class);

        editName = view.findViewById(R.id.edit_name);
        editQuantity = view.findViewById(R.id.edit_quantity);
        editExpiryDate = view.findViewById(R.id.edit_expiry_date);

        editExpiryDate.setOnClickListener(v -> showDatePicker());

        Button buttonSave = view.findViewById(R.id.button_save);
        buttonSave.setOnClickListener(v -> saveItem());

        return view;
    }

    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select expiry date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");

        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            String formattedDate = dateFormat.format(new Date(selection));
            editExpiryDate.setText(formattedDate);
            editExpiryDate.setError(null);
        });

        datePicker.addOnDismissListener(dialog -> editExpiryDate.clearFocus());
    }

    private void saveItem() {
        clearErrors();

        String name = editName.getText() != null
                ? editName.getText().toString().trim()
                : "";

        String quantityText = editQuantity.getText() != null
                ? editQuantity.getText().toString().trim()
                : "";

        String expiryText = editExpiryDate.getText() != null
                ? editExpiryDate.getText().toString().trim()
                : "";

        if (name.isEmpty()) {
            editName.setError("Item name is required");
            editName.requestFocus();
            return;
        }

        if (quantityText.isEmpty()) {
            editQuantity.setError("Quantity is required");
            editQuantity.requestFocus();
            return;
        }

        double quantity;

        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException e) {
            editQuantity.setError("Enter a valid quantity");
            editQuantity.requestFocus();
            return;
        }

        if (quantity <= 0) {
            editQuantity.setError("Quantity must be greater than 0");
            editQuantity.requestFocus();
            return;
        }

        LocalDate expiryDate = null;

        if (!expiryText.isEmpty()) {
            try {
                expiryDate = LocalDate.parse(expiryText);
            } catch (DateTimeParseException e) {
                editExpiryDate.setError("Use date format yyyy-MM-dd");
                editExpiryDate.requestFocus();
                return;
            }
        }

        Item item = new Item();
        item.name = name;
        item.quantity = quantity;
        item.expiryDate = expiryDate;

        itemViewModel.insert(item);

        Toast.makeText(getContext(), "Item saved", Toast.LENGTH_SHORT).show();
        clearForm();
    }

    private void clearErrors() {
        editName.setError(null);
        editQuantity.setError(null);
        editExpiryDate.setError(null);
    }

    private void clearForm() {
        editName.setText("");
        editQuantity.setText("");
        editExpiryDate.setText("");
        editName.requestFocus();
    }
}
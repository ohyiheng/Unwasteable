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
import androidx.navigation.Navigation;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import my.edu.utar.unwasteable.R;
import my.edu.utar.unwasteable.data.Item;
import my.edu.utar.unwasteable.viewmodel.ItemViewModel;

public class ItemAddFragment extends Fragment {

    private ItemViewModel itemViewModel;
    private TextInputEditText editName, editQuantity, editExpiryDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds()) // Default to today
                .build();

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");

        datePicker.addOnPositiveButtonClickListener(selection -> {
            // Format the date to a string (matching your Room Entity field)
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String formattedDate = dateFormat.format(new Date(selection));

            // Set the text in the EditText
            editExpiryDate.setText(formattedDate);
        });

        datePicker.addOnDismissListener(dialog -> {
            editExpiryDate.clearFocus();
        });
    }

    private void saveItem() {
        if (editName.getText() == null || editQuantity.getText() == null) return;

        String name = editName.getText().toString();
        String quantity = editQuantity.getText().toString();
        LocalDate expiryDate = editExpiryDate.getText() != null
                ? LocalDate.parse(editExpiryDate.getText().toString())
                : null;

        if (name.isEmpty() || quantity.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Item item = new Item();
        item.name = name;
        item.quantity = Double.parseDouble(quantity);
        item.expiryDate = expiryDate;

        itemViewModel.insert(item);
        Toast.makeText(getContext(), "Item saved", Toast.LENGTH_SHORT).show();
        Navigation.findNavController(requireView()).popBackStack();
    }
}

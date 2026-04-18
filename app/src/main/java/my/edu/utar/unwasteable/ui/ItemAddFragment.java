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

import com.google.android.material.textfield.TextInputEditText;

import my.edu.utar.unwasteable.R;
import my.edu.utar.unwasteable.data.Item;
import my.edu.utar.unwasteable.viewmodel.ItemViewModel;

public class ItemAddFragment extends Fragment {

    private ItemViewModel itemViewModel;
    private TextInputEditText editName, editQuantity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_add, container, false);

        itemViewModel = new ViewModelProvider(this).get(ItemViewModel.class);

        editName = view.findViewById(R.id.edit_name);
        editQuantity = view.findViewById(R.id.edit_quantity);

        Button buttonSave = view.findViewById(R.id.button_save);
        buttonSave.setOnClickListener(v -> saveItem());

        return view;
    }

    private void saveItem() {
        if (editName.getText() == null || editQuantity.getText() == null) return;

        String name = editName.getText().toString();
        String quantity = editQuantity.getText().toString();

        if (name.isEmpty() || quantity.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Item item = new Item();
        item.name = name;
        item.quantity = Double.parseDouble(quantity);

        itemViewModel.insert(item);
        Toast.makeText(getContext(), "Item saved", Toast.LENGTH_SHORT).show();
        Navigation.findNavController(requireView()).popBackStack();
    }
}

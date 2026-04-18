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

import com.google.android.material.textfield.TextInputEditText;

import my.edu.utar.unwasteable.R;
import my.edu.utar.unwasteable.data.Category;
import my.edu.utar.unwasteable.viewmodel.CategoryViewModel;

public class CategoryAddFragment extends Fragment {

    private CategoryViewModel categoryViewModel;
    private TextInputEditText editCategoryName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_add, container, false);

        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        editCategoryName = view.findViewById(R.id.edit_category_name);

        Button buttonSave = view.findViewById(R.id.button_save_category);
        buttonSave.setOnClickListener(v -> saveCategory());

        return view;
    }

    private void saveCategory() {
        String name = editCategoryName.getText() != null ? editCategoryName.getText().toString().trim() : "";

        if (name.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a category name", Toast.LENGTH_SHORT).show();
            return;
        }

        Category category = new Category();
        category.name = name;

        categoryViewModel.insert(category);
        Toast.makeText(getContext(), "Category saved", Toast.LENGTH_SHORT).show();

        // Clear input
        editCategoryName.setText("");
    }
}

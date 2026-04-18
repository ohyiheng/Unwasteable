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
import my.edu.utar.unwasteable.data.Location;
import my.edu.utar.unwasteable.viewmodel.LocationViewModel;

public class LocationAddFragment extends Fragment {

    private LocationViewModel locationViewModel;
    private TextInputEditText editLocationName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location_add, container, false);

        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        editLocationName = view.findViewById(R.id.edit_location_name);

        Button buttonSave = view.findViewById(R.id.button_save_location);
        buttonSave.setOnClickListener(v -> saveLocation());

        return view;
    }

    private void saveLocation() {
        String name = editLocationName.getText() != null ? editLocationName.getText().toString().trim() : "";

        if (name.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a location name", Toast.LENGTH_SHORT).show();
            return;
        }

        Location location = new Location();
        location.name = name;

        locationViewModel.insert(location);
        Toast.makeText(getContext(), "Location saved", Toast.LENGTH_SHORT).show();
        
        // Clear input
        editLocationName.setText("");
    }
}

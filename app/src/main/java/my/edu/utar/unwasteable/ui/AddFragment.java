package my.edu.utar.unwasteable.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import my.edu.utar.unwasteable.R;
import my.edu.utar.unwasteable.data.Item;
import my.edu.utar.unwasteable.viewmodel.ItemViewModel;

public class AddFragment extends Fragment {

    private ItemViewModel itemViewModel;

    private TextInputEditText editName;
    private TextInputEditText editQuantity;
    private TextInputEditText editExpiryDate;
    private TextInputEditText editLocationName;
    private TextInputEditText editCategoryName;

    private TextView tvFoodInfoResult;
    private Button buttonCheckFoodInfo;

    private final ExecutorService apiExecutor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        setupViewModel();
        bindViews(view);
        setupActions(view);

        return view;
    }

    private void setupViewModel() {
        itemViewModel = new ViewModelProvider(this).get(ItemViewModel.class);
    }

    private void bindViews(View view) {
        editName = view.findViewById(R.id.edit_name);
        editQuantity = view.findViewById(R.id.edit_quantity);
        editExpiryDate = view.findViewById(R.id.edit_expiry_date);
        editLocationName = view.findViewById(R.id.edit_location_name);
        editCategoryName = view.findViewById(R.id.edit_category_name);

        tvFoodInfoResult = view.findViewById(R.id.tv_food_info_result);
        buttonCheckFoodInfo = view.findViewById(R.id.button_check_food_info);
    }

    private void setupActions(View view) {
        editExpiryDate.setOnClickListener(v -> showDatePicker(editExpiryDate));

        buttonCheckFoodInfo.setOnClickListener(v -> checkFoodInfo());

        Button buttonSave = view.findViewById(R.id.button_save);
        buttonSave.setOnClickListener(v -> saveItem());
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

    private void checkFoodInfo() {
        String itemName = getText(editName);

        if (itemName.isEmpty()) {
            editName.setError(getString(R.string.food_info_enter_name));
            editName.requestFocus();
            return;
        }

        editName.setError(null);
        buttonCheckFoodInfo.setEnabled(false);
        tvFoodInfoResult.setText(R.string.food_info_loading);

        apiExecutor.execute(() -> {
            String result;

            try {
                result = fetchFoodInfo(itemName);
            } catch (Exception e) {
                result = getString(R.string.food_info_error);
            }

            String finalResult = result;
            mainHandler.post(() -> {
                if (!isAdded()) {
                    return;
                }

                tvFoodInfoResult.setText(finalResult);
                buttonCheckFoodInfo.setEnabled(true);
            });
        });
    }

    private String fetchFoodInfo(String itemName) throws Exception {
        String encodedTerm = URLEncoder.encode(itemName, StandardCharsets.UTF_8.toString());

        String endpoint =
                "https://world.openfoodfacts.org/cgi/search.pl"
                        + "?search_terms=" + encodedTerm
                        + "&search_simple=1"
                        + "&action=process"
                        + "&json=1"
                        + "&page_size=1";

        HttpURLConnection connection = null;

        try {
            URL url = new URL(endpoint);
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            connection.setRequestProperty("User-Agent", "Unwasteable-Android-App");

            int responseCode = connection.getResponseCode();

            if (responseCode < 200 || responseCode >= 300) {
                return getString(R.string.food_info_error);
            }

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream())
            );

            StringBuilder responseBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
            }

            reader.close();

            return parseFoodInfoResponse(responseBuilder.toString(), itemName);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String parseFoodInfoResponse(String response, String itemName) throws Exception {
        JSONObject root = new JSONObject(response);
        JSONArray products = root.optJSONArray("products");

        if (products == null || products.length() == 0) {
            return getString(R.string.food_info_no_result, itemName);
        }

        JSONObject product = products.getJSONObject(0);

        String productName = product.optString("product_name", "").trim();
        String brands = product.optString("brands", "").trim();
        String categories = product.optString("categories", "").trim();

        if (productName.isEmpty()) {
            productName = itemName;
        }

        if (brands.isEmpty()) {
            brands = getString(R.string.food_info_not_available);
        }

        if (categories.isEmpty()) {
            categories = getString(R.string.food_info_not_available);
        }

        return getString(
                R.string.food_info_result,
                productName,
                brands,
                categories
        );
    }

    private void saveItem() {
        clearAllErrors();

        String itemName = getText(editName);
        String quantityText = getText(editQuantity);
        String expiryText = getText(editExpiryDate);
        String locationName = getText(editLocationName);
        String categoryName = getText(editCategoryName);

        Item itemToSave = buildItemOrShowError(itemName, quantityText, expiryText);

        if (itemToSave == null) {
            return;
        }

        itemToSave.locationName = locationName.isEmpty() ? null : locationName;
        itemToSave.categoryName = categoryName.isEmpty() ? null : categoryName;

        checkDuplicateBeforeSaving(itemToSave);
    }

    private void checkDuplicateBeforeSaving(Item itemToSave) {
        itemViewModel.findItemByName(itemToSave.name, existingItem -> {
            if (!isAdded()) {
                return;
            }

            if (existingItem == null) {
                insertNewItem(itemToSave);
                return;
            }

            showDuplicateItemDialog(itemToSave, existingItem);
        });
    }

    private void showDuplicateItemDialog(Item itemToSave, Item existingItem) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.duplicate_item_title)
                .setMessage(getString(R.string.duplicate_item_message, existingItem.name))
                .setNegativeButton(R.string.cancel, null)
                .setNeutralButton(R.string.add_anyway, (dialog, which) -> insertNewItem(itemToSave))
                .setPositiveButton(R.string.update_existing, (dialog, which) ->
                        updateExistingItem(itemToSave, existingItem)
                )
                .show();
    }

    private void insertNewItem(Item itemToSave) {
        itemViewModel.insert(itemToSave);
        clearSavedFields();

        Toast.makeText(requireContext(), R.string.toast_detail_saved, Toast.LENGTH_SHORT).show();
    }

    private void updateExistingItem(Item itemToSave, Item existingItem) {
        itemViewModel.insertOrUpdateExisting(itemToSave, existingItem);
        clearSavedFields();

        Toast.makeText(requireContext(), R.string.item_updated, Toast.LENGTH_SHORT).show();
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

    private void clearSavedFields() {
        editName.setText("");
        editQuantity.setText("");
        editExpiryDate.setText("");
        editLocationName.setText("");
        editCategoryName.setText("");

        tvFoodInfoResult.setText(R.string.food_info_initial);

        clearAllErrors();
        editName.requestFocus();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        apiExecutor.shutdownNow();
    }
}
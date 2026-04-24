package my.edu.utar.unwasteable.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import my.edu.utar.unwasteable.R;
import my.edu.utar.unwasteable.data.Item;
import my.edu.utar.unwasteable.viewmodel.ItemViewModel;

public class ItemListFragment extends Fragment {

    private ItemViewModel itemViewModel;

    private RecyclerView recyclerView;
    private View emptyStateContainer;
    private TextView tvPantryItemCount;

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

        Button buttonAddFirstItem = view.findViewById(R.id.button_add_first_item);
        buttonAddFirstItem.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.add)
        );

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        final ItemAdapter itemAdapter = new ItemAdapter();
        recyclerView.setAdapter(itemAdapter);

        itemViewModel = new ViewModelProvider(this).get(ItemViewModel.class);
        itemViewModel.getAllItems().observe(getViewLifecycleOwner(), items -> {
            itemAdapter.setLocalItems(items);
            updatePantryState(items);
        });

        return view;
    }

    private void updatePantryState(List<Item> items) {
        int itemCount = items == null ? 0 : items.size();
        boolean isEmpty = itemCount == 0;

        tvPantryItemCount.setText(
                getResources().getQuantityString(
                        R.plurals.pantry_item_count,
                        itemCount,
                        itemCount
                )
        );

        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        emptyStateContainer.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }
}
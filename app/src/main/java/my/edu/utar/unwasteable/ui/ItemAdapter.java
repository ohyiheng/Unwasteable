package my.edu.utar.unwasteable.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import my.edu.utar.unwasteable.data.Item;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private List<Item> localItems = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewTitle;
        private final TextView textViewDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(android.R.id.text1);
            textViewDescription = itemView.findViewById(android.R.id.text2);
        }

        public TextView getTextViewTitle() {
            return textViewTitle;
        }

        public TextView getTextViewDescription() {
            return textViewDescription;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(android.R.layout.simple_list_item_2, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        Item currentItem = localItems.get(position);
        viewHolder.getTextViewTitle().setText("Name: " + currentItem.name);
        viewHolder.getTextViewDescription().setText("Qty: " + currentItem.quantity);
    }

    @Override
    public int getItemCount() {
        return localItems.size();
    }

    public void setLocalItems(List<Item> localItems) {
        this.localItems = localItems;
        notifyDataSetChanged();
    }

}

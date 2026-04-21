package my.edu.utar.unwasteable.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import my.edu.utar.unwasteable.R;
import my.edu.utar.unwasteable.data.Item;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private List<Item> localItems = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvItemName;
        private final TextView tvItemQuantity;
        private final TextView tvItemExpiry;
        private final TextView tvItemBadge;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemQuantity = itemView.findViewById(R.id.tvItemQuantity);
            tvItemExpiry = itemView.findViewById(R.id.tvItemExpiry);
            tvItemBadge = itemView.findViewById(R.id.tvItemBadge);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item currentItem = localItems.get(position);

        holder.tvItemName.setText(currentItem.name);
        holder.tvItemQuantity.setText("Quantity: " + currentItem.quantity);

        String expiryText = currentItem.expiryDate == null
                ? "Expiry date: -"
                : "Expiry date: " + currentItem.expiryDate;

        holder.tvItemExpiry.setText(expiryText);
        holder.tvItemBadge.setText(getStatusText(currentItem.expiryDate));
    }

    @Override
    public int getItemCount() {
        return localItems == null ? 0 : localItems.size();
    }

    public void setLocalItems(List<Item> localItems) {
        this.localItems = localItems;
        notifyDataSetChanged();
    }

    private String getStatusText(LocalDate expiryDate) {
        if (expiryDate == null) return "Unknown";

        LocalDate today = LocalDate.now();

        if (expiryDate.isBefore(today)) {
            return "Expired";
        } else if (!expiryDate.isAfter(today.plusDays(3))) {
            return "Soon";
        } else {
            return "Fresh";
        }
    }
}
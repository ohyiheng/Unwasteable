package my.edu.utar.unwasteable.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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
        Context context = holder.itemView.getContext();

        holder.tvItemName.setText(currentItem.name);
        holder.tvItemQuantity.setText(
                context.getString(R.string.item_quantity_format, formatQuantity(currentItem.quantity))
        );

        if (currentItem.expiryDate == null) {
            holder.tvItemExpiry.setText(context.getString(R.string.item_expiry_empty));
        } else {
            holder.tvItemExpiry.setText(
                    context.getString(R.string.item_expiry_format, currentItem.expiryDate.toString())
            );
        }

        bindBadge(holder.tvItemBadge, currentItem.expiryDate);
    }

    @Override
    public int getItemCount() {
        return localItems == null ? 0 : localItems.size();
    }

    public void setLocalItems(List<Item> localItems) {
        this.localItems = localItems;
        notifyDataSetChanged();
    }

    private void bindBadge(TextView badgeView, LocalDate expiryDate) {
        Context context = badgeView.getContext();

        if (expiryDate == null) {
            setBadge(
                    badgeView,
                    context.getString(R.string.badge_unknown),
                    R.drawable.bg_status_unknown,
                    R.color.text_secondary
            );
            return;
        }

        LocalDate today = LocalDate.now();

        if (expiryDate.isBefore(today)) {
            setBadge(
                    badgeView,
                    context.getString(R.string.badge_expired),
                    R.drawable.bg_status_expired,
                    R.color.danger_text
            );
        } else if (!expiryDate.isAfter(today.plusDays(3))) {
            setBadge(
                    badgeView,
                    context.getString(R.string.badge_soon),
                    R.drawable.bg_status_soon,
                    R.color.warning_text
            );
        } else {
            setBadge(
                    badgeView,
                    context.getString(R.string.badge_fresh),
                    R.drawable.bg_status_fresh,
                    R.color.green_primary_dark
            );
        }
    }

    private void setBadge(TextView badgeView, String text, int backgroundRes, int textColorRes) {
        badgeView.setText(text);
        badgeView.setBackground(ContextCompat.getDrawable(badgeView.getContext(), backgroundRes));
        badgeView.setTextColor(ContextCompat.getColor(badgeView.getContext(), textColorRes));
    }

    private String formatQuantity(double quantity) {
        if (quantity == (int) quantity) {
            return String.valueOf((int) quantity);
        }
        return String.valueOf(quantity);
    }
}
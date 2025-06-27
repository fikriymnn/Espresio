package com.example.espresio.adapters;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.espresio.R;
import com.example.espresio.models.SaleItem;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class SaleItemAdapter extends RecyclerView.Adapter<SaleItemAdapter.SaleItemViewHolder> {
    private List<SaleItem> saleItems;
    private OnSaleItemUpdateListener updateListener;
    private OnSaleItemRemoveListener removeListener;
    private NumberFormat currencyFormat;

    public interface OnSaleItemUpdateListener {
        void onUpdateQuantity(SaleItem item, int newQuantity);
    }

    public interface OnSaleItemRemoveListener {
        void onRemoveItem(SaleItem item);
    }

    public SaleItemAdapter(List<SaleItem> saleItems,
                           OnSaleItemUpdateListener updateListener,
                           OnSaleItemRemoveListener removeListener) {
        this.saleItems = saleItems;
        this.updateListener = updateListener;
        this.removeListener = removeListener;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    }

    @NonNull
    @Override
    public SaleItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sale_item, parent, false);
        return new SaleItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SaleItemViewHolder holder, int position) {
        SaleItem item = saleItems.get(position);
        holder.tvItemName.setText(item.getProductName());
        holder.tvItemPrice.setText(currencyFormat.format(item.getPrice()));
        holder.etQuantity.setText(String.valueOf(item.getQuantity()));
        holder.tvSubtotal.setText(currencyFormat.format(item.getSubtotal()));

        holder.etQuantity.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                try {
                    int newQuantity = Integer.parseInt(holder.etQuantity.getText().toString());
                    if (newQuantity != item.getQuantity() && updateListener != null) {
                        updateListener.onUpdateQuantity(item, newQuantity);
                    }
                } catch (NumberFormatException e) {
                    holder.etQuantity.setText(String.valueOf(item.getQuantity()));
                }
            }
        });

        holder.btnRemove.setOnClickListener(v -> {
            if (removeListener != null) {
                removeListener.onRemoveItem(item);
            }
        });

        holder.btnMinus.setOnClickListener(v -> {
            int newQuantity = item.getQuantity() - 1;
            if (newQuantity > 0 && updateListener != null) {
                updateListener.onUpdateQuantity(item, newQuantity);
            }
        });

        holder.btnPlus.setOnClickListener(v -> {
            int newQuantity = item.getQuantity() + 1;
            if (updateListener != null) {
                updateListener.onUpdateQuantity(item, newQuantity);
            }
        });
    }

    @Override
    public int getItemCount() {
        return saleItems.size();
    }

    static class SaleItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvItemPrice, tvSubtotal;
        EditText etQuantity;
        Button btnRemove, btnMinus, btnPlus;

        SaleItemViewHolder(View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tv_item_name);
            tvItemPrice = itemView.findViewById(R.id.tv_item_price);
            tvSubtotal = itemView.findViewById(R.id.tv_subtotal);
            etQuantity = itemView.findViewById(R.id.et_quantity);
            btnRemove = itemView.findViewById(R.id.btn_remove);
            btnMinus = itemView.findViewById(R.id.btn_minus);
            btnPlus = itemView.findViewById(R.id.btn_plus);
        }
    }
}
package com.example.espresio.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.espresio.R;
import com.example.espresio.models.SaleItem;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class SaleDetailItemAdapter extends RecyclerView.Adapter<SaleDetailItemAdapter.ViewHolder> {
    private List<SaleItem> saleItems;
    private NumberFormat currencyFormat;

    public SaleDetailItemAdapter(List<SaleItem> saleItems) {
        this.saleItems = saleItems;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sale_detail_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SaleItem item = saleItems.get(position);

        holder.tvProductName.setText(item.getProductName());
        holder.tvUnitPrice.setText(currencyFormat.format(item.getPrice()));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
        holder.tvItemSubtotal.setText(currencyFormat.format(item.getSubtotal()));
    }

    @Override
    public int getItemCount() {
        return saleItems != null ? saleItems.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvUnitPrice, tvQuantity, tvItemSubtotal;

        ViewHolder(View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvUnitPrice = itemView.findViewById(R.id.tv_unit_price);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvItemSubtotal = itemView.findViewById(R.id.tv_item_subtotal);
        }
    }
}
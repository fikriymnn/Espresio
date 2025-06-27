package com.example.espresio.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.espresio.R;
import com.example.espresio.models.Purchase;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

//public class PurchaseReportAdapter extends RecyclerView.Adapter<PurchaseReportAdapter.PurchaseReportViewHolder> {
//    private List<Purchase> purchases;
//    private SimpleDateFormat dateFormat;
//    private NumberFormat currencyFormat;
//
//    public PurchaseReportAdapter(List<Purchase> purchases) {
//        this.purchases = purchases;
//        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
//        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
//    }
//
//    @NonNull
//    @Override
//    public PurchaseReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.item_purchase_report, parent, false);
//        return new PurchaseReportViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull PurchaseReportViewHolder holder, int position) {
//        Purchase purchase = purchases.get(position);
//
//        holder.tvPurchaseDate.setText(dateFormat.format(new Date(purchase.getPurchaseDate())));
//        holder.tvIngredientName.setText(purchase.getIngredientName());
//        holder.tvQuantity.setText(purchase.getQuantity() + " " + purchase.getUnit());
//        holder.tvSupplier.setText("Supplier: " + purchase.getSupplier());
//        holder.tvUnitPrice.setText("Unit Price: " + currencyFormat.format(purchase.getUnitPrice()));
//        holder.tvTotalPrice.setText(currencyFormat.format(purchase.getTotalPrice()));
//        holder.tvAdminName.setText("Admin: " + purchase.getAdminName());
//    }
//
//    @Override
//    public int getItemCount() {
//        return purchases.size();
//    }
//
//    static class PurchaseReportViewHolder extends RecyclerView.ViewHolder {
//        TextView tvPurchaseDate, tvIngredientName, tvQuantity, tvSupplier,
//                tvUnitPrice, tvTotalPrice, tvAdminName;
//
//        PurchaseReportViewHolder(View itemView) {
//            super(itemView);
//            tvPurchaseDate = itemView.findViewById(R.id.tv_purchase_date);
//            tvIngredientName = itemView.findViewById(R.id.tv_ingredient_name);
//            tvQuantity = itemView.findViewById(R.id.tv_quantity);
//            tvSupplier = itemView.findViewById(R.id.tv_supplier);
//            tvUnitPrice = itemView.findViewById(R.id.tv_unit_price);
//            tvTotalPrice = itemView.findViewById(R.id.tv_total_price);
//            tvAdminName = itemView.findViewById(R.id.tv_admin_name);
//        }
//    }
//}